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

import org.gradle.util.ConfigureUtil;

import java.util.Arrays;
import java.util.List;

import groovy.lang.Closure;

public class PutnamiExtension {
	public static final String PWT_EXTENSION = "putnami";

	private String gwtVersion = "2.7.0";
	private boolean gwtServletLib = false;
	private boolean gwtDevLib = false;
	private boolean gwtElementalLib = false;

	private String jettyVersion = "9.2.7.v20150116";

	/**
	 * GWT Module to compile.
	 */
	private List<String> module = Lists.newArrayList();

	private CompilerOptions compile = new CompilerOptions();
	private CodeServerOption dev = new CodeServerOption();
	private JettyOption jetty = new JettyOption();

	public String getGwtVersion() {
		return gwtVersion;
	}

	public void setGwtVersion(String gwtVersion) {
		this.gwtVersion = gwtVersion;
	}

	public String getJettyVersion() {
		return jettyVersion;
	}

	public void setJettyVersion(String jettyVersion) {
		this.jettyVersion = jettyVersion;
	}

	public boolean isGwtServletLib() {
		return gwtServletLib;
	}

	public void setGwtServletLib(boolean gwtServletLib) {
		this.gwtServletLib = gwtServletLib;
	}

	public boolean isGwtDevLib() {
		return gwtDevLib;
	}

	public void setGwtDevLib(boolean gwtDevLib) {
		this.gwtDevLib = gwtDevLib;
	}

	public boolean isGwtElementalLib() {
		return gwtElementalLib;
	}

	public void setGwtElementalLib(boolean gwtElementalLib) {
		this.gwtElementalLib = gwtElementalLib;
	}

	public CodeServerOption getDev() {
		return dev;
	}

	public void setDev(CodeServerOption dev) {
		this.dev = dev;
	}

	public PutnamiExtension dev(Closure<CodeServerOption> c) {
		ConfigureUtil.configure(c, dev);
		return this;
	}

	public CompilerOptions getCompile() {
		return compile;
	}

	public void setCompile(CompilerOptions compile) {
		this.compile = compile;
	}

	public PutnamiExtension compile(Closure<CompilerOptions> c) {
		ConfigureUtil.configure(c, compile);
		return this;
	}

	public JettyOption getJetty() {
		return jetty;
	}

	public void setJetty(JettyOption jetty) {
		this.jetty = jetty;
	}

	public PutnamiExtension jetty(Closure<JettyOption> c) {
		ConfigureUtil.configure(c, jetty);
		return this;
	}

	public List<String> getModule() {
		return module;
	}

	public void module(String... module) {
		this.module.addAll(Arrays.asList(module));
	}

}
