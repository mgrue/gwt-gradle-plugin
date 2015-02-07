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

import org.gradle.util.ConfigureUtil;

import groovy.lang.Closure;

public class PwtExtension {
	public static final String PWT_EXTENSION = "pwt";

	private String gwtVersion = "2.7.0";

	private CompilerOptions compile = new CompilerOptions();
	private CodeServerOption dev = new CodeServerOption();

	public String getGwtVersion() {
		return gwtVersion;
	}

	public void setGwtVersion(String gwtVersion) {
		this.gwtVersion = gwtVersion;
	}

	public CodeServerOption getDev() {
		return dev;
	}

	public void setDev(CodeServerOption dev) {
		this.dev = dev;
	}

	public PwtExtension dev(Closure<CodeServerOption> c) {
		ConfigureUtil.configure(c, dev);
		return this;
	}

	public CompilerOptions getCompile() {
		return compile;
	}

	public void setCompile(CompilerOptions compile) {
		this.compile = compile;
	}

	public PwtExtension compile(Closure<CompilerOptions> c) {
		ConfigureUtil.configure(c, compile);
		return this;
	}

}
