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

@WebServlet(name = "UpdateShareValueMobileServlet", urlPatterns = { "/updatesharevaluemobile" })
public class UpdateShareValueMobileServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(UpdateShareValueMobileServlet.class);

	private static final long serialVersionUID = 1L;
	
	private final MathContext mathContext = MathContext.DECIMAL32;

	private IBusiness business;

	@Override
	public void init() {
		business = new Business();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
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

				// business.updateOneCurrency(portfolio.getCurrency());
				//business.addOrUpdateCompaniesLimitedRequest(portfolio.getCompaniesYahooIdRealTime());
				
				double newLiquidity = account.getLiquidity() + movement + yield - buy + sell - taxe;
				newLiquidity = (new BigDecimal(newLiquidity, mathContext)).doubleValue();
				business.updateLiquidity(account, newLiquidity);
				portfolio = business.getUserPortfolio(user.getId(), null, null);
				business.updateCurrentShareValue(portfolio, account, movement, yield, buy, sell, taxe, commentary);
				response.sendRedirect("homemobile");
			} catch (NumberFormatException e) {
				response.getWriter().write("{\"error\":" + e.getMessage() + "\"}");
			}
		} catch (Throwable t) {
			log.error("Error: " + t.getMessage(), t);
			throw new ServletException("Error: " + t.getMessage(), t);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		doGet(request, response);
	}

}
