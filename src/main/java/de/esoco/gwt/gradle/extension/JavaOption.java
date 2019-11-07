/**
 * This file is part of gwt-gradle-plugin.
 *
 * gwt-gradle-plugin is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * gwt-gradle-plugin is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with gwt-gradle-plugin. If not,
 * see <http://www.gnu.org/licenses/>.
 */
package de.esoco.gwt.gradle.extension;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;


public class JavaOption {

	private final List<String> javaArgs = Lists.newArrayList();

	private String maxHeapSize;
	private String minHeapSize;
	private String maxPermSize;

	private int     debugPort    = 8000;
	private boolean debugJava    = false;
	private boolean debugSuspend = false;

	private boolean envClasspath = false;

	public int getDebugPort() {

		return debugPort;
	}

	public List<String> getJavaArgs() {

		return javaArgs;
	}

	public String getMaxHeapSize() {

		return maxHeapSize;
	}

	public String getMaxPermSize() {

		return maxPermSize;
	}

	public String getMinHeapSize() {

		return minHeapSize;
	}

	public boolean isDebugJava() {

		return debugJava;
	}

	public boolean isDebugSuspend() {

		return debugSuspend;
	}

	public boolean isEnvClasspath() {

		return envClasspath;
	}

	public void setDebugJava(boolean debugJava) {

		this.debugJava = debugJava;
	}

	public void setDebugPort(int debugPort) {

		this.debugPort = debugPort;
	}

	public void setDebugPort(String debugPort) {

		this.debugPort = Integer.valueOf(debugPort);
	}

	public void setDebugSuspend(boolean debugSuspend) {

		this.debugSuspend = debugSuspend;
	}

	public void setDebugSuspend(String debugSuspend) {

		this.debugSuspend = Boolean.valueOf(debugSuspend);
	}

	public void setEnvClasspath(boolean envClasspath) {

		this.envClasspath = envClasspath;
	}

	public void setJavaArgs(String... javaArgs) {

		this.javaArgs.addAll(Arrays.asList(javaArgs));
	}

	public void setMaxHeapSize(String maxHeapSize) {

		this.maxHeapSize = maxHeapSize;
	}

	public void setMaxPermSize(String maxPermSize) {

		this.maxPermSize = maxPermSize;
	}

	public void setMinHeapSize(String minHeapSize) {

		this.minHeapSize = minHeapSize;
	}
}
