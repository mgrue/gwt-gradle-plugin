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
package fr.putnami.pwt.gradle;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.WarPlugin;

import fr.putnami.pwt.gradle.extension.PwtExtension;
import fr.putnami.pwt.gradle.task.GwtCompileTask;
import fr.putnami.pwt.gradle.task.GwtDevTask;
import fr.putnami.pwt.gradle.task.GwtRunTask;

public class PwtPlugin implements Plugin<Project> {

	@Override
	public void apply(final Project project) {
		project.getPlugins().apply(PwtLibPlugin.class);
		project.getPlugins().apply(WarPlugin.class);

		project.getTasks().create(GwtCompileTask.NAME, GwtCompileTask.class);
		project.getTasks().create(GwtDevTask.NAME, GwtDevTask.class);
		project.getTasks().create(GwtRunTask.NAME, GwtRunTask.class);

		final PwtExtension extension = project.getExtensions().getByType(PwtExtension.class);

		project.getTasks().withType(GwtCompileTask.class, new Action<GwtCompileTask>() {
			@Override
			public void execute(final GwtCompileTask task) {
				task.configure(project, extension.getCompile());
			}
		});

	}

}
