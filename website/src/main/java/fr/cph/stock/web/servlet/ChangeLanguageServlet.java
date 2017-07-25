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

package fr.cph.stock.web.servlet;

import lombok.extern.log4j.Log4j2;
import fr.cph.stock.util.Constants;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This servlet is called to change the user language
 *
 * @author Carl-Philipp Harmant
 */
@Log4j2
@WebServlet(name = "ChangeLanguageServlet", urlPatterns = {"/language"})
public class ChangeLanguageServlet extends HttpServlet {

	private static final long serialVersionUID = -1381535043505856447L;

	@Override
	protected final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		try {
			final String language = request.getParameter(Constants.LANGUAGE);
			if (language.equals(Constants.ENGLISH) || language.equals(Constants.FRANCAIS)) {
				final Cookie cookie = new Cookie(Constants.LANGUAGE, language.intern());
				response.addCookie(cookie);
			}
			response.sendRedirect(Constants.HOME);
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
