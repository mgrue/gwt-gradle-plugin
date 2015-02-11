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
package fr.putnami.gwt.gradle.filter;


import com.google.common.io.ByteStreams;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class NoCacheJsFilter implements Filter {

	private static final String PARAM_LAUNCHER_DIR = "LAUNCHER_DIR";

	private String launcherDir = "";

	@Override
	public void init(FilterConfig paramFilterConfig) throws ServletException {
		launcherDir = paramFilterConfig.getInitParameter(PARAM_LAUNCHER_DIR);

	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
		FilterChain chain)
		throws IOException, ServletException {
		if (request instanceof HttpServletRequest) {
			String uri = ((HttpServletRequest) request).getRequestURI();
			String moduleName = uri.substring(uri.lastIndexOf("/") + 1).replaceAll(".nocache.js", "");
			File noCacheFile = new File(launcherDir + "/" + moduleName + "/" + moduleName + ".nocache.js");
			FileInputStream launcherStream = new FileInputStream(noCacheFile);
			ByteStreams.copy(launcherStream, response.getOutputStream());
			response.flushBuffer();
		}
	}

}
