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
package fr.putnami.pwt.gradle.task;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;

import org.gradle.api.Project;
import org.gradle.api.plugins.WarPlugin;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.bundling.War;

import java.io.File;
import java.io.IOException;

import fr.putnami.pwt.gradle.action.JavaAction;
import fr.putnami.pwt.gradle.extension.JettyOption;
import fr.putnami.pwt.gradle.extension.PutnamiExtension;
import fr.putnami.pwt.gradle.util.ResourceUtils;

public class GwtRunTask extends AbstractJettyTask {

	public static final String NAME = "gwtRun";

	public GwtRunTask() {
		setName(NAME);
		setDescription("Run jetty with the GW the GWT modules");

		dependsOn(WarPlugin.WAR_TASK_NAME);
	}

	@TaskAction
	public void exec() throws Exception {
		PutnamiExtension putnami = getProject().getExtensions().getByType(PutnamiExtension.class);
		JettyOption jettyOption = putnami.getJetty();

		try {
			ResourceUtils.ensureDir(jettyOption.getLogFile().getParentFile());
			ResourceUtils.copy("/stub.jetty-run-conf.xml", jettyOption.getJettyConf(),
				new ImmutableMap.Builder<String, String>()
					.put("__WAR_FILE__", jettyOption.getWar().getAbsolutePath())
					.build());
		} catch (IOException e) {
			Throwables.propagate(e);
		}

		JavaAction jetty = execJetty(jettyOption);
		jetty.join();
	}

	@Override
	public void configureJetty(final Project project, final JettyOption options) {
		War warTask = (War) getProject().getTasks().getByName("war");
		options.setWar(warTask.getArchivePath());
		options.setJettyConf(new File(getProject().getBuildDir(), "putnami/jetty/jetty-run-conf.xml"));
		super.configureJetty(project, options);
	}

}
