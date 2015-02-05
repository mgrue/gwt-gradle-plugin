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

public class CodeServerOption {

	private String bindAddress;
	private boolean precompile = false;
	private int port = 9876;
	private String[] src;
	private boolean incremental = true;
	private LogLevel logLevel = LogLevel.WARN;
	private JsInteropMode jsInterop = JsInteropMode.NONE;
	private String[] module;

	public String getBindAddress() {
		return bindAddress;
	}

	public void setBindAddress(String bindAddress) {
		this.bindAddress = bindAddress;
	}

	public boolean isPrecompile() {
		return precompile;
	}

	public void setPrecompile(boolean precompile) {
		this.precompile = precompile;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String[] getSrc() {
		return src;
	}

	public void setSrc(String[] src) {
		this.src = src;
	}

	public boolean isIncremental() {
		return incremental;
	}

	public void setIncremental(boolean incremental) {
		this.incremental = incremental;
	}

	public LogLevel getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(LogLevel logLevel) {
		this.logLevel = logLevel;
	}

	public JsInteropMode getJsInterop() {
		return jsInterop;
	}

	public void setJsInterop(JsInteropMode jsInterop) {
		this.jsInterop = jsInterop;
	}

	public String[] getModule() {
		return module;
	}

	public void setModule(String[] module) {
		this.module = module;
	}
}
