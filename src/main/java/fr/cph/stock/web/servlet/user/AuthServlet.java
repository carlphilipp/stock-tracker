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

import static fr.cph.stock.util.Constants.LANGUAGE_PARAM;

/**
 * This servlet is called when the user want to login
 * 
 * @author Carl-Philipp Harmant
 * 
 */
@WebServlet(name = "AuthServlet", urlPatterns = { "/auth" })
public class AuthServlet extends HttpServlet {

	/** Serialization **/
	private static final long serialVersionUID = 1L;
	/** Logger **/
	private static final Logger LOG = Logger.getLogger(AuthServlet.class);
	/** Business **/
	private IBusiness business;
	/** Cookie validity **/
	private static final int ONE_YEAR_COOKIE = 60 * 60 * 24 * 365;
	/** Cookie list **/
	private List<String> lcookies;

	@Override
	public final void init() {
		business = Business.getInstance();
		lcookies = new ArrayList<>();
		lcookies.add("quote");
		lcookies.add("currency");
		lcookies.add("parity");
		lcookies.add("stopLoss");
		lcookies.add("objective");
		lcookies.add("yield1");
		lcookies.add("yield2");
		lcookies.add("autoUpdate");
	}

	@Override
	protected final void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		try {
			request.getSession().invalidate();
			String login = request.getParameter("login");
			String password = request.getParameter("password");
			User user = business.checkUser(login, password);
			if (user == null) {
				request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
			} else {
				if (!user.getAllow()) {
					request.getSession().setAttribute("error", "Account not confirmed. Check your email!");
					request.getRequestDispatcher("index.jsp").forward(request, response);
				} else {
					request.getSession().setAttribute("user", user);
					if (request.getCookies() != null) {
						List<Cookie> cookies = Arrays.asList(request.getCookies());
						for (String str : lcookies) {
							if (!CookieManagement.containsCookie(cookies, str)) {
								Cookie cookie = new Cookie(str, "checked");
								cookie.setMaxAge(ONE_YEAR_COOKIE);
								response.addCookie(cookie);
							}
						}
						if (!CookieManagement.containsCookie(cookies, LANGUAGE_PARAM)) {
							Cookie cookie = new Cookie(LANGUAGE_PARAM, "English");
							cookie.setMaxAge(ONE_YEAR_COOKIE);
							response.addCookie(cookie);
						}
					} else {
						for (String str : lcookies) {
							Cookie cookie = new Cookie(str, "checked");
							cookie.setMaxAge(ONE_YEAR_COOKIE);
							response.addCookie(cookie);
						}
						Cookie cookie = new Cookie(LANGUAGE_PARAM, "English");
						cookie.setMaxAge(ONE_YEAR_COOKIE);
						response.addCookie(cookie);
					}
					response.sendRedirect("home");
				}
			}
		} catch (Throwable t) {
			LOG.error("Error: " + t.getMessage(), t);
			throw new ServletException("Error: " + t.getMessage(), t);
		}
	}
}
