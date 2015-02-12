/**
 * This file is part of pwt.
 *
 * pwt is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * pwt is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with pwt. If not,
 * see <http://www.gnu.org/licenses/>.
 */
package fr.putnami.gwt.gradle.task;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.DependencySet;
import org.gradle.api.artifacts.ProjectDependency;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.ConventionMapping;
import org.gradle.api.internal.IConventionAware;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.plugins.WarPluginConvention;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

import fr.putnami.gwt.gradle.PwtLibPlugin;
import fr.putnami.gwt.gradle.action.JavaAction;
import fr.putnami.gwt.gradle.extension.DevOption;
import fr.putnami.gwt.gradle.extension.JettyOption;
import fr.putnami.gwt.gradle.extension.PutnamiExtension;
import fr.putnami.gwt.gradle.util.JavaCommandBuilder;
import fr.putnami.gwt.gradle.util.ResourceUtils;

public class GwtDevTask extends AbstractJettyTask {

	public static final String NAME = "gwtDev";

	private FileCollection src;
	private List<String> modules = Lists.newArrayList();

	public GwtDevTask() {
		setName(NAME);
		setDescription("Run SDM");

		dependsOn(JavaPlugin.COMPILE_JAVA_TASK_NAME, JavaPlugin.PROCESS_RESOURCES_TASK_NAME);
	}

	@TaskAction
	public void exec() throws Exception {
		WarPluginConvention warConvention = (WarPluginConvention) getProject().getConvention().getPlugins().get("war");
		PutnamiExtension putnami = getProject().getExtensions().getByType(PutnamiExtension.class);
		DevOption sdmOption = putnami.getDev();
		JettyOption jettyOption = putnami.getJetty();

		try {
			File webOverrideFile = ResourceUtils.copy(
				"/stub.web-dev-override.xml", new File(getProject().getBuildDir(), "putnami/conf/web-dev-override.xml"),
				new ImmutableMap.Builder<String, String>()
					.put("__LAUNCHER_DIR__", sdmOption.getLauncherDir().getAbsolutePath() + "")
					.build());
			ResourceUtils.copy("/stub.jetty-dev-conf.xml", jettyOption.getJettyConf(),
				new ImmutableMap.Builder<String, String>()
					.put("__WEB_OVERRIDE__", webOverrideFile.getAbsolutePath())
					.put("__WAR_FILE__", warConvention.getWebAppDir().getAbsolutePath())
					.build());

		} catch (IOException e) {
			Throwables.propagate(e);
		}

		JavaAction jetty = execJetty(jettyOption);
		execSdm();
		jetty.join();
	}

	private JavaAction execSdm() {
		ConfigurationContainer configs = getProject().getConfigurations();
		Configuration sdmConf = configs.getByName(PwtLibPlugin.CONF_GWT_SDM);
		Configuration compileConf = configs.getByName(JavaPlugin.COMPILE_CONFIGURATION_NAME);

		DependencySet depSet = compileConf.getAllDependencies();
		List<File> subProjectSrc = Lists.newArrayList();
		for (Dependency dep : depSet) {
			if (dep instanceof ProjectDependency) {
				ProjectDependency projectDependency = (ProjectDependency) dep;
				JavaPluginConvention javaConvention =
					projectDependency.getDependencyProject().getConvention().getPlugin(JavaPluginConvention.class);
				SourceSet mainSourceSet = javaConvention.getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME);

				for (File file : mainSourceSet.getAllSource().getSrcDirs()) {
					subProjectSrc.add(file);
				}
			}
		}

		PutnamiExtension putnami = getProject().getExtensions().getByType(PutnamiExtension.class);
		DevOption devOption = putnami.getDev();

		JavaCommandBuilder builder = new JavaCommandBuilder();
		builder.configureJavaArgs(putnami.getDev());
		builder.setMainClass("com.google.gwt.dev.codeserver.CodeServer");
		builder.addClassPath(sdmConf.getAsPath());

		if (getSrc() != null) {
			for (File srcDir : getSrc()) {
				if (srcDir.isDirectory()) {
				builder.addArg("-src", srcDir);
				}
			}
		}
		for (File srcDir : subProjectSrc) {
			if (srcDir.isDirectory()) {
				builder.addArg("-src", srcDir);
			}
		}
		builder.addArgIf(devOption.getAllowMissingSrc(), "-allowMissingSrc", "-noallowMissingSrc");
		builder.addArg("-bindAddress", devOption.getBindAddress());
		builder.addArgIf(devOption.getCompileTest(), "-compileTest ", "-nocompileTest");
		if (Boolean.TRUE.equals(devOption.getCompileTest())) {
			builder.addArg("-compileTestRecompiles", devOption.getCompileTestRecompiles());
		}
		builder.addArgIf(devOption.getFailOnError(), "-failOnError", "-nofailOnError");
		builder.addArgIf(devOption.getPrecompile(), "-precompile", "-noprecompile");
		builder.addArg("-port", devOption.getPort());
		builder.addArgIf(devOption.getEnforceStrictResources(), "-XenforceStrictResources ", "-XnoenforceStrictResources");
		builder.addArg("-workDir", devOption.getWorkDir());
		builder.addArg("-launcherDir", devOption.getLauncherDir());
		builder.addArgIf(devOption.getPrecompile(), "-incremental", "-noincremental");
		builder.addArg("-sourceLevel", devOption.getSourceLevel());
		builder.addArg("-logLevel", devOption.getLogLevel());
		builder.addArg("-XmethodNameDisplayMode", devOption.getMethodNameDisplayMode());
		builder.addArg("-XjsInteropMode", devOption.getJsInteropMode());

		for (String module : getModules()) {
			builder.addArg(module);
		}

		JavaAction sdmAction = new JavaAction(builder.toString());
		sdmAction.execute(this);

		return sdmAction;
	}

	public void configureJetty(Project project, JettyOption options) {
		options.setJettyConf(new File(getProject().getBuildDir(), "putnami/conf/jetty-dev-conf.xml"));
	}

	public void configureCodeServer(final Project project, final PutnamiExtension extention) {
		final DevOption options = extention.getDev();

		final File buildDir = new File(project.getBuildDir(), "putnami");

		options.setLauncherDir(ResourceUtils.ensureDir(buildDir, "conf"));
		options.setWorkDir(ResourceUtils.ensureDir(buildDir, "work"));

		JavaPluginConvention javaConvention = project.getConvention().getPlugin(JavaPluginConvention.class);
		SourceSet mainSourceSet = javaConvention.getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME);
		final FileCollection sources = getProject().files(mainSourceSet.getAllJava().getSrcDirs());

		ConventionMapping convention = ((IConventionAware) this).getConventionMapping();
		convention.map("modules", new Callable<List<String>>() {
			@Override
			public List<String> call() throws Exception {
				return extention.getModule();
			}
		});
		convention.map("src", new Callable<FileCollection>() {
			@Override
			public FileCollection call() throws Exception {
				return sources;
			}
		});
	}

	@Input
	public List<String> getModules() {
		return modules;
	}

	@InputFiles
	public FileCollection getSrc() {
		return src;
	}

}
