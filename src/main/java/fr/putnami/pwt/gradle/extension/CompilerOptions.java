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

public class CompilerOptions {
	// -logLevel The level of logging detail: ERROR, WARN, INFO, TRACE, DEBUG, SPAM, or ALL
//-workDir                         The compiler's working directory for internal use (must be writeable; defaults to a system temp dir)
//-[no]compileReport               Compile a report that tells the "Story of Your Compile". (defaults to OFF)
//-X[no]checkCasts                 EXPERIMENTAL: Insert run-time checking of cast operations. (defaults to ON)
//-X[no]classMetadata              EXPERIMENTAL: Include metadata for some java.lang.Class methods (e.g. getName()). (defaults to ON)
//-[no]draftCompile                Compile quickly with minimal optimizations. (defaults to OFF)
//-[no]checkAssertions             Include assert statements in compiled output. (defaults to OFF)
//-X[no]closureCompiler            EXPERIMENTAL: Compile output Javascript with the Closure compiler for even further optimizations. (defaults to OFF)
//-XfragmentCount                  EXPERIMENTAL: Limits of number of fragments using a code splitter that merges split points.
//-XfragmentMerge                  DEPRECATED (use -XfragmentCount instead): Enables Fragment merging code splitter.
//-gen                             Debugging: causes normally-transient generated types to be saved in the specified directory
//-[no]incrementalCompileWarnings  Whether to show warnings during monolithic compiles for issues that will break in incremental compiles (strict compile errors, strict source directory inclusion, missing dependencies). (defaults to OFF)
//-XjsInteropMode                  Specifies JsInterop mode, either NONE, JS, or CLOSURE (defaults to NONE)
//-missingDepsFile                 Specifies a file into which detailed missing dependency information will be written.
//-Xnamespace                      Puts most JavaScript globals into namespaces. Default: PACKAGE for -draftCompile, otherwise NONE
//-optimize                        Sets the optimization level used by the compiler.  0=none 9=maximum.
//-[no]overlappingSourceWarnings   Whether to show warnings during monolithic compiles for overlapping source inclusion. (defaults to OFF)
//-[no]saveSource                  Enables saving source code needed by debuggers. Also see -debugDir. (defaults to OFF)
//-style                           Script output style: OBF[USCATED], PRETTY, or DETAILED (defaults to OBF)
//-[no]failOnError                 Fail compilation if any input file contains an error. (defaults to OFF)
//-X[no]enforceStrictResources     EXPERIMENTAL: Avoid adding implicit dependencies on "client" and "public" for modules that don't define any dependencies. (defaults to OFF)
//-[no]validateOnly                Validate all source code, but do not compile. (defaults to OFF)
//-sourceLevel                     Specifies Java source level (defaults to auto:1.7)
//-localWorkers                    The number of local workers to use when compiling permutations
//-[no]incremental                 Compiles faster by reusing data from the previous compile. (defaults to OFF)
//-war                             The directory into which deployable output files will be written (defaults to 'war')
//-deploy                          The directory into which deployable but not servable output files will be written (defaults to 'WEB-INF/deploy' under the -war directory/jar, and may be the same as the -extra directory/jar)
//-extra                           The directory into which extra files, not intended for deployment, will be written
//-saveSourceOutput                Overrides where source files useful to debuggers will be written. Default: saved with extras.
//-XmethodNameDisplayMode          Emit extra information allow chrome dev tools to display Java identifiers in many places instead of JavaScript functions.
//and
//module[s]                        Specifies the name(s) of the module(s) to compile

}
