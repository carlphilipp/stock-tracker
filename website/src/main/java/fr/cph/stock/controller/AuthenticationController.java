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

import fr.cph.stock.entities.User;
import fr.cph.stock.exception.LoginException;
import fr.cph.stock.service.UserService;
import fr.cph.stock.util.Constants;
import fr.cph.stock.web.servlet.CookieManagement;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This servlet is called when the user want to login
 *
 * @author Carl-Philipp Harmant
 */
@Log4j2
@Controller
public class AuthenticationController {

	private static final int ONE_YEAR_COOKIE = 60 * 60 * 24 * 365;

	@NonNull
	private final UserService userService;
	@NonNull
	private final List<String> defaultCookies;

	@Autowired
	public AuthenticationController(final UserService userService) {
		this.userService = userService;
		defaultCookies = new ArrayList<>();
		defaultCookies.add(Constants.QUOTE);
		defaultCookies.add(Constants.CURRENCY);
		defaultCookies.add(Constants.PARITY);
		defaultCookies.add(Constants.STOP_LOSS);
		defaultCookies.add(Constants.OBJECTIVE);
		defaultCookies.add(Constants.YIELD_1);
		defaultCookies.add(Constants.YIELD_2);
		defaultCookies.add(Constants.AUTO_UPDATE);
	}

	@RequestMapping(value = "/auth", method = RequestMethod.POST)
	public ModelAndView authUser(
		final HttpServletRequest request,
		final HttpServletResponse response,
		@RequestParam(value = Constants.LOGIN) final String login,
		@RequestParam(value = Constants.PASSWORD) final String password) throws LoginException {
		final ModelAndView model = new ModelAndView();
		request.getSession().invalidate();
		final User user = userService.checkUser(login, password).orElseThrow(() -> new LoginException(login));
		if (!user.getAllow()) {
			request.getSession().setAttribute(Constants.ERROR, "Account not confirmed. Check your email!");
			model.setViewName("index");
		} else {
			request.getSession().setAttribute(Constants.USER, user);
			setUpCookies(request, response);

			log.info("User logged in [{}]", login);
			model.setViewName("redirect:/home");
		}
		return model;
	}

	private void setUpCookies(final HttpServletRequest request, final HttpServletResponse response) {
		if (request.getCookies() != null) {
			final List<Cookie> cookies = Arrays.asList(request.getCookies());
			defaultCookies.stream().filter(cookieName -> CookieManagement.notContainsCookie(cookies, cookieName))
				.forEach(cookieName -> addCookieToResponse(response, cookieName, Constants.CHECKED));
			if (CookieManagement.notContainsCookie(cookies, Constants.LANGUAGE)) {
				addCookieToResponse(response, Constants.LANGUAGE, Constants.ENGLISH);
			}
		} else {
			defaultCookies.forEach(cookieName -> addCookieToResponse(response, cookieName, Constants.CHECKED));
			addCookieToResponse(response, Constants.LANGUAGE, Constants.ENGLISH);
		}
	}

	private void addCookieToResponse(final HttpServletResponse response, final String name, final String value) {
		final Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge(ONE_YEAR_COOKIE);
		response.addCookie(cookie);
	}
}
