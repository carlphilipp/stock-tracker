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

package fr.cph.stock.web.servlet;

import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static fr.cph.stock.util.Constants.*;

/**
 * This servlet is called to change the user language
 * 
 * @author Carl-Philipp Harmant
 * 
 */

@WebServlet(name = "ChangeLanguageServlet", urlPatterns = { "/language" })
public class ChangeLanguageServlet extends HttpServlet {

	private static final long serialVersionUID = -1381535043505856447L;

	private static final Logger LOG = Logger.getLogger(ChangeLanguageServlet.class);

	@Override
	protected final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		try {
			final String language = request.getParameter(LANGUAGE);
			if (language.equals(ENGLISH) || language.equals(FRANCAIS)) {
				final Cookie cookie = new Cookie(LANGUAGE, language.intern());
				response.addCookie(cookie);
			}
			response.sendRedirect(HOME);
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
