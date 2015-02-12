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

import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.plugins.WarPlugin;

import fr.putnami.gwt.gradle.PwtLibPlugin;
import fr.putnami.gwt.gradle.action.JavaAction;
import fr.putnami.gwt.gradle.extension.JettyOption;
import fr.putnami.gwt.gradle.util.JavaCommandBuilder;
import fr.putnami.gwt.gradle.util.ResourceUtils;

public class AbstractJettyTask extends AbstractTask {

	protected JavaAction execJetty(JettyOption jettyOption) {
		ConfigurationContainer configs = getProject().getConfigurations();

		Configuration runtimeConf = configs.getByName(WarPlugin.PROVIDED_RUNTIME_CONFIGURATION_NAME);
		Configuration jettyClassPath = configs.getByName(PwtLibPlugin.CONF_JETTY);

		JavaCommandBuilder builder = new JavaCommandBuilder();
		builder.configureJavaArgs(jettyOption);
		builder.setMainClass("org.eclipse.jetty.runner.Runner");

		builder.addClassPath(jettyClassPath.getAsPath());
		builder.addClassPath(runtimeConf.getAsPath());

		if (jettyOption.getLogRequestFile() != null) {
			ResourceUtils.ensureDir(jettyOption.getLogRequestFile().getParentFile());
			builder.addArg("--log", jettyOption.getLogRequestFile());
		}
		if (jettyOption.getLogFile() != null) {
			ResourceUtils.ensureDir(jettyOption.getLogFile().getParentFile());
			builder.addArg("--out", jettyOption.getLogFile());
		}
		builder.addArg("--host", jettyOption.getBindAddress());
		builder.addArg("--port", jettyOption.getPort());
		builder.addArg("--stop-port", jettyOption.getStopPort());
		builder.addArg("--stop-key", jettyOption.getStopKey());

		builder.addArg(jettyOption.getJettyConf().getAbsolutePath());

		JavaAction jetty = new JavaAction(builder.toString());
		jetty.execute(this);

		return jetty;
	}
}
