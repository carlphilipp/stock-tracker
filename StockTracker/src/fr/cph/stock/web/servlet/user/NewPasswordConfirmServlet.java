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

import java.io.IOException;

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

@WebServlet(name = "NewPasswordConfirmServlet", urlPatterns = { "/newpasswordconf" })
public class NewPasswordConfirmServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(NewPasswordConfirmServlet.class);
	
	private static final long serialVersionUID = 1L;
	
	private IBusiness business;

	public void init() {
		business = new Business();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		try {
			String login = request.getParameter("login");
			String password = request.getParameter("password");
			User user = business.getUser(login);
			String md5PasswordHashed = Security.encodeToSha256(password);
			String saltHashed = Security.generateSalt();
			String cryptedPasswordSalt = Security.encodeToSha256(md5PasswordHashed + saltHashed);
			user.setPassword(saltHashed + cryptedPasswordSalt);
			business.updateOneUserPassword(user);
			request.setAttribute("ok", "Password changed!");
			request.getRequestDispatcher("index.jsp").forward(request, response);
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new ServletException("Error: " + t.getMessage(), t);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
