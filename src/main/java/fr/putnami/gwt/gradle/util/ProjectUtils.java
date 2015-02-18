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
package fr.putnami.gwt.gradle.util;

import com.google.common.collect.Lists;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.DependencySet;
import org.gradle.api.artifacts.ProjectDependency;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;

import java.io.File;
import java.util.Collection;
import java.util.List;

public final class ProjectUtils {

	private ProjectUtils() {
	}

	public static Collection<File> listProjectDepsSrcDirs(Project project) {
		ConfigurationContainer configs = project.getConfigurations();
		Configuration compileConf = configs.getByName(JavaPlugin.COMPILE_CONFIGURATION_NAME);
		DependencySet depSet = compileConf.getAllDependencies();

		List<File> result = Lists.newArrayList();
		for (Dependency dep : depSet) {
			if (dep instanceof ProjectDependency) {
				ProjectDependency projectDependency = (ProjectDependency) dep;
				JavaPluginConvention javaConvention =
					projectDependency.getDependencyProject().getConvention().getPlugin(JavaPluginConvention.class);
				SourceSet mainSourceSet = javaConvention.getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME);

				for (File file : mainSourceSet.getAllSource().getSrcDirs()) {
					result.add(file);
				}
			}
		}
		return result;
	}
}
