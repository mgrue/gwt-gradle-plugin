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
import java.util.concurrent.Semaphore;

import fr.putnami.gwt.gradle.action.JavaAction;
import fr.putnami.gwt.gradle.action.JavaAction.ProcessLogger;
import fr.putnami.gwt.gradle.extension.DevOption;
import fr.putnami.gwt.gradle.extension.JettyOption;
import fr.putnami.gwt.gradle.extension.PutnamiExtension;
import fr.putnami.gwt.gradle.helper.CodeServerBuilder;
import fr.putnami.gwt.gradle.helper.JettyServerBuilder;
import fr.putnami.gwt.gradle.util.ProjectUtils;
import fr.putnami.gwt.gradle.util.ResourceUtils;

public class GwtDevTask extends AbstractTask {

	public static final String NAME = "gwtDev";

	private FileCollection src;
	private List<String> modules = Lists.newArrayList();

	public GwtDevTask() {
		setName(NAME);
		setDescription("Run DevMode");

		dependsOn(JavaPlugin.COMPILE_JAVA_TASK_NAME, JavaPlugin.PROCESS_RESOURCES_TASK_NAME);
	}

	@TaskAction
	public void exec() throws Exception {
		PutnamiExtension putnami = getProject().getExtensions().getByType(PutnamiExtension.class);
		DevOption sdmOption = putnami.getDev();
		JettyOption jettyOption = putnami.getJetty();

		createWarExploded(sdmOption);

		ResourceUtils.ensureDir(sdmOption.getWar());
		ResourceUtils.ensureDir(sdmOption.getWorkDir());

		try {
			ResourceUtils.copy("/stub.jetty-conf.xml", jettyOption.getJettyConf(),
				new ImmutableMap.Builder<String, String>()
					.put("__WAR_FILE__", sdmOption.getWar().getAbsolutePath())
					.build());

		} catch (IOException e) {
			Throwables.propagate(e);
		}

		execSdm();

		JavaAction jetty = execJetty();
		jetty.join();
	}

	private void createWarExploded(DevOption sdmOption) throws IOException {
		WarPluginConvention warConvention = getProject().getConvention().getPlugin(WarPluginConvention.class);
		JavaPluginConvention javaConvention = getProject().getConvention().getPlugin(JavaPluginConvention.class);

		File warDir = sdmOption.getWar();

		ResourceUtils.copyDirectory(warConvention.getWebAppDir(), warDir);

		if (Boolean.TRUE.equals(sdmOption.getNoServer())) {
			File webInfDir = ResourceUtils.ensureDir(new File(warDir, "WEB-INF"));
			ResourceUtils.deleteDirectory(webInfDir);
		} else {
			SourceSet mainSourceSet = javaConvention.getSourceSets().getByName("main");
			File classesDir = ResourceUtils.ensureDir(new File(warDir, "WEB-INF/classes"));
			for (File file : mainSourceSet.getResources().getSrcDirs()) {
				ResourceUtils.copyDirectory(file, classesDir);
			}
			ResourceUtils.copyDirectory(mainSourceSet.getOutput().getClassesDir(), classesDir);
			for (File file : mainSourceSet.getOutput().getFiles()) {
				if (file.exists() && file.isFile()) {
					ResourceUtils.copy(file, new File(classesDir, file.getName()));
				}
			}
			File libDir = ResourceUtils.ensureDir(new File(warDir, "WEB-INF/lib"));
			for (File file : mainSourceSet.getRuntimeClasspath()) {
				if (file.exists() && file.isFile()) {
					ResourceUtils.copy(file, new File(libDir, file.getName()));
				}
			}
		}
	}

	private JavaAction execJetty() {
		PutnamiExtension putnami = getProject().getExtensions().getByType(PutnamiExtension.class);
		JettyServerBuilder jettyBuilder = new JettyServerBuilder();
		jettyBuilder.configure(getProject(), putnami.getJetty());
		JavaAction jetty = jettyBuilder.buildJavaAction();
		jetty.execute(this);
		return jetty;
	}

	private JavaAction execSdm() {
		PutnamiExtension putnami = getProject().getExtensions().getByType(PutnamiExtension.class);
		DevOption devOption = putnami.getDev();

		CodeServerBuilder sdmBuilder = new CodeServerBuilder();
		sdmBuilder.addSrc(getSrc());
		sdmBuilder.addSrc(ProjectUtils.listProjectDepsSrcDirs(getProject()));
		sdmBuilder.configure(getProject(), putnami.getDev(), getModules());
		sdmBuilder.addArg("-launcherDir", devOption.getWar());

		JavaAction sdmAction = sdmBuilder.buildJavaAction();

		final Semaphore lock = new Semaphore(1);

		sdmAction.setInfoLogger(new ProcessLogger() {
			@Override
			protected void printLine(String line) {
				if (line.contains("The code server is ready")) {
					lock.release();
				}
				super.printLine(line);
			}
		});

		lock.acquireUninterruptibly();
		sdmAction.execute(this);
		lock.acquireUninterruptibly();
		return sdmAction;
	}

	public void configureJetty(JettyOption options) {
		options.setJettyConf(new File(getProject().getBuildDir(), "putnami/conf/jetty-dev-conf.xml"));
	}

	public void configureCodeServer(final Project project, final PutnamiExtension extention) {
		final DevOption options = extention.getDev();
		options.init(project);

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
