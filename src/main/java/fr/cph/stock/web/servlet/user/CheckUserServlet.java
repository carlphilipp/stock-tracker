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
import fr.cph.stock.security.Security;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static fr.cph.stock.util.Constants.*;

/**
 * This servlet is called when the user is registering
 * 
 * @author Carl-Philipp Harmant
 * 
 */
@WebServlet(name = "CheckUserServlet", urlPatterns = { "/check" })
public class CheckUserServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(CheckUserServlet.class);
	private IBusiness business;

	@Override
	public final void init() {
		this.business = Business.getInstance();
	}

	@Override
	protected final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		try {
			String login = request.getParameter(LOGIN);
			String check = request.getParameter(CHECK);
			User user = business.getUser(login);
			if (user != null) {
				String serverCheck = Security.encodeToSha256(user.getLogin() + user.getPassword() + user.getEmail());
				if (check.equals(serverCheck)) {
					business.validateUser(login);
					request.setAttribute(MESSAGE, "It worked!<br>You can now <a href='index.jsp'>login</a>");
				} else {
					request.setAttribute(MESSAGE, "Sorry, it did not work");
				}
				request.setAttribute(USER, user);
			} else {
				request.setAttribute(MESSAGE, "Sorry, it did not work");
			}
			request.getRequestDispatcher("/jsp/check.jsp").forward(request, response);

		} catch (Throwable t) {
			LOG.error(t.getMessage(), t);
			throw new ServletException("Error: " + t.getMessage(), t);
		}
	}

	@Override
	protected final void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		doGet(request, response);
	}
}
