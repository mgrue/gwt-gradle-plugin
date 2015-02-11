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
package fr.putnami.gwt.gradle;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.plugins.MavenPlugin;
import org.gradle.api.plugins.WarPlugin;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.bundling.Jar;

import fr.putnami.gwt.gradle.extension.PutnamiExtension;

public class PwtLibPlugin implements Plugin<Project> {

	public static final String PLUGIN_VERSION = "0.1.0-SNAPSHOT";

	public static final String CONF_GWT_SDM = "sdmConf";
	public static final String CONF_JETTY = "jettyConf";

	@Override
	public void apply(Project project) {

		project.getPlugins().apply(JavaPlugin.class);
		project.getPlugins().apply(MavenPlugin.class);

		final PutnamiExtension extention =
			project.getExtensions().create(PutnamiExtension.PWT_EXTENSION, PutnamiExtension.class);

		project.getConfigurations().create(CONF_GWT_SDM);
		project.getConfigurations().create(CONF_JETTY);

		includeSourcesToJar(project);

		project.afterEvaluate(new Action<Project>() {
			@Override
			public void execute(final Project project) {
				String gwtVersion = extention.getGwtVersion();
				String jettyVersion = extention.getJettyVersion();

				DependencyHandler dependencies = project.getDependencies();

				String providedConfiguration = project.getPlugins().hasPlugin("war") ?
					WarPlugin.PROVIDED_COMPILE_CONFIGURATION_NAME : JavaPlugin.COMPILE_CONFIGURATION_NAME;

				dependencies.add(CONF_GWT_SDM, "com.google.gwt:gwt-codeserver:" + gwtVersion);
				dependencies.add(CONF_GWT_SDM, "com.google.gwt:gwt-user:" + gwtVersion);
				dependencies.add(providedConfiguration
					, "com.google.gwt:gwt-user:" + gwtVersion);
				if (extention.isGwtElementalLib()) {
					dependencies
						.add(JavaPlugin.COMPILE_CONFIGURATION_NAME, "com.google.gwt:gwt-elemental:" + gwtVersion);
				}
				if (extention.isGwtServletLib()) {
					dependencies
						.add(JavaPlugin.COMPILE_CONFIGURATION_NAME, "com.google.gwt:gwt-servlet:" + gwtVersion);
				}
				if (extention.isGwtDevLib()) {
					dependencies
						.add(providedConfiguration, "com.google.gwt:gwt-dev:" + gwtVersion);
				}

				dependencies.add(CONF_JETTY, "org.eclipse.jetty:jetty-runner:" + jettyVersion);
				dependencies.add(CONF_JETTY, "fr.putnami.pwt:putnami-gradle-plugin:" + PLUGIN_VERSION);

			}
		});
	}

	private void includeSourcesToJar(Project project) {
		Jar jarTask = project.getTasks().withType(Jar.class).getByName("jar");
		JavaPluginConvention javaConvention = project.getConvention().getPlugin(JavaPluginConvention.class);
		SourceSet mainSourset = javaConvention.getSourceSets().getByName("main");
		jarTask.from(mainSourset.getAllSource());
	}

}