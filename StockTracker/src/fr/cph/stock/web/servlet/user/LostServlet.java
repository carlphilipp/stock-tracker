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

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import fr.cph.stock.business.Business;
import fr.cph.stock.business.IBusiness;
import fr.cph.stock.entities.User;
import fr.cph.stock.security.Security;
import fr.cph.stock.util.Info;
import fr.cph.stock.util.Mail;

/**
 * This servlet is called when the user has lost his password and want to get a new ont
 * 
 * @author Carl-Philipp Harmant
 * 
 */
@WebServlet(name = "LostServlet", urlPatterns = { "/lost" })
public class LostServlet extends HttpServlet {

	/** Serialization **/
	private static final long serialVersionUID = -1724898618001479554L;
	/** Logger **/
	private static final Logger LOG = Logger.getLogger(LostServlet.class);
	/** Business **/
	private IBusiness business;

	/** Init **/
	@Override
	public final void init() {
		business = new Business();
	}

	/**
	 * Get method
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the answer
	 * @throws ServletException
	 *             the exception
	 */
	@Override
	protected final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		try {
			String email = request.getParameter("email");
			if (!email.equals("")) {
				User user = business.getUserWithEmail(email);
				if (user != null) {
					StringBuilder body = new StringBuilder();
					String check = Security.encodeToSha256(user.getLogin() + user.getPassword() + user.getEmail());
					body.append("Dear " + user.getLogin()
							+ ",\n\nSomeone is trying to reset your password. If it is not you, just ignore this email.\n"
							+ "If it's you, click on this link:  " + Info.ADDRESS + Info.FOLDER + "/newpassword?&login="
							+ user.getLogin() + "&check=" + check + ".\n\nBest regards,\nThe " + Info.NAME + " team.");
					Mail.sendMail("[Password Reset] " + Info.NAME, body.toString(), new String[] {email}, null);
					request.setAttribute("ok", "Check your email!");
				} else {
					request.setAttribute("error", "User not found!");
				}
			} else {
				request.setAttribute("error", "User not found!");
			}
			request.getRequestDispatcher("index.jsp").forward(request, response);
		} catch (Throwable t) {
			LOG.error(t.getMessage(), t);
			throw new ServletException("Error: " + t.getMessage(), t);
		}
	}

	/**
	 * Post method
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the answer
	 * @throws ServletException
	 *             the servlet exception
	 */
	@Override
	protected final void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		doGet(request, response);
	}

}
