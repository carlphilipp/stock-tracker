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

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import fr.cph.stock.business.Business;
import fr.cph.stock.business.IBusiness;
import fr.cph.stock.entities.Portfolio;
import fr.cph.stock.entities.User;
import fr.cph.stock.exception.LanguageException;
import fr.cph.stock.exception.YahooException;
import fr.cph.stock.language.LanguageFactory;
import fr.cph.stock.util.Info;

@WebServlet(name = "CurrencyServlet", urlPatterns = { "/currencies" })
public class CurrencyServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger log = Logger.getLogger(CurrencyServlet.class);
	private IBusiness business;
	private LanguageFactory language;

	@Override
	public void init() throws ServletException {
		business = new Business();
		try {
			language = LanguageFactory.getInstance();
		} catch (LanguageException e) {
			log.error(e.getMessage(), e);
			throw new ServletException("Error: " + e.getMessage(), e);
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		try {
			HttpSession session = request.getSession(false);
			User user = (User) session.getAttribute("user");
			String update = request.getParameter("update");
			if (update != null) {
				Portfolio portfolio = business.getUserPortfolio(user.getId(), null, null);
				try {
					business.updateOneCurrency(portfolio.getCurrency());
					request.setAttribute("message", "Done !");
				} catch (YahooException e) {
					request.setAttribute("error", e.getMessage());
				}
			}
			Portfolio portfolio = business.getUserPortfolio(user.getId(), null, null);
			Object[][] tab = business.getAllCurrencyData(portfolio.getCurrency());
			request.setAttribute("portfolio", portfolio);
			request.setAttribute("tab", tab);
			String lang = CookieManagement.getCookieLanguage(Arrays.asList(request.getCookies()));
			request.setAttribute("language", language.getLanguage(lang));
			request.setAttribute("appTitle", Info.NAME + " &bull;   Currencies");
			request.getRequestDispatcher("jsp/currencies.jsp").forward(request, response);
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new ServletException("Error: " + t.getMessage(), t);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
