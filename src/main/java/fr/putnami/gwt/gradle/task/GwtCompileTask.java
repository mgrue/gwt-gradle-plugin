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

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.ConventionMapping;
import org.gradle.api.internal.IConventionAware;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.List;
import java.util.concurrent.Callable;

import fr.putnami.gwt.gradle.PwtLibPlugin;
import fr.putnami.gwt.gradle.action.JavaAction;
import fr.putnami.gwt.gradle.extension.CompilerOption;
import fr.putnami.gwt.gradle.extension.PutnamiExtension;
import fr.putnami.gwt.gradle.util.JavaCommandBuilder;

public class GwtCompileTask extends AbstractTask {

	public static final String NAME = "gwtCompile";

	private List<String> modules;
	private File war;
	private FileCollection src;

	public GwtCompileTask() {
		setName(NAME);
		setDescription("Compile the GWT modules");

		dependsOn(JavaPlugin.COMPILE_JAVA_TASK_NAME, JavaPlugin.PROCESS_RESOURCES_TASK_NAME);

	}

	@TaskAction
	public void exec() {

		PutnamiExtension putnami = getProject().getExtensions().getByType(PutnamiExtension.class);
		CompilerOption compilerOptions = putnami.getCompile();

		JavaAction compileAction = new JavaAction(buildCompileCommand(compilerOptions));
		compileAction.execute(this);
		compileAction.join();
		if (compileAction.exitValue() != 0) {
			throw new RuntimeException("Fail to compile GWT modules");
		}
	}

	private String buildCompileCommand(CompilerOption compilerOptions) {
		Configuration sdmConf = getProject().getConfigurations().getByName(PwtLibPlugin.CONF_GWT_SDM);
		Configuration compileConf = getProject().getConfigurations().getByName(JavaPlugin.COMPILE_CONFIGURATION_NAME);

		JavaCommandBuilder builder = new JavaCommandBuilder();
		builder.configureJavaArgs(compilerOptions);
		builder.addJavaArgs("-Dgwt.persistentunitcachedir=" + getProject().getBuildDir() + "/putnami/work/cahce");

		builder.setMainClass("com.google.gwt.dev.Compiler");

		for (File sourceDir : getSrc()) {
			builder.addClassPath(sourceDir.getAbsolutePath());
		}

		builder.addClassPath(compileConf.getAsPath());
		builder.addClassPath(sdmConf.getAsPath());

		builder.addArg("-war", getWar());
		builder.addArg("-extra", compilerOptions.getExtra());
		builder.addArg("-workDir", compilerOptions.getWorkDir());
		builder.addArg("-gen", compilerOptions.getGen());
		builder.addArg("-deploy", compilerOptions.getDeploy());

		builder.addArg("-logLevel", compilerOptions.getLogLevel());
		builder.addArg("-localWorkers", compilerOptions.getLocalWorkers());
		builder.addArgIf(compilerOptions.getFailOnError(), "-failOnError", "-nofailOnError");
		builder.addArg("-sourceLevel", compilerOptions.getSourceLevel());
		builder.addArgIf(compilerOptions.getDraftCompile(), "-draftCompile", "-nodraftCompile");
		builder.addArg("-optimize", compilerOptions.getOptimize());
		builder.addArg("-style", compilerOptions.getStyle());
		builder.addArgIf(compilerOptions.getCompileReport(), "-compileReport", "-nocompileReport");

		if (Boolean.TRUE.equals(compilerOptions.getIncremental())) {
			builder.addArg("-incremental");
			// builder.addArg("-incrementalCompileWarnings");
		}

		builder.addArgIf(compilerOptions.getCheckAssertions(), "-checkAssertions", "-nocheckAssertions");
		builder.addArgIf(compilerOptions.getCheckCasts(), "-XcheckCasts", "-XnocheckCasts");
		builder.addArgIf(compilerOptions.getEnforceStrictResources(), "-XenforceStrictResources",
			"-XnoenforceStrictResources");
		builder.addArgIf(compilerOptions.getClassMetadata(), "-XclassMetadata", "-XnoclassMetadata");

		builder.addArgIf(compilerOptions.getOverlappingSourceWarnings(), "-overlappingSourceWarnings",
			"-nooverlappingSourceWarnings");
		builder.addArgIf(compilerOptions.getSaveSource(), "-saveSource", "-nosaveSource");
		builder.addArg("-XmethodNameDisplayMode", compilerOptions.getMethodNameDisplayMode());

		builder.addArgIf(compilerOptions.getClosureCompiler(), "-XclosureCompiler", "-XnoclosureCompiler");

		builder.addArg("-XjsInteropMode", compilerOptions.getJsInteropMode());

		for (String module : getModules()) {
			builder.addArg(module);
		}

		return builder.toString();
	}

	public void configure(final Project project, final PutnamiExtension extention) {
		final CompilerOption options = extention.getCompile();

		final File buildDir = new File(project.getBuildDir(), "putnami");

		options.setWar(new File(buildDir, "out"));
		options.setWorkDir(new File(buildDir, "work"));
		options.setGen(new File(buildDir, "extra/gen"));
		options.setDeploy(new File(buildDir, "extra/deploy"));
		options.setExtra(new File(buildDir, "extra"));
		options.setSaveSourceOutput(new File(buildDir, "extra/source"));
		options.setMissingDepsFile(new File(buildDir, "extra/missingDepsFile"));
		options.setLocalWorkers(evalWorkers());


		JavaPluginConvention javaConvention = project.getConvention().getPlugin(JavaPluginConvention.class);
		SourceSet mainSourceSet = javaConvention.getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME);
		final FileCollection sources = getProject().files(mainSourceSet.getAllJava().getSrcDirs())
			.plus(project.files(mainSourceSet.getOutput().getClassesDir()))
			.plus(project.files(mainSourceSet.getOutput().getResourcesDir()));

		ConventionMapping mapping = ((IConventionAware) this).getConventionMapping();

		mapping.map("modules", new Callable<List<String>>() {
			@Override
			public List<String> call() throws Exception {
				return extention.getModule();
			}
		});

		mapping.map("war", new Callable<File>() {
			@Override
			public File call() throws Exception {
				return options.getWar();
			}
		});
		mapping.map("src", new Callable<FileCollection>() {
			@Override
			public FileCollection call() throws Exception {
				return sources;
			}
		});
	}

	private int evalWorkers() {
		long workers = Runtime.getRuntime().availableProcessors();
		OperatingSystemMXBean osMBean = ManagementFactory.getOperatingSystemMXBean();
		if(osMBean instanceof com.sun.management.OperatingSystemMXBean){
			com.sun.management.OperatingSystemMXBean sunOsMBean = (com.sun.management.OperatingSystemMXBean)osMBean;
			long nbFreeMemInGb = sunOsMBean.getFreePhysicalMemorySize() / (1024 * 1024 * 1024);

			if (nbFreeMemInGb < workers) {
				workers = nbFreeMemInGb;
			}
			if (workers < 1) {
				workers = 1;
			}
		}
		return (int) workers;
	}

	@OutputDirectory
	public File getWar() {
		return war;
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
