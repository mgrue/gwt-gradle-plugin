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
package fr.putnami.pwt.gradle.extension;

import com.google.common.collect.Lists;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * GWT Compiler options.
 */
public class CompilerOptions extends JavaOptions {

	/**
	 * The compiler's working directory for internal use (must be writeable; defaults to a system
	 * buil/work)
	 */
	private File workDir;
	/**
	 * The directory into which deployable output files will be written.
	 */
	private File war;
	/**
	 * The directory into which deployable but not servable output files will be written (defaults to
	 * 'WEB-INF/deploy' under the -war directory/jar, and may be the same as the -extra
	 * directory/jar).
	 */
	private File deploy;
	/**
	 * Debugging: causes normally-transient generated types to be saved in the specified directory
	 */
	private File gen;
	/**
	 * The directory into which extra files, not intended for deployment, will be written.
	 */
	private File extra;
	/**
	 * Overrides where source files useful to debuggers will be written. Default: saved with extras.
	 */
	private File saveSourceOutput;
	/**
	 * Specifies a file into which detailed missing dependency information will be written.
	 */
	private File missingDepsFile;
	/**
	 * GWT Module to compile.
	 */
	private List<String> module = Lists.newArrayList();
	/**
	 * The level of logging detail
	 */
	private LogLevel logLevel = LogLevel.WARN;
	/**
	 * Compile a report that tells the "Story of Your Compile"
	 */
	private boolean compileReport = false;
	/**
	 * Compile quickly with minimal optimizations.
	 */
	private boolean draftCompile = false;
	/**
	 * Include assert statements in compiled output.
	 */
	private boolean checkAssertions = false;
	/**
	 * Script output style
	 */
	private CodeStyle style = CodeStyle.OBF;
	/**
	 * Sets the optimization level used by the compiler. 0=none 9=maximum.
	 */
	private int optimize = 9;
	/**
	 * Whether to show warnings during monolithic compiles for overlapping source inclusion.
	 */
	private boolean overlappingSourceWarnings = false;
	/**
	 * Enables saving source code needed by debuggers. Also see -debugDir.
	 */
	private boolean saveSource = false;
	/**
	 * Fail compilation if any input file contains an error.
	 */
	private boolean failOnError = false;
	/**
	 * Validate all source code, but do not compile.
	 */
	private boolean validateOnly = false;
	/**
	 * Specifies Java source level
	 */
	private String sourceLevel = "";
	/**
	 * The number of local workers to use when compiling permutations.
	 */
	private int localWorkers;
	/**
	 * Compiles faster by reusing data from the previous compile.
	 */
	private boolean incremental = false;
	/**
	 * Emit extra information allow chrome dev tools to display Java identifiers in many places
	 * instead of JavaScript functions.
	 */
	private MethodNameDisplayMode methodNameDisplayMode = MethodNameDisplayMode.NONE;
	/**
	 * EXPERIMENTAL: Avoid adding implicit dependencies on "client" and "public" for modules that
	 * don't define any dependencies.
	 */
	private boolean enforceStrictResources = false;
	/**
	 * EXPERIMENTAL: Insert run-time checking of cast operations.
	 */
	private boolean checkCasts = true;
	/**
	 * EXPERIMENTAL: Include metadata for some java.lang.Class methods (e.g. getName()).
	 */
	private boolean classMetadata = true;
	/**
	 * EXPERIMENTAL: Compile output Javascript with the Closure compiler for even further
	 * optimizations.
	 */
	private boolean closureCompiler = false;
	/**
	 * Specifies JsInterop mode, either NONE, JS, or CLOSURE
	 */
	private JsInteropMode jsInteropMode = JsInteropMode.NONE;

	public LogLevel getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(LogLevel logLevel) {
		this.logLevel = logLevel;
	}

