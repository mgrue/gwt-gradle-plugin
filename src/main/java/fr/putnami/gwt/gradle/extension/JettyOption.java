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

import java.io.File;

public class JettyOption extends JavaOptions {

	/**
	 * jetty xml conf.
	 */
	private File jettyConf;
	/**
	 * war to deploy.
	 */
	private File war;
	/**
	 * interface to listen on.
	 */
	private String bindAddress = "";
	/**
	 * request log filename.
	 */
	private File logRequestFile;
	/**
	 * info/warn/debug log filename
	 */
	private File logFile;
	/**
	 * port to listen on.
	 */
	private int port = 8080;
	/**
	 * port to listen for stop command.
	 */
	private int stopPort = 8089;
	/**
	 * security string for stop command.
	 */
	private String stopKey = "JETTY-STOP";

	public File getJettyConf() {
		return jettyConf;
	}

	public void setJettyConf(File jettyConf) {
		this.jettyConf = jettyConf;
	}

	public void jettyConf(String jettyConf) {
		this.jettyConf = new File(jettyConf);
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

	public String getBindAddress() {
		return bindAddress;
	}

	public void bindAddress(String bindAddress) {
		this.bindAddress = bindAddress;
	}

	public File getLogRequestFile() {
		return logRequestFile;
	}

	public void setLogRequestFile(File logRequestFile) {
		this.logRequestFile = logRequestFile;
	}

	public void logRequestFile(String logRequestFile) {
		this.logRequestFile = new File(logRequestFile);
	}

	public File getLogFile() {
		return logFile;
	}

	public void setLogFile(File logFile) {
		this.logFile = logFile;
	}

	public void logFile(String logFile) {
		this.logFile = new File(logFile);
	}

	public int getPort() {
		return port;
	}

	public void port(int port) {
		this.port = port;
	}

	public int getStopPort() {
		return stopPort;
	}

	public void stopPort(int stopPort) {
		this.stopPort = stopPort;
	}

	public String getStopKey() {
		return stopKey;
	}

	public void stopKey(String stopKey) {
		this.stopKey = stopKey;
	}

}
