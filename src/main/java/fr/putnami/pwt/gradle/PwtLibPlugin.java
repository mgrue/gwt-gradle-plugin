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

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.plugins.MavenPlugin;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.bundling.Jar;

import fr.putnami.pwt.gradle.extension.PutnamiExtension;

public class PwtLibPlugin implements Plugin<Project> {

	public static final String CONF_GWT_SDM = "gwtSdmConf";
	public static final String CONF_JETTY = "jettyConf";

	@Override
	public void apply(Project project) {

		project.getPlugins().apply(JavaPlugin.class);
		project.getPlugins().apply(MavenPlugin.class);

		project.getExtensions()
			.create(PutnamiExtension.PWT_EXTENSION, PutnamiExtension.class);

		project.getConfigurations().create(CONF_GWT_SDM);
		project.getDependencies().add(CONF_GWT_SDM, "com.google.gwt:gwt-codeserver:2.7.0");
		project.getDependencies().add(CONF_GWT_SDM, "com.google.gwt:gwt-user:2.7.0");

		project.getConfigurations().create(CONF_JETTY);
		project.getDependencies().add(CONF_JETTY, "org.eclipse.jetty:jetty-runner:9.2.2.v20140723");
		project.getDependencies().add(CONF_JETTY, "fr.putnami.pwt:putnami-gradle-plugin:0.1.0-SNAPSHOT");

		project.getDependencies().add("compile", "com.google.gwt:gwt-user:2.7.0");

		Jar jarTask = project.getTasks().withType(Jar.class).getByName("jar");
		JavaPluginConvention javaConvention = project.getConvention().getPlugin(JavaPluginConvention.class);
		SourceSet mainSourset = javaConvention.getSourceSets().getByName("main");
		jarTask.from(mainSourset.getAllSource());
	}

}
