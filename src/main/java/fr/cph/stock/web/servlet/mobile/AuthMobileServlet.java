/**
 * Copyright 2016 Carl-Philipp Harmant
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.cph.stock.web.servlet.mobile;

import fr.cph.stock.business.UserBusiness;
import fr.cph.stock.business.impl.UserBusinessImpl;
import fr.cph.stock.entities.User;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static fr.cph.stock.util.Constants.*;

/**
 * This servlet is called by mobile to connect to the app
 *
 * @author Carl-Philipp Harmant
 */
@WebServlet(name = "AuthMobileServlet", urlPatterns = {"/authmobile"})
public class AuthMobileServlet extends HttpServlet {

	private static final long serialVersionUID = -7713821485113054118L;
	private static final Logger LOG = Logger.getLogger(AuthMobileServlet.class);
	private UserBusiness userBusiness;

	@Override
	public final void init() {
		userBusiness = UserBusinessImpl.INSTANCE;
	}

	@Override
	protected final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		try {
			request.getSession().invalidate();
			final String login = request.getParameter(LOGIN);
			final String password = request.getParameter(PASSWORD);
			final User user = userBusiness.checkUser(login, password);
			if (user == null) {
				response.setContentType("application/json");
				response.getWriter().write("{\"error\":\"Login or password unknown\"}");
			} else {
				if (!user.getAllow()) {
					response.setContentType("application/json");
					response.getWriter().write("{\"error\":\"User not allowed}\"");
				} else {
					request.getSession().setAttribute(USER, user);
					response.sendRedirect(HOMEMOBILE);
				}
			}
		} catch (final Throwable t) {
			LOG.error("Error: " + t.getMessage(), t);
			throw new ServletException("Error: " + t.getMessage(), t);
		}
	}

	@Override
	protected final void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		doGet(request, response);
	}
}
