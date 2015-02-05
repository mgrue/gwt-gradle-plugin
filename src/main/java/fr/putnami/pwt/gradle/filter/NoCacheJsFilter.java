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
package fr.putnami.pwt.gradle.filter;


import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import java.io.IOException;
import java.net.URL;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class NoCacheJsFilter implements Filter {

	private static final String PARAM_CODE_SERVER_PORT = "CODE_SERVER_PORT";

	private String port = "9876";

	@Override
	public void init(FilterConfig paramFilterConfig) throws ServletException {
		port = paramFilterConfig.getInitParameter(PARAM_CODE_SERVER_PORT);

	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
		FilterChain chain)
		throws IOException, ServletException {
		if (request instanceof HttpServletRequest) {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			String uri = httpRequest.getRequestURI();
			String moduleName = uri.substring(uri.lastIndexOf("/") + 1).replaceAll(".nocache.js", "");
			System.out.println(moduleName);
			URL url = getClass().getResource("/stub.nocache.js");
			String template = Resources.toString(url, Charsets.UTF_8);
			template = template
				.replace("__MODULE_NAME__", moduleName)
				.replace("__SUPERDEV_PORT__", port);
			response.getWriter().write(template);
			response.flushBuffer();
		}
	}

}
