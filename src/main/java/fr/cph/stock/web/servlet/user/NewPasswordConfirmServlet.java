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
import fr.cph.stock.business.impl.UserBusinessImpl;
import fr.cph.stock.entities.User;
import fr.cph.stock.security.SecurityService;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static fr.cph.stock.util.Constants.LOGIN;
import static fr.cph.stock.util.Constants.PASSWORD;

/**
 * This servlet is called when new password asked
 *
 * @author Carl-Philipp Harmant
 */
@WebServlet(name = "NewPasswordConfirmServlet", urlPatterns = {"/newpasswordconf"})
public class NewPasswordConfirmServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(NewPasswordConfirmServlet.class);
	private UserBusiness userBusiness;
	private SecurityService securityService;

	/**
	 * Init
	 **/
	@Override
	public final void init() {
		userBusiness = UserBusinessImpl.INSTANCE;
		securityService = SecurityService.INSTANCE;
	}

	@Override
	protected final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		try {
			final String login = request.getParameter(LOGIN);
			final String password = request.getParameter(PASSWORD);
			final User user = userBusiness.getUser(login);
			final String md5PasswordHashed = securityService.encodeToSha256(password);
			final String saltHashed = securityService.generateSalt();
			final String cryptedPasswordSalt = securityService.encodeToSha256(md5PasswordHashed + saltHashed);
			user.setPassword(saltHashed + cryptedPasswordSalt);
			userBusiness.updateOneUserPassword(user);
			request.setAttribute("ok", "Password changed!");
			request.getRequestDispatcher("index.jsp").forward(request, response);
		} catch (final Throwable t) {
			LOG.error(t.getMessage(), t);
			throw new ServletException("Error: " + t.getMessage(), t);
		}
	}

	@Override
	protected final void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		doGet(request, response);
	}
}
