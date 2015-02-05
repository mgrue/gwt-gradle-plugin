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
package fr.putnami.pwt.gradle.utli;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.util.List;

public class JavaCommandBuilder {

	private List<String> classPaths = Lists.newArrayList();
	private List<String> javaArgs = Lists.newArrayList();
	private String mainClass;
	private List<String> args = Lists.newArrayList();

	public void setMainClass(String mainClass) {
		this.mainClass = mainClass;
	}

	public JavaCommandBuilder addClassPath(String classPath) {
		this.classPaths.add(classPath);
		return this;
	}

	public JavaCommandBuilder addJavaArgs(String javaArgs) {
		this.javaArgs.add(javaArgs);
		return this;
	}

	public JavaCommandBuilder addArgs(String args) {
		this.args.add(args);
		return this;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("java");

		if (classPaths.size() > 0) {
			sb.append(" -cp ");
			int i = 0;
			for (String classPath : classPaths) {
				if (!Strings.isNullOrEmpty(classPath.trim())) {
					if (i > 0) {
						sb.append(System.getProperty("path.separator"));
					}
					sb.append(classPath.trim());
					i++;
				}
			}
		}

		for (String arg : javaArgs) {
			if (!Strings.isNullOrEmpty(arg)) {
				sb.append(" ");
				sb.append(arg);
			}
		}

		sb.append(" ");
		sb.append(mainClass);

		for (String arg : args) {
			if (!Strings.isNullOrEmpty(arg)) {
				sb.append(" ");
				sb.append(arg);
			}
		}

		return sb.toString();
	}

}
