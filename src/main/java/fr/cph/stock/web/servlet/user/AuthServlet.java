/**
 * Copyright 2013 Carl-Philipp Harmant
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

package fr.cph.stock.web.servlet.user;

import fr.cph.stock.business.Business;
import fr.cph.stock.business.IBusiness;
import fr.cph.stock.entities.User;
import fr.cph.stock.web.servlet.CookieManagement;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static fr.cph.stock.util.Constants.*;

/**
 * This servlet is called when the user want to login
 *
 * @author Carl-Philipp Harmant
 */
@WebServlet(name = "AuthServlet", urlPatterns = {"/auth"})
public class AuthServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(AuthServlet.class);
	private IBusiness business;
	private static final int ONE_YEAR_COOKIE = 60 * 60 * 24 * 365;
	private List<String> lcookies;

	@Override
	public final void init() {
		business = Business.getInstance();
		lcookies = new ArrayList<>();
		lcookies.add(QUOTE);
		lcookies.add(CURRENCY);
		lcookies.add(PARITY);
		lcookies.add(STOP_LOSS);
		lcookies.add(OBJECTIVE);
		lcookies.add(YIELD_1);
		lcookies.add(YIELD_2);
		lcookies.add(AUTO_UPDATE);
	}

	@Override
	protected final void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		try {
			request.getSession().invalidate();
			final String login = request.getParameter(LOGIN);
			final String password = request.getParameter(PASSWORD);
			final User user = business.checkUser(login, password);
			if (user == null) {
				request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
			} else {
				if (!user.getAllow()) {
					request.getSession().setAttribute(ERROR, "Account not confirmed. Check your email!");
					request.getRequestDispatcher("index.jsp").forward(request, response);
				} else {
					request.getSession().setAttribute(USER, user);
					if (request.getCookies() != null) {
						final List<Cookie> cookies = Arrays.asList(request.getCookies());
						for (final String cookieName : lcookies) {
							if (!CookieManagement.containsCookie(cookies, cookieName)) {
								addCookieToResponse(response, cookieName, CHECKED);
							}
						}
						if (!CookieManagement.containsCookie(cookies, LANGUAGE)) {
							addCookieToResponse(response, LANGUAGE, ENGLISH);
						}
					} else {
						for (final String cookieName : lcookies) {
							addCookieToResponse(response, cookieName, CHECKED);
						}
						addCookieToResponse(response, LANGUAGE, ENGLISH);
					}
					response.sendRedirect(HOME);
				}
			}
		} catch (final Throwable t) {
			LOG.error("Error: " + t.getMessage(), t);
			throw new ServletException("Error: " + t.getMessage(), t);
		}
	}

	private void addCookieToResponse(final HttpServletResponse response, final String name, final String value) {
		final Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge(ONE_YEAR_COOKIE);
		response.addCookie(cookie);
	}
}
