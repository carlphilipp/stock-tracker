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

package fr.cph.stock.web.servlet.mobile;

import java.math.BigDecimal;
import java.math.MathContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import fr.cph.stock.business.Business;
import fr.cph.stock.business.IBusiness;
import fr.cph.stock.entities.Account;
import fr.cph.stock.entities.Portfolio;
import fr.cph.stock.entities.User;

/**
 * This servlet is called to update its share value, from mobile
 * 
 * @author Carl-Philipp Harmant
 * 
 */
@WebServlet(name = "UpdateShareValueMobileServlet", urlPatterns = { "/updatesharevaluemobile" })
public class UpdateShareValueMobileServlet extends HttpServlet {

	/** Serialization **/
	private static final long serialVersionUID = 2877166802472612746L;
	/** Logger **/
	private static final Logger LOG = Logger.getLogger(ReloadPortfolioMobileServlet.class);
	/** Business **/
	private IBusiness business;
	/** Precision **/
	private final MathContext mathContext = MathContext.DECIMAL32;

	@Override
	public final void init() {
		business = new Business();
	}

	@Override
	protected final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		try {
			HttpSession session = request.getSession(false);
			User user = (User) session.getAttribute("user");
			Portfolio portfolio = business.getUserPortfolio(user.getId(), null, null);
			try {
				int accountId = Integer.valueOf(request.getParameter("accountId"));
				double movement = Double.valueOf(request.getParameter("liquidity"));
				double yield = Double.valueOf(request.getParameter("yield"));
				double buy = Double.valueOf(request.getParameter("buy"));
				double sell = Double.valueOf(request.getParameter("sell"));
				double taxe = Double.valueOf(request.getParameter("taxe"));
				String commentary = request.getParameter("commentary");
				Account account = null;
				for (Account acc : portfolio.getAccounts()) {
					if (acc.getId() == accountId) {
						account = acc;
						break;
					}
				}

				if (account == null) {
					response.getWriter().write("{\"error\":\"Account not found\"}");
				} else {
					double newLiquidity = account.getLiquidity() + movement + yield - buy + sell - taxe;
					newLiquidity = new BigDecimal(newLiquidity, mathContext).doubleValue();
					business.updateLiquidity(account, newLiquidity);
					portfolio = business.getUserPortfolio(user.getId(), null, null);
					business.updateCurrentShareValue(portfolio, account, movement, yield, buy, sell, taxe, commentary);
					response.sendRedirect("homemobile");
				}

				// business.updateOneCurrency(portfolio.getCurrency());
				// business.addOrUpdateCompaniesLimitedRequest(portfolio.getCompaniesYahooIdRealTime());

			} catch (NumberFormatException e) {
				response.getWriter().write("{\"error\":" + e.getMessage() + "\"}");
			}
		} catch (Throwable t) {
			LOG.error("Error: " + t.getMessage(), t);
			throw new ServletException("Error: " + t.getMessage(), t);
		}
	}

	@Override
	protected final void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		doGet(request, response);
	}

}
