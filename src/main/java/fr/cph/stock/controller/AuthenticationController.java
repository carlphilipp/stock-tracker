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

package fr.cph.stock.controller;

import fr.cph.stock.business.UserBusiness;
import fr.cph.stock.entities.User;
import fr.cph.stock.exception.LoginException;
import fr.cph.stock.web.servlet.CookieManagement;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
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
@Controller
public class AuthenticationController {

	private static final int ONE_YEAR_COOKIE = 60 * 60 * 24 * 365;

	@Autowired
	private UserBusiness userBusiness;
	private List<String> defaultCookies;

	@PostConstruct
	public final void init() {
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

	@RequestMapping(value = "/auth", method = RequestMethod.POST)
	public String authUser(
		final HttpServletRequest request,
		final HttpServletResponse response,
		@RequestParam(value = LOGIN) final String login,
		@RequestParam(value = PASSWORD) final String password) throws LoginException {
		request.getSession().invalidate();
		final Optional<User> userOptional = userBusiness.checkUser(login, password);
		if (userOptional.isPresent()) {
			final User user = userOptional.get();
			if (!user.getAllow()) {
				request.getSession().setAttribute(ERROR, "Account not confirmed. Check your email!");
				return "index";
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
				log.info("User logged in [{}]", login);
				return "forward:/loadHome";
			}
		} else {
			return "loginError";
		}
	}

	private void addCookieToResponse(final HttpServletResponse response, final String name, final String value) {
		final Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge(ONE_YEAR_COOKIE);
		response.addCookie(cookie);
	}
}
