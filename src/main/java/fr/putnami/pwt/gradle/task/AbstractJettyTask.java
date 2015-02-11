package fr.putnami.pwt.gradle.task;

import org.gradle.api.Project;

import java.io.File;

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

		builder.addArg("--log", jettyOption.getLogRequestFile());
		builder.addArg("--out", jettyOption.getLogFile());
		builder.addArg("--host", jettyOption.getBindAddress());
		builder.addArg("--port", jettyOption.getPort());
		builder.addArg("--stop-port", jettyOption.getStopPort());
		builder.addArg("--stop-key", jettyOption.getStopKey());

		builder.addArg(jettyOption.getJettyConf().getAbsolutePath());

		JavaAction jetty = new JavaAction(builder.toString());
		jetty.execute(this);

		return jetty;
	}

	public void configureJetty(final Project project, final JettyOption options) {
		final File buildDir = new File(project.getBuildDir(), "putnami");
		final File logDir = ResourceUtils.ensureDir(buildDir, "logs");

		options.setLogFile(new File(logDir, "jetty.log"));
		options.setLogRequestFile(new File(logDir, "request.log"));
	}
}
