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

import com.google.common.collect.Lists;

import org.gradle.api.Project;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.ConventionMapping;
import org.gradle.api.internal.IConventionAware;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskAction;

import java.util.List;
import java.util.concurrent.Callable;

import fr.putnami.gwt.gradle.action.JavaAction;
import fr.putnami.gwt.gradle.extension.DevOption;
import fr.putnami.gwt.gradle.extension.PutnamiExtension;
import fr.putnami.gwt.gradle.helper.CodeServerBuilder;
import fr.putnami.gwt.gradle.util.ProjectUtils;

public class GwtCodeServerTask extends AbstractTask {

	public static final String NAME = "gwtCodeServer";

	private FileCollection src;
	private List<String> modules = Lists.newArrayList();

	public GwtCodeServerTask() {
		setName(NAME);
		setDescription("Run CodeServer");

		dependsOn(JavaPlugin.COMPILE_JAVA_TASK_NAME, JavaPlugin.PROCESS_RESOURCES_TASK_NAME);
	}

	@TaskAction
	public void exec() throws Exception {
		PutnamiExtension putnami = getProject().getExtensions().getByType(PutnamiExtension.class);

		CodeServerBuilder sdmBuilder = new CodeServerBuilder();
		sdmBuilder.addSrc(getSrc());
		sdmBuilder.addSrc(ProjectUtils.listProjectDepsSrcDirs(getProject()));
		sdmBuilder.configure(getProject(), putnami.getDev(), getModules());

		JavaAction sdmAction = sdmBuilder.buildJavaAction();
		sdmAction.execute(this);
		sdmAction.join();
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
