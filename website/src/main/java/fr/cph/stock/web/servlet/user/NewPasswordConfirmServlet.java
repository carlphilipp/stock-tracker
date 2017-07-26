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

import fr.cph.stock.service.UserService;
import fr.cph.stock.entities.User;
import fr.cph.stock.exception.NotFoundException;
import fr.cph.stock.security.SecurityService;
import lombok.extern.log4j.Log4j2;
import fr.cph.stock.util.Constants;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This servlet is called when new password asked
 *
 * @author Carl-Philipp Harmant
 */
// FIXME to delete
@Log4j2
//@WebServlet(name = "NewPasswordConfirmServlet", urlPatterns = {"/newpasswordconf"})
public class NewPasswordConfirmServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private UserService userService;
	private SecurityService securityService;

	/**
	 * Init
	 **/
	@Override
	public final void init() {
	}

	@Override
	protected final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		try {
			final String login = request.getParameter(Constants.LOGIN);
			final String password = request.getParameter(Constants.PASSWORD);

			final User user = userService.getUser(login).orElseThrow(() -> new NotFoundException(login));
			final String md5PasswordHashed = securityService.encodeToSha256(password);
			final String saltHashed = securityService.generateSalt();
			final String cryptedPasswordSalt = securityService.encodeToSha256(md5PasswordHashed + saltHashed);
			user.setPassword(saltHashed + cryptedPasswordSalt);
			userService.updateOneUserPassword(user);
			request.setAttribute("ok", "Password changed!");
			request.getRequestDispatcher("index.jsp").forward(request, response);
		} catch (final Throwable t) {
			log.error(t.getMessage(), t);
			throw new ServletException("Error: " + t.getMessage(), t);
		}
	}

	@Override
	protected final void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		doGet(request, response);
	}
}
