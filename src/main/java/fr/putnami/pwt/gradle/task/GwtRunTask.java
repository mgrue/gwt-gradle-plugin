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

import org.gradle.api.artifacts.Configuration;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.plugins.WarPlugin;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskAction;

import java.io.File;

import fr.putnami.pwt.gradle.PwtLibPlugin;
import fr.putnami.pwt.gradle.action.JavaAction;
import fr.putnami.pwt.gradle.utli.JavaCommandBuilder;
import fr.putnami.pwt.gradle.utli.ResourceUtils;

public class GwtRunTask extends AbstractPwtTask {

	public static final String NAME = "gwtRun";

	public GwtRunTask() {
		setName(NAME);
		setDescription("Run jetty with the GW the GWT modules");

		dependsOn(WarPlugin.WAR_TASK_NAME, JavaPlugin.COMPILE_JAVA_TASK_NAME, JavaPlugin.PROCESS_RESOURCES_TASK_NAME);
	}

	@TaskAction
	public void exec() throws Exception {
		File buildDir = getProject().getBuildDir();
		JavaPluginConvention javaPluginConvention = getProject().getConvention().getPlugin(JavaPluginConvention.class);
		// WarPluginConvention warPluginConvention =
		// getProject().getConvention().getPlugin(WarPluginConvention.class);

		File workDir = ResourceUtils.ensureDir(buildDir, "pwt/work");
		File jettyConfDir = ResourceUtils.ensureDir(buildDir, "pwt/jetty");

		File webOverrideFile =
			ResourceUtils.copy("/stub.web-override.xml",
				jettyConfDir, "web-override.xml",
				new ImmutableMap.Builder<String, String>()
					.put("__CODE_SERVER_PORT__", "9876")
					.build());
		File jettyConf = ResourceUtils.copy("/stub.jetty-conf.xml",
			jettyConfDir, "jetty-conf.xml",
			new ImmutableMap.Builder<String, String>()
				.put("__WEB_OVERRIDE__", webOverrideFile.getAbsolutePath())
				.put("__WAR_FILE__", "build/libs/testplugin.war")
				.build());

		SourceSetContainer sourceSetContainer = javaPluginConvention.getSourceSets();

		SourceSet mainSourceSet = sourceSetContainer.getByName("main");
		System.out.println(mainSourceSet.getJava().getSrcDirs());
		System.out.println(mainSourceSet.getResources().getSrcDirs());

		String jettyClassPath =
			getProject().getConfigurations().getByName(PwtLibPlugin.CONF_JETTY).getAsPath();

		JavaCommandBuilder builder = new JavaCommandBuilder();
		builder.setMainClass("org.eclipse.jetty.runner.Runner");
		builder.addClassPath(jettyClassPath);

		builder.addArg(jettyConf.getAbsolutePath());

		JavaAction runJettyAction = new JavaAction(builder.toString());
		runJettyAction.execute(this);

		Configuration sdmConf =
			getProject().getConfigurations().getByName(PwtLibPlugin.CONF_GWT_SDM);

		builder = new JavaCommandBuilder();
		builder.setMainClass("com.google.gwt.dev.codeserver.CodeServer");
		builder.addClassPath(sdmConf.getAsPath());
		builder.addArg("-noprecompile");
		builder.addArg("-src src/main/java");
		// builder.addArgs("-src " + devSrcDir.getAbsolutePath());
		builder.addArg("-workDir ", workDir);
		builder.addArg("fr.pwt.testplugin.TestDev");

		JavaAction sdmAction = new JavaAction(builder.toString());

		sdmAction.execute(this);
		// sdmAction.join();

		runJettyAction.join();
	}
}
