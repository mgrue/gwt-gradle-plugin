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

import com.google.common.collect.Lists;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class CodeServerOption extends JavaOptions {

	/**
	 * Allows -src flags to reference missing directories.
	 */
	private boolean allowMissingSrc = false;
	/**
	 * The ip address of the code server.
	 */
	private String bindAddress = "127.0.0.1";
	/**
	 * Exits after compiling the modules. The exit code will be 0 if the compile succeeded
	 */
	private boolean compileTest = false;
	/**
	 * The number of times to recompile (after the first one) during a compile test.
	 */
	private int compileTestRecompiles = 1000;
	/**
	 * Stop compiling if a module has a Java file with a compile error, even if unused.
	 */
	private boolean failOnError = false;
	/**
	 * Precompile modules.
	 */
	private boolean precompile = false;
	/**
	 * The port where the code server will run.
	 */
	private int port = 9876;
	/**
	 * A directory containing GWT source to be prepended to the classpath for compiling.
	 */
	private List<String> src = Lists.newArrayList();
	/**
	 * EXPERIMENTAL: Don't implicitly depend on "client" and "public" when a module doesn't define any
	 * dependencies.
	 */
	private boolean enforceStrictResources = false;
	/**
	 * The root of the directory tree where the code server willwrite compiler output. If not
	 * supplied, a temporary directorywill be used.
	 */
	private File workDir;
	/**
	 * An output directory where files for launching Super Dev Mode will be written.
	 */
	private File launcherDir;
	/**
	 * Compiles faster by reusing data from the previous compile.
	 */
	private boolean incremental = true;
	/**
	 * Specifies Java source level.
	 */
	private String sourceLevel = "";
	/**
	 * The level of logging detail.
	 */
	private LogLevel logLevel = LogLevel.WARN;
	/**
	 * Specifies JsInterop mode.
	 */
	private JsInteropMode jsInteropMode = JsInteropMode.NONE;
	/**
	 * Emit extra information allow chrome dev tools to display Java identifiers in many places
	 * instead of JavaScript functions.
	 */
	private MethodNameDisplayMode methodNameDisplayMode = MethodNameDisplayMode.NONE;

	public boolean isAllowMissingSrc() {
		return allowMissingSrc;
	}

	public void allowMissingSrc(boolean allowMissingSrc) {
		this.allowMissingSrc = allowMissingSrc;
	}

	public String getBindAddress() {
		return bindAddress;
	}

	public void bindAddress(String bindAddress) {
		this.bindAddress = bindAddress;
	}

	public boolean isCompileTest() {
		return compileTest;
	}

	public void compileTest(boolean compileTest) {
		this.compileTest = compileTest;
	}

	public int getCompileTestRecompiles() {
		return compileTestRecompiles;
	}

	public void compileTestRecompiles(int compileTestRecompiles) {
		this.compileTestRecompiles = compileTestRecompiles;
	}

	public boolean isFailOnError() {
		return failOnError;
	}

	public void failOnError(boolean failOnError) {
		this.failOnError = failOnError;
	}

	public boolean isPrecompile() {
		return precompile;
	}

	public void precompile(boolean precompile) {
		this.precompile = precompile;
	}

	public int getPort() {
		return port;
	}

	public void port(int port) {
		this.port = port;
	}

	public List<String> getSrc() {
		return src;
	}

	public void src(String... src) {
		this.src.addAll(Arrays.asList(src));
	}

	public boolean isEnforceStrictResources() {
		return enforceStrictResources;
	}

	public void enforceStrictResources(boolean enforceStrictResources) {
		this.enforceStrictResources = enforceStrictResources;
	}

	public File getWorkDir() {
		return workDir;
	}

	public void setWorkDir(File workDir) {
		this.workDir = workDir;
	}

	public void workDir(String workDir) {
		this.workDir = new File(workDir);
	}

	public File getLauncherDir() {
		return launcherDir;
	}

	public void setLauncherDir(File launcherDir) {
		this.launcherDir = launcherDir;
	}

	public void launcherDir(String launcherDir) {
		this.launcherDir = new File(launcherDir);
	}

	public boolean isIncremental() {
		return incremental;
	}

	public void incremental(boolean incremental) {
		this.incremental = incremental;
	}

	public String getSourceLevel() {
		return sourceLevel;
	}

	public void sourceLevel(String sourceLevel) {
		this.sourceLevel = sourceLevel;
	}

	public LogLevel getLogLevel() {
		return logLevel;
	}

	public void logLevel(String logLevel) {
		this.logLevel = LogLevel.valueOf(logLevel);
	}

	public JsInteropMode getJsInteropMode() {
		return jsInteropMode;
	}

	public void jsInteropMode(String jsInteropMode) {
		this.jsInteropMode = JsInteropMode.valueOf(jsInteropMode);
	}

	public MethodNameDisplayMode getMethodNameDisplayMode() {
		return methodNameDisplayMode;
	}

	public void methodNameDisplayMode(String methodNameDisplayMode) {
		this.methodNameDisplayMode = MethodNameDisplayMode.valueOf(methodNameDisplayMode);
	}

}
