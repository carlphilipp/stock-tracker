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

package fr.cph.stock.web.servlet.user;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import fr.cph.stock.entities.User;

/**
 * This servlet is called when the user want to logout
 * 
 * @author Carl-Philipp Harmant
 * 
 */
@WebServlet(name = "LogOutServlet", urlPatterns = { "/logout" })
public class LogOutServlet extends HttpServlet {

	/** Serialization **/
	private static final long serialVersionUID = -6107097536653860984L;

	/** Logger **/
	private static final Logger LOG = Logger.getLogger(LogOutServlet.class);

	/**
	 * Get method
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the answer
	 * @throws ServletException
	 *             the exception
	 */
	@Override
	protected final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		try {
			HttpSession session = request.getSession(false);
			if (session != null) {
				User user = (User) session.getAttribute("User");
				if (user != null) {
					user = null;
				}
				session.invalidate();
			}
			response.sendRedirect("index.jsp");
		} catch (Throwable t) {
			LOG.error(t.getMessage(), t);
			throw new ServletException("Error: " + t.getMessage(), t);
		}
	}

	/**
	 * Post method
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the answer
	 * @throws ServletException
	 *             the servlet exception
	 */
	@Override
	protected final void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		doGet(request, response);
	}
}
