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

package fr.cph.stock.web.servlet.user;

import fr.cph.stock.business.UserBusiness;
import fr.cph.stock.entities.User;
import fr.cph.stock.guice.GuiceInjector;
import fr.cph.stock.web.servlet.CookieManagement;
import lombok.extern.log4j.Log4j2;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static fr.cph.stock.util.Constants.*;

/**
 * This servlet is called when the user want to login
 *
 * @author Carl-Philipp Harmant
 */
@Log4j2
@WebServlet(name = "AuthServlet", urlPatterns = {"/auth"})
public class AuthServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final int ONE_YEAR_COOKIE = 60 * 60 * 24 * 365;
	private UserBusiness userBusiness;
	private List<String> lcookies;

	@Override
	public final void init() {
		userBusiness = GuiceInjector.INSTANCE.getUserBusiness();
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
			final Optional<User> userOptional = userBusiness.checkUser(login, password);
			if (userOptional.isPresent()) {
				final User user = userOptional.get();
				if (!user.getAllow()) {
					request.getSession().setAttribute(ERROR, "Account not confirmed. Check your email!");
					request.getRequestDispatcher("index.jsp").forward(request, response);
				} else {
					request.getSession().setAttribute(USER, user);
					if (request.getCookies() != null) {
						final List<Cookie> cookies = Arrays.asList(request.getCookies());
						lcookies.stream().filter(cookieName -> CookieManagement.notContainsCookie(cookies, cookieName))
							.forEach(cookieName -> addCookieToResponse(response, cookieName, CHECKED));
						if (CookieManagement.notContainsCookie(cookies, LANGUAGE)) {
							addCookieToResponse(response, LANGUAGE, ENGLISH);
						}
					} else {
						lcookies.forEach(cookieName -> addCookieToResponse(response, cookieName, CHECKED));
						addCookieToResponse(response, LANGUAGE, ENGLISH);
					}
					response.sendRedirect(HOME);
				}
			} else {
				request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
			}
		} catch (final Throwable t) {
			log.error("Error: {}", t.getMessage(), t);
			throw new ServletException("Error: " + t.getMessage(), t);
		}
	}

	private void addCookieToResponse(final HttpServletResponse response, final String name, final String value) {
		final Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge(ONE_YEAR_COOKIE);
		response.addCookie(cookie);
	}
}
