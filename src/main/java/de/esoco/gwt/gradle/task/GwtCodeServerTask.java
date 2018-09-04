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
package de.esoco.gwt.gradle.task;

import com.google.common.base.Strings;

import de.esoco.gwt.gradle.action.JavaAction;
import de.esoco.gwt.gradle.extension.DevOption;
import de.esoco.gwt.gradle.extension.GwtExtension;
import de.esoco.gwt.gradle.helper.CodeServerBuilder;
import de.esoco.gwt.gradle.util.ResourceUtils;

import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.TaskAction;

public class GwtCodeServerTask extends AbstractTask {

	public static final String NAME = "gwtCodeServer";

	public GwtCodeServerTask() {
		setDescription("Run CodeServer in SuperDevMode");

		dependsOn(JavaPlugin.COMPILE_JAVA_TASK_NAME, JavaPlugin.PROCESS_RESOURCES_TASK_NAME);
	}

	@TaskAction
	public void exec() {
		GwtExtension putnami = getProject().getExtensions().getByType(GwtExtension.class);
		if (!Strings.isNullOrEmpty(putnami.getSourceLevel()) &&
			Strings.isNullOrEmpty(putnami.getDev().getSourceLevel())) {
			putnami.getDev().setSourceLevel(putnami.getSourceLevel());
		}

		ResourceUtils.ensureDir(putnami.getDev().getLauncherDir());

		CodeServerBuilder sdmBuilder = new CodeServerBuilder();
		if (!putnami.getGwtVersion().startsWith("2.6")) {
			sdmBuilder.addArg("-launcherDir", putnami.getDev().getLauncherDir());
		}
		sdmBuilder.configure(getProject(), putnami.getDev(), putnami.getModule());

		JavaAction sdmAction = sdmBuilder.buildJavaAction();
		sdmAction.execute(this);
		sdmAction.join();
	}

	public void configureCodeServer(final Project project, final GwtExtension extention) {
		final DevOption options = extention.getDev();
		options.init(project);
	}
}
