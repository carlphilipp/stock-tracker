/**
 * Copyright 2013 Carl-Philipp Harmant
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.cph.stock.web.servlet.mobile;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import fr.cph.stock.entities.User;

public class SessionMobileFilter  implements Filter {
	
	//	private static final Logger log = Logger.getLogger(SessionMobileFilter.class);
	
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws ServletException {
		try {
			HttpServletRequest request = (HttpServletRequest) req;
			HttpServletResponse response = (HttpServletResponse) res;
			HttpSession session = request.getSession(false);

			if (session == null) {
				response.setContentType("application/json");
				response.getWriter().write("{\"error\":\"No active session\"}");
			} else {
				User user = (User) session.getAttribute("user");
				if (user == null) {
					response.setContentType("application/json");
					response.getWriter().write("{\"error\":\"User session not found\"}");
				} else {
					chain.doFilter(req, res);
				}
			}
		} catch (Throwable t) {
			throw new ServletException("Error: " + t.getMessage(), t);
		}
	}

	@Override
	public void destroy() {

	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}
}
