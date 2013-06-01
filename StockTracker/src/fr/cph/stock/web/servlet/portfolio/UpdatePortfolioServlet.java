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

package fr.cph.stock.web.servlet.portfolio;

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
import fr.cph.stock.exception.YahooException;
import fr.cph.stock.language.LanguageFactory;
import fr.cph.stock.web.servlet.CookieManagement;

@WebServlet(name = "UpdatePortfolioServlet", urlPatterns = { "/updateportfolio" })
public class UpdatePortfolioServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(UpdatePortfolioServlet.class);

	private static final long serialVersionUID = 1L;
	private IBusiness business;

	public void init() {
		business = new Business();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		try {
			HttpSession session = request.getSession(false);
			String lang = CookieManagement.getCookieLanguage(Arrays.asList(request.getCookies()));
			LanguageFactory language = LanguageFactory.getInstance();
			StringBuilder sb = new StringBuilder("");
			User user = (User) session.getAttribute("user");
			String updateCurrencies = request.getParameter("currencyUpdate");
			try {
				Portfolio portfolio = business.getUserPortfolio(user.getId(), null, null);
				if (updateCurrencies != null) {
					business.updateOneCurrency(portfolio.getCurrency());
				}
				business.addOrUpdateCompaniesLimitedRequest(portfolio.getCompaniesYahooIdRealTime());
			} catch (YahooException e1) {
				sb.append(e1.getMessage() + " ");
			}
			if (!sb.toString().equals("")) {
				request.setAttribute("updateStatus", "<span class='cQuoteDown'>" + sb.toString() + "</span>");
			} else {
				request.setAttribute("updateStatus", "<span class='cQuoteUp'>" + language.getLanguage(lang).get("CONSTANT_UPDATED")
						+ " !</span>");
			}
			request.getRequestDispatcher("home").forward(request, response);

		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new ServletException("Error: " + t.getMessage(), t);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
