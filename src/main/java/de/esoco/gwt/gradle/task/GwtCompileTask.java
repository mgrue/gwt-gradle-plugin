/**
 * This file is part of gwt-gradle-plugin.
 *
 * gwt-gradle-plugin is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * gwt-gradle-plugin is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with gwt-gradle-plugin. If not,
 * see <http://www.gnu.org/licenses/>.
 */
package de.esoco.gwt.gradle.task;

import com.google.common.base.Strings;

import de.esoco.gwt.gradle.command.CompileCommand;
import de.esoco.gwt.gradle.extension.CompilerOption;
import de.esoco.gwt.gradle.extension.GwtExtension;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.ConventionMapping;
import org.gradle.api.internal.IConventionAware;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskAction;

import java.io.File;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;


@CacheableTask
public class GwtCompileTask extends AbstractTask {

	public static final String NAME = "gwtCompile";

	private List<String>   modules;
	private File           war;
	private FileCollection src;

	public GwtCompileTask() {

		setDescription("Compile the GWT modules");

		dependsOn(JavaPlugin.COMPILE_JAVA_TASK_NAME,
		          JavaPlugin.PROCESS_RESOURCES_TASK_NAME);
	}

	public void configure(final Project project, final GwtExtension extension) {

		final CompilerOption options = extension.getCompile();

		options.init(project);
		options.setLocalWorkers(evalWorkers(options));

		final ConfigurableFileCollection sources     = project.files();
		Set<Project>                     allProjects = new HashSet<>();

		addSources(project, sources, allProjects);

		ConventionMapping mapping =
		    ((IConventionAware) this).getConventionMapping();

		mapping.map("modules", new Callable<List<String>>() {

		                @Override
		                public List<String> call() {

		                    return extension.getModule();
		                }
		            });
		mapping.map("war", new Callable<File>() {

		                @Override
		                public File call() {

		                    return options.getWar();
		                }
		            });
		mapping.map("src", new Callable<FileCollection>() {

		                @Override
		                public FileCollection call() {

		                    return sources;
		                }
		            });
	}

	@TaskAction
	public void exec() {

		Project project = getProject();

		GwtExtension extension =
		    project.getExtensions().getByType(GwtExtension.class);

		CompilerOption compilerOptions = extension.getCompile();

		if (!Strings.isNullOrEmpty(extension.getSourceLevel()) &&
		    Strings.isNullOrEmpty(compilerOptions.getSourceLevel())) {
			compilerOptions.setSourceLevel(extension.getSourceLevel());
		}

		CompileCommand command =
		    new CompileCommand(project, compilerOptions, getSrc(), getWar(),
		                       getModules());

		command.execute();

		project.getTasks().getByName(GwtCheckTask.NAME).setEnabled(false);
	}

	@Input
	public List<String> getModules() {

		return modules;
	}

	@InputFiles
	@PathSensitive(PathSensitivity.RELATIVE)
	public FileCollection getSrc() {

		return src;
	}

	@OutputDirectory
	public File getWar() {

		return war;
	}

	@SuppressWarnings("UnstableApiUsage")
	private void addSources(Project project,
	                        final ConfigurableFileCollection sources,
	                        final Set<Project> allProjects) {

		if (allProjects.add(project)) {
			// wait until the project is fully evaluated before attempting to read the sourceset,
			// in case user modifies it.
			// TODO: use Provider<> instead of afterEvaluate.
			//  This requires only-newer versions of gradle, so we'll leave this in for now,
			//  and consider using Provider in a future release, to have a window for users of old gradle versions.
			final Action<? super Project> includeSourceAction =
			    new Action<Project>() {

					@Override
					public void execute(Project project) {

						addSourceSet(sources, project,
						             SourceSet.MAIN_SOURCE_SET_NAME);

						// recursively add _all_ project dependencies
						addSources(project, sources, allProjects);
					}
				};

			if (project.getState().getExecuted()) {
				includeSourceAction.execute(project);
			} else {
				project.afterEvaluate(includeSourceAction);
			}
		}
	}

	private void addSourceSet(ConfigurableFileCollection sources,
	                          Project project, String sourceSet) {

		JavaPluginConvention javaConvention =
		    project.getConvention().findPlugin(JavaPluginConvention.class);

		if (javaConvention != null) {
			project.getLogger().info("Adding {}.sourceSets.main.output and sourceSets.main.allSource.srcDirs to {}", project.getPath(), getPath());
			SourceSet mainSourceSet = javaConvention.getSourceSets().getByName(sourceSet);
			sources
				.from(project.files(mainSourceSet.getOutput().getFiles())) // this _should_ include proper task dependencies, but it does not...
				.from(project.files(mainSourceSet.getAllSource().getSrcDirs()));
			// add explicit task dependencies, so we rebuild whenever source outputs change.
			dependsOn(mainSourceSet.getOutput());
		}
	}

	private int evalWorkers(CompilerOption options) {

		long                  workers =
		    Runtime.getRuntime().availableProcessors();
		OperatingSystemMXBean osMBean =
		    ManagementFactory.getOperatingSystemMXBean();

		if (osMBean instanceof com.sun.management.OperatingSystemMXBean) {
			com.sun.management.OperatingSystemMXBean sunOsMBean    =
			    (com.sun.management.OperatingSystemMXBean) osMBean;
			long                                     memPerWorker  =
			    1024L * 1024L * options.getLocalWorkersMem();
			long                                     nbFreeMemInGb =
			    sunOsMBean.getFreePhysicalMemorySize() / memPerWorker;

			if (nbFreeMemInGb < workers) {
				workers = nbFreeMemInGb;
			}

			if (workers < 1) {
				workers = 1;
			}
		}

		return (int) workers;
	}
}
