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
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

import static fr.cph.stock.util.Constants.ERROR;
import static fr.cph.stock.util.Constants.USER;

/**
 * This servlet is called when the user want to login
 *
 * @author Carl-Philipp Harmant
 */
@SessionAttributes(USER)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Log4j2
@Controller
public class AuthenticationController {

	@NonNull
	private final UserService userService;

	@RequestMapping(value = "/auth", method = RequestMethod.POST)
	public ModelAndView auth(
		final HttpServletRequest request,
		@RequestParam(value = Constants.LOGIN) final String login,
		@RequestParam(value = Constants.PASSWORD) final String password) throws LoginException {
		final ModelAndView model = new ModelAndView();
		final User user = User.builder()
			.login("cp")
			.locale("en_US")
			.timeZone("America/Chicago")
			.allow(true)
			.build();//userService.checkUser(login, password).orElseThrow(() -> new LoginException(login));
		if (!user.getAllow()) {
			model.addObject(ERROR, "Account not confirmed. Check your email!");
			model.setViewName("index");
		} else {
			request.getSession().setAttribute(USER, user);
			model.addObject(USER, user);
			model.setViewName("redirect:/home");
		}
		return model;
	}
}
