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
import fr.cph.stock.security.SecurityService;
import fr.cph.stock.util.Info;
import fr.cph.stock.util.Mail;
import lombok.extern.log4j.Log4j2;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static fr.cph.stock.util.Constants.EMAIL;
import static fr.cph.stock.util.Constants.ERROR;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * This servlet is called when the user has lost his password and want to get a new ont
 *
 * @author Carl-Philipp Harmant
 */
@Log4j2
@WebServlet(name = "LostServlet", urlPatterns = {"/lost"})
public class LostServlet extends HttpServlet {

	private static final long serialVersionUID = -1724898618001479554L;
	private static final String USER_NOT_FOUND = "User not found!";

	private UserBusiness userBusiness;
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
			final String email = request.getParameter(EMAIL);
			if (isNotBlank(email)) {
				final Optional<User> userOptional = userBusiness.getUserWithEmail(email);
				if (userOptional.isPresent()) {
					final User user = userOptional.get();
					final StringBuilder body = new StringBuilder();
					final String check = securityService.encodeToSha256(user.getLogin() + user.getPassword() + user.getEmail());
					body.append("Dear ")
						.append(user.getLogin())
						.append(",\n\nSomeone is trying to reset your password. If it is not you, just ignore this email.\n")
						.append("If it's you, click on this link:  ")
						.append(Info.ADDRESS)
						.append(Info.FOLDER)
						.append("/newpassword?&login=")
						.append(user.getLogin())
						.append("&check=")
						.append(check)
						.append(".\n\nBest regards,\nThe ")
						.append(Info.NAME)
						.append(" team.");
					Mail.sendMail("[Password Reset] " + Info.NAME, body.toString(), new String[]{email});
					request.setAttribute("ok", "Check your email!");
				} else {
					request.setAttribute(ERROR, USER_NOT_FOUND);
				}
			} else {
				request.setAttribute(ERROR, USER_NOT_FOUND);
			}
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
