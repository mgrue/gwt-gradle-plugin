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

import com.google.common.collect.ImmutableMap;

import org.gradle.api.Project;
import org.gradle.api.internal.ConventionMapping;
import org.gradle.api.internal.IConventionAware;
import org.gradle.api.plugins.WarPlugin;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.bundling.War;

import java.io.File;
import java.util.concurrent.Callable;

import fr.putnami.pwt.gradle.PwtLibPlugin;
import fr.putnami.pwt.gradle.action.JavaAction;
import fr.putnami.pwt.gradle.extension.JettyOption;
import fr.putnami.pwt.gradle.extension.PutnamiExtension;
import fr.putnami.pwt.gradle.util.JavaCommandBuilder;
import fr.putnami.pwt.gradle.util.ResourceUtils;

public class GwtRunTask extends AbstractTask {

	public static final String NAME = "gwtRun";

	private File jettyConf;
	private File jettyWar;
	private String jettyBindAddress;
	private File jettyLogRequestFile;
	private File jettyLogFile;
	private int jettyPort;
	private int jettyStopPort;
	private String jettyStopKey;

	public GwtRunTask() {
		setName(NAME);
		setDescription("Run jetty with the GW the GWT modules");

		dependsOn(WarPlugin.WAR_TASK_NAME);
	}

	@TaskAction
	public void exec() throws Exception {
		File buildDir = getProject().getBuildDir();
		File jettyConfDir = ResourceUtils.ensureDir(buildDir, "pwt/jetty");
		File jettyConf = ResourceUtils.copy("/stub.jetty-run-conf.xml", jettyConfDir, "jetty-run-conf.xml",
			new ImmutableMap.Builder<String, String>()
				.put("__WAR_FILE__", getJettyWar().getAbsolutePath())
				.build());

		String jettyClassPath = getProject()
			.getConfigurations().getByName(PwtLibPlugin.CONF_JETTY).getAsPath();
		PutnamiExtension putnami = getProject().getExtensions().getByType(PutnamiExtension.class);

		JavaCommandBuilder builder = new JavaCommandBuilder();
		builder.configureJavaArgs(putnami.getJetty());
		builder.setMainClass("org.eclipse.jetty.runner.Runner");
		builder.addClassPath(jettyClassPath);

		builder.addArg("--log", getJettyLogRequestFile());
		builder.addArg("--out", getJettyLogFile());
		builder.addArg("--host", getJettyBindAddress());
		builder.addArg("--port", getJettyPort());
		builder.addArg("--stop-port", getJettyStopPort());
		builder.addArg("--stop-key", getJettyStopKey());

		builder.addArg(jettyConf.getAbsolutePath());

		JavaAction runJettyAction = new JavaAction(builder.toString());
		runJettyAction.execute(this);

		runJettyAction.join();
	}

	public void configureJetty(final Project project, final JettyOption options) {
		final File buildDir = new File(project.getBuildDir(), "putnami");
		final File logDir = ResourceUtils.ensureDir(buildDir, "logs");

		War warTask = (War) getProject().getTasks().getByName("war");

		options.setWar(warTask.getArchivePath());
		options.setLogFile(new File(logDir, "jetty.log"));
		options.setLogRequestFile(new File(logDir, "request.log"));
		options.setJettyConf(ResourceUtils.ensureDir(buildDir, "jerryConf"));

		ConventionMapping convention = ((IConventionAware) this).getConventionMapping();

		convention.map("jettyConf", new Callable<File>() {
			@Override
			public File call() throws Exception {
				return options.getJettyConf();
			}
		});
		convention.map("jettyWar", new Callable<File>() {
			@Override
			public File call() throws Exception {
				return options.getWar();
			}
		});
		convention.map("jettyBindAddress", new Callable<String>() {
			@Override
			public String call() throws Exception {
				return options.getBindAddress();
			}
		});
		convention.map("jettyLogRequestFile", new Callable<File>() {
			@Override
			public File call() throws Exception {
				return options.getLogRequestFile();
			}
		});
		convention.map("jettyLogFile", new Callable<File>() {
			@Override
			public File call() throws Exception {
				return options.getLogFile();
			}
		});
		convention.map("jettyPort", new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				return options.getPort();
			}
		});
		convention.map("jettyStopPort", new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				return options.getStopPort();
			}
		});
		convention.map("jettyStopKey", new Callable<String>() {
			@Override
			public String call() throws Exception {
				return options.getStopKey();
			}
		});
	}

	@OutputDirectory
	public File getJettyConf() {
		return jettyConf;
	}

	@InputFile
	public File getJettyWar() {
		return jettyWar;
	}

	@Input
	public String getJettyBindAddress() {
		return jettyBindAddress;
	}

	@OutputFile
	public File getJettyLogRequestFile() {
		return jettyLogRequestFile;
	}

	@OutputFile
	public File getJettyLogFile() {
		return jettyLogFile;
	}

	@Input
	public int getJettyPort() {
		return jettyPort;
	}

	@Input
	public int getJettyStopPort() {
		return jettyStopPort;
	}

	@Input
	public String getJettyStopKey() {
		return jettyStopKey;
	}

}
