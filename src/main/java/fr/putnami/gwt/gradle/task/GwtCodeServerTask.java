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
import org.gradle.api.file.FileCollection;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskAction;

import fr.putnami.gwt.gradle.action.JavaAction;
import fr.putnami.gwt.gradle.extension.DevOption;
import fr.putnami.gwt.gradle.extension.PutnamiExtension;
import fr.putnami.gwt.gradle.helper.CodeServerBuilder;
import fr.putnami.gwt.gradle.util.ProjectUtils;
import fr.putnami.gwt.gradle.util.ResourceUtils;

public class GwtCodeServerTask extends AbstractTask {

	public static final String NAME = "gwtCodeServer";

	public GwtCodeServerTask() {
		setName(NAME);
		setDescription("Run CodeServer");

		dependsOn(JavaPlugin.COMPILE_JAVA_TASK_NAME, JavaPlugin.PROCESS_RESOURCES_TASK_NAME);
	}

	@TaskAction
	public void exec() throws Exception {
		PutnamiExtension putnami = getProject().getExtensions().getByType(PutnamiExtension.class);

		JavaPluginConvention javaConvention = getProject().getConvention().getPlugin(JavaPluginConvention.class);
		SourceSet mainSourceSet = javaConvention.getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME);
		final FileCollection sources = getProject().files(mainSourceSet.getAllJava().getSrcDirs());

		ResourceUtils.ensureDir(putnami.getDev().getLauncherDir());

		CodeServerBuilder sdmBuilder = new CodeServerBuilder();
		sdmBuilder.addSrc(sources);
		sdmBuilder.addSrc(ProjectUtils.listProjectDepsSrcDirs(getProject()));
		sdmBuilder.addArg("-launcherDir", putnami.getDev().getLauncherDir());
		sdmBuilder.configure(getProject(), putnami.getDev(), putnami.getModule());

		JavaAction sdmAction = sdmBuilder.buildJavaAction();
		sdmAction.execute(this);
		sdmAction.join();
	}

	public void configureCodeServer(final Project project, final PutnamiExtension extention) {
		final DevOption options = extention.getDev();
		options.init(project);
	}
}