	public void logLevel(String logLevel) {
		this.logLevel = LogLevel.valueOf(logLevel);
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

	public boolean isCompileReport() {
		return compileReport;
	}

	public void compileReport(boolean compileReport) {
		this.compileReport = compileReport;
	}

	public boolean isDraftCompile() {
		return draftCompile;
	}

	public void draftCompile(boolean draftCompile) {
		this.draftCompile = draftCompile;
	}

	public boolean isCheckAssertions() {
		return checkAssertions;
	}

	public void checkAssertions(boolean checkAssertions) {
		this.checkAssertions = checkAssertions;
	}

	public File getGen() {
		return gen;
	}

	public void setGen(File gen) {
		this.gen = gen;
	}

	public void gen(String gen) {
		this.gen = new File(gen);
	}

	public File getMissingDepsFile() {
		return missingDepsFile;
	}

	public void missingDepsFile(String missingDepsFile) {
		this.missingDepsFile = new File(missingDepsFile);
	}

	public void setMissingDepsFile(File missingDepsFile) {
		this.missingDepsFile = missingDepsFile;
	}

	public int getOptimize() {
		return optimize;
	}

	public void optimize(int optimize) {
		this.optimize = optimize;
	}

	public boolean isOverlappingSourceWarnings() {
		return overlappingSourceWarnings;
	}

	public void overlappingSourceWarnings(boolean overlappingSourceWarnings) {
		this.overlappingSourceWarnings = overlappingSourceWarnings;
	}

	public boolean isSaveSource() {
		return saveSource;
	}

	public void saveSource(boolean saveSource) {
		this.saveSource = saveSource;
	}

	public CodeStyle getStyle() {
		return style;
	}

	public void style(String style) {
		this.style = CodeStyle.valueOf(style);
	}

	public boolean isFailOnError() {
		return failOnError;
	}

	public void failOnError(boolean failOnError) {
		this.failOnError = failOnError;
	}

	public boolean isValidateOnly() {
		return validateOnly;
	}

	public void validateOnly(boolean validateOnly) {
		this.validateOnly = validateOnly;
	}

	public String getSourceLevel() {
		return sourceLevel;
	}

	public void sourceLevel(String sourceLevel) {
		this.sourceLevel = sourceLevel;
	}

	public int getLocalWorkers() {
		return localWorkers;
	}

	public void localWorkers(int localWorkers) {
		this.localWorkers = localWorkers;
	}

	public boolean isIncremental() {
		return incremental;
	}

	public void incremental(boolean incremental) {
		this.incremental = incremental;
	}

	public File getWar() {
		return war;
	}

	public void setWar(File war) {
		this.war = war;
	}

	public void war(String war) {
		this.war = new File(war);
	}

	public File getDeploy() {
		return deploy;
	}

	public void setDeploy(File deploy) {
		this.deploy = deploy;
	}

	public void deploy(String deploy) {
		this.deploy = new File(deploy);
	}

	public File getExtra() {
		return extra;
	}

	public void setExtra(File extra) {
		this.extra = extra;
	}

	public void extra(String extra) {
		this.extra = new File(extra);
	}


	public File getSaveSourceOutput() {
		return saveSourceOutput;
	}

	public void setSaveSourceOutput(File saveSourceOutput) {
		this.saveSourceOutput = saveSourceOutput;
	}

	public void saveSourceOutput(String saveSourceOutput) {
		this.saveSourceOutput = new File(saveSourceOutput);
	}

	public MethodNameDisplayMode getMethodNameDisplayMode() {
		return methodNameDisplayMode;
	}

	public void setMethodNameDisplayMode(MethodNameDisplayMode methodNameDisplayMode) {
		this.methodNameDisplayMode = methodNameDisplayMode;
	}

	public void methodNameDisplayMode(String methodNameDisplayMode) {
		this.methodNameDisplayMode = MethodNameDisplayMode.valueOf(methodNameDisplayMode);
	}

	public List<String> getModule() {
		return module;
	}

	public void module(String... module) {
		this.module.addAll(Arrays.asList(module));
	}

	public boolean isEnforceStrictResources() {
		return enforceStrictResources;
	}

	public void enforceStrictResources(boolean enforceStrictResources) {
		this.enforceStrictResources = enforceStrictResources;
	}

	public boolean isCheckCasts() {
		return checkCasts;
	}

	public void checkCasts(boolean checkCasts) {
		this.checkCasts = checkCasts;
	}

	public boolean isClassMetadata() {
		return classMetadata;
	}

	public void classMetadata(boolean classMetadata) {
		this.classMetadata = classMetadata;
	}

	public boolean isClosureCompiler() {
		return closureCompiler;
	}

	public void closureCompiler(boolean closureCompiler) {
		this.closureCompiler = closureCompiler;
	}

	public JsInteropMode getJsInteropMode() {
		return jsInteropMode;
	}

	public void jsInteropMode(String jsInteropMode) {
		this.jsInteropMode = JsInteropMode.valueOf(jsInteropMode);
	}

}
