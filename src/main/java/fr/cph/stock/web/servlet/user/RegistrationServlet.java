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
import fr.cph.stock.exception.LoginException;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fr.cph.stock.util.Constants.*;

/**
 * This servlet is called when the user want to register
 *
 * @author Carl-Philipp Harmant
 *
 */
@WebServlet(name = "RegistrationServlet", urlPatterns = { "/register" })
public class RegistrationServlet extends HttpServlet {

	private static final long serialVersionUID = 6262531123441177265L;
	private static final Logger LOG = Logger.getLogger(RegistrationServlet.class);
	private IBusiness business;

	@Override
	public final void init() {
		this.business = Business.getInstance();
	}

	@Override
	protected final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		try {
			final String login = request.getParameter(LOGIN);
			final String password = request.getParameter(PASSWORD);
			final String email = request.getParameter(EMAIL);
			if (!isValidEmailAddress(email)) {
				request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
			} else {
				try {
					business.createUser(login, password, email);
					final User user = business.getUser(login);
					request.setAttribute(USER, user);
				} catch (LoginException e) {
					request.setAttribute(ERROR, e.getMessage());
				}
				request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
			}
		} catch (final Throwable t) {
			LOG.error(t.getMessage(), t);
			throw new ServletException("Error: " + t.getMessage(), t);
		}

	}

	@Override
	protected final void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		doGet(request, response);
	}

	private boolean isValidEmailAddress(final String email) {
		final String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
		Pattern p = Pattern.compile(ePattern);
		Matcher m = p.matcher(email);
		return m.matches();
	}
}
