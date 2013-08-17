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

/**
 * This classes is called each time the user try to access a page from mobile that needs to be logged in (basicely, every single
 * page) It checks if the session is valid or not
 * 
 * @author Carl-Philipp Harmant
 * 
 */
public class SessionMobileFilter implements Filter {

	@Override
	public final void init(final FilterConfig config) throws ServletException {

	}

	@Override
	public final void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain)
			throws ServletException {
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
	public final void destroy() {
	}

}
