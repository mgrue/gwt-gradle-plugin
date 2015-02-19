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
package fr.putnami.gwt.gradle.extension;

import org.gradle.api.Project;

import java.io.File;

public class DevOption extends JavaOption {

	/**
	 * The ip address of the code server.
	 */
	private String bindAddress;
	/**
	 * Stop compiling if a module has a Java file with a compile error, even if unused.
	 */
	private Boolean noServer;
	/**
	 * Exits after compiling the modules. The exit code will be 0 if the compile succeeded
	 */
	private Boolean compileTest;
	/**
	 * The number of times to recompile (after the first one) during a compile test.
	 */
	private Integer compileTestRecompiles;
	/**
	 * Stop compiling if a module has a Java file with a compile error, even if unused.
	 */
	private Boolean failOnError;
	/**
	 * Precompile modules.
	 */
	private Boolean precompile = false;
	/**
	 * The port where the code server will run.
	 */
	private Integer port = 9876;
	/**
	 * EXPERIMENTAL: Don't implicitly depend on "client" and "public" when a module doesn't define any
	 * dependencies.
	 */
	private Boolean enforceStrictResources;
	/**
	 * The root of the directory tree where the code server willwrite compiler output. If not
	 * supplied, a temporary directorywill be used.
	 */
	private File workDir;
	/**
	 * Compiles faster by reusing data from the previous compile.
	 */
	private Boolean incremental;
	/**
	 * Specifies Java source level.
	 */
	private String sourceLevel = "";
	/**
	 * The level of logging detail.
	 */
	private LogLevel logLevel = LogLevel.ERROR;
	/**
	 * Specifies JsInterop mode.
	 */
	private JsInteropMode jsInteropMode;
	/**
	 * Emit extra information allow chrome dev tools to display Java identifiers in many places
	 * instead of JavaScript functions.
	 */
	private MethodNameDisplayMode methodNameDisplayMode;

	private File war;

	public String getBindAddress() {
		return bindAddress;
	}

	public void setBindAddress(String bindAddress) {
		this.bindAddress = bindAddress;
	}

	public Boolean getNoServer() {
		return noServer;
	}

	public void setNoServer(Boolean noServer) {
		this.noServer = noServer;
	}

	public Boolean getCompileTest() {
		return compileTest;
	}

	public void setCompileTest(Boolean compileTest) {
		this.compileTest = compileTest;
	}

	public Integer getCompileTestRecompiles() {
		return compileTestRecompiles;
	}

	public void setCompileTestRecompiles(Integer compileTestRecompiles) {
		this.compileTestRecompiles = compileTestRecompiles;
	}

	public Boolean getFailOnError() {
		return failOnError;
	}

	public void setFailOnError(Boolean failOnError) {
		this.failOnError = failOnError;
	}

	public Boolean getPrecompile() {
		return precompile;
	}

	public void setPrecompile(Boolean precompile) {
		this.precompile = precompile;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public Boolean getEnforceStrictResources() {
		return enforceStrictResources;
	}

	public void setEnforceStrictResources(Boolean enforceStrictResources) {
		this.enforceStrictResources = enforceStrictResources;
	}

	public File getWorkDir() {
		return workDir;
	}

	public void setWorkDir(File workDir) {
		this.workDir = workDir;
	}

	public void setWorkDir(String workDir) {
		this.workDir = new File(workDir);
	}

	public Boolean getIncremental() {
		return incremental;
	}

	public void setIncremental(Boolean incremental) {
		this.incremental = incremental;
	}

	public String getSourceLevel() {
		return sourceLevel;
	}

	public void setSourceLevel(String sourceLevel) {
		this.sourceLevel = sourceLevel;
	}

	public LogLevel getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(String logLevel) {
		this.logLevel = LogLevel.valueOf(logLevel);
	}

	public JsInteropMode getJsInteropMode() {
		return jsInteropMode;
	}

	public void setJsInteropMode(String jsInteropMode) {
		this.jsInteropMode = JsInteropMode.valueOf(jsInteropMode);
	}

	public MethodNameDisplayMode getMethodNameDisplayMode() {
		return methodNameDisplayMode;
	}

	public void setMethodNameDisplayMode(String methodNameDisplayMode) {
		this.methodNameDisplayMode = MethodNameDisplayMode.valueOf(methodNameDisplayMode);
	}

	public File getWar() {
		return war;
	}

	public void setWar(File war) {
		this.war = war;
	}

	public void init(Project project) {
		final File buildDir = new File(project.getBuildDir(), "putnami");

		setWar(new File(buildDir, "warDev"));
		setWorkDir(new File(buildDir, "work"));
	}

}
