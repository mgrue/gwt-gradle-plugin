package fr.putnami.pwt.gradle.task;

import fr.putnami.pwt.gradle.PwtLibPlugin;
import fr.putnami.pwt.gradle.action.JavaAction;
import fr.putnami.pwt.gradle.extension.JettyOption;
import fr.putnami.pwt.gradle.util.JavaCommandBuilder;
import fr.putnami.pwt.gradle.util.ResourceUtils;

public class AbstractJettyTask extends AbstractTask {

	protected JavaAction execJetty(JettyOption jettyOption) {
		String jettyClassPath =
			getProject().getConfigurations().getByName(PwtLibPlugin.CONF_JETTY).getAsPath();

		JavaCommandBuilder builder = new JavaCommandBuilder();
		builder.configureJavaArgs(jettyOption);
		builder.setMainClass("org.eclipse.jetty.runner.Runner");

		builder.addClassPath(jettyClassPath);

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
