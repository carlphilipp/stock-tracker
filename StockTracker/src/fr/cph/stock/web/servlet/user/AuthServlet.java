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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import fr.cph.stock.business.Business;
import fr.cph.stock.business.IBusiness;
import fr.cph.stock.entities.User;
import fr.cph.stock.web.servlet.CookieManagement;

@WebServlet(name = "AuthServlet", urlPatterns = { "/auth" })
public class AuthServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(AuthServlet.class);

	private static final long serialVersionUID = 1L;

	private final int ONE_YEAR_COOKIE = 60 * 60 * 24 * 365;

	private IBusiness business;
	private List<String> lcookies;

	public AuthServlet() {
		super();
	}

	public void init() {
		business = new Business();
		lcookies = new ArrayList<String>();
		lcookies.add("quote");
		lcookies.add("currency");
		lcookies.add("parity");
		lcookies.add("stopLoss");
		lcookies.add("objective");
		lcookies.add("yield1");
		lcookies.add("yield2");
		lcookies.add("autoUpdate");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
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
						if (!CookieManagement.containsCookie(cookies, "language")) {
							Cookie cookie = new Cookie("language", "English");
							cookie.setMaxAge(ONE_YEAR_COOKIE);
							response.addCookie(cookie);
						}
					} else {
						for (String str : lcookies) {
							Cookie cookie = new Cookie(str, "checked");
							cookie.setMaxAge(ONE_YEAR_COOKIE);
							response.addCookie(cookie);
						}
						Cookie cookie = new Cookie("language", "English");
						cookie.setMaxAge(ONE_YEAR_COOKIE);
						response.addCookie(cookie);
					}
					response.sendRedirect("home");
				}
			}
		} catch (Throwable t) {
			log.error("Error: " + t.getMessage(), t);
			throw new ServletException("Error: " + t.getMessage(), t);
		}
	}
}
