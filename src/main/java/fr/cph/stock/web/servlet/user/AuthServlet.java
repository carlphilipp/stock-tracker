/**
 * Copyright 2017 Carl-Philipp Harmant
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
import fr.cph.stock.exception.LoginException;
import fr.cph.stock.web.servlet.CookieManagement;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
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
//@WebServlet(name = "AuthServlet", urlPatterns = {"/auth"})
@Controller
public class AuthServlet {

	private static final int ONE_YEAR_COOKIE = 60 * 60 * 24 * 365;

	@Autowired
	private UserBusiness userBusiness;
	private List<String> defaultCookies;

	//@Override
	@PostConstruct
	public final void init() {
		//userBusiness = GuiceInjector.INSTANCE.getUserBusiness();
		defaultCookies = new ArrayList<>();
		defaultCookies.add(QUOTE);
		defaultCookies.add(CURRENCY);
		defaultCookies.add(PARITY);
		defaultCookies.add(STOP_LOSS);
		defaultCookies.add(OBJECTIVE);
		defaultCookies.add(YIELD_1);
		defaultCookies.add(YIELD_2);
		defaultCookies.add(AUTO_UPDATE);
	}

	@RequestMapping(value = "/auth")
	protected final String doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, LoginException {
		//ModelAndView model = new ModelAndView("derp");
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
						defaultCookies.stream().filter(cookieName -> CookieManagement.notContainsCookie(cookies, cookieName))
							.forEach(cookieName -> addCookieToResponse(response, cookieName, CHECKED));
						if (CookieManagement.notContainsCookie(cookies, LANGUAGE)) {
							addCookieToResponse(response, LANGUAGE, ENGLISH);
						}
					} else {
						defaultCookies.forEach(cookieName -> addCookieToResponse(response, cookieName, CHECKED));
						addCookieToResponse(response, LANGUAGE, ENGLISH);
					}
					//model.addObject(USER, user);
					log.info("User logged in [{}]", login);
					//response.sendRedirect(HOME);
					return "forward:/derp";
				}
			} else {
				request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
			}
		} catch (final LoginException ex) {
			throw ex;
		} catch (final Throwable t) {
			log.error("Error: {}", t.getMessage(), t);
			throw new ServletException("Error: " + t.getMessage(), t);
		}
		return "error";
	}

	private void addCookieToResponse(final HttpServletResponse response, final String name, final String value) {
		final Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge(ONE_YEAR_COOKIE);
		response.addCookie(cookie);
	}
}
