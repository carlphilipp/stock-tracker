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
import fr.cph.stock.exception.LoginException;

@WebServlet(name = "RegistrationServlet", urlPatterns = { "/register" })
public class RegistrationServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(RegistrationServlet.class);

	private static final long serialVersionUID = 1L;
	private IBusiness business;

	public void init() {
		business = new Business();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		try {
			String login = request.getParameter("login");
			String password = request.getParameter("password");
			String email = request.getParameter("email");
			try {
				business.createUser(login, password, email);
				User user = business.getUser(login);
				request.setAttribute("user", user);
			} catch (LoginException e) {
				request.setAttribute("error", e.getMessage());
			}
			request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);

		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new ServletException("Error: " + t.getMessage(), t);
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
