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

import fr.cph.stock.business.Business;
import fr.cph.stock.business.IBusiness;
import fr.cph.stock.entities.Portfolio;
import fr.cph.stock.entities.User;
import fr.cph.stock.exception.YahooException;
import fr.cph.stock.language.LanguageFactory;
import fr.cph.stock.web.servlet.CookieManagement;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;

import static fr.cph.stock.util.Constants.*;

/**
 * This servlet is called when the user want to update the portfolio
 *
 * @author Carl-Philipp Harmant
 *
 */
@WebServlet(name = "UpdatePortfolioServlet", urlPatterns = { "/updateportfolio" })
public class UpdatePortfolioServlet extends HttpServlet {

	private static final long serialVersionUID = 5252788304524725462L;
	private static final Logger LOG = Logger.getLogger(UpdatePortfolioServlet.class);
	private IBusiness business;

	@Override
	public final void init() {
		this.business = Business.getInstance();
	}

	@Override
	protected final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		try {
			final HttpSession session = request.getSession(false);
			final String lang = CookieManagement.getCookieLanguage(Arrays.asList(request.getCookies()));
			final LanguageFactory language = LanguageFactory.getInstance();
			final StringBuilder sb = new StringBuilder("");
			final User user = (User) session.getAttribute(USER);
			final String updateCurrencies = request.getParameter(CURRENCY_UPDATE);
			String error = null;
			try {
				final Portfolio portfolio = business.getUserPortfolio(user.getId(), null, null);
				if (updateCurrencies != null) {
					business.updateOneCurrency(portfolio.getCurrency());
				}
				error = business.addOrUpdateCompaniesLimitedRequest(portfolio.getCompaniesYahooIdRealTime());
			} catch (YahooException e1) {
				LOG.error(e1.getMessage(), e1);
				sb.append(e1.getMessage() + " ");
			}
			if (!sb.toString().equals("")) {
				request.setAttribute(UPDATE_STATUS, "<span class='cQuoteDown'>Error !</span>");
			} else {
				if (error != null && !error.equals("")) {
					request.setAttribute(
							UPDATE_STATUS,
							"<span class='cQuoteOrange'>"
									+ error
									+ "The company does not exist anymore. Please delete it from your portfolio. The other companies has been updated.</span>");
				} else {
					request.setAttribute("updateStatus", "<span class='cQuoteUp'>" + language.getLanguage(lang).get("CONSTANT_UPDATED") + " !</span>");
				}
			}
			request.getRequestDispatcher(HOME).forward(request, response);
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
