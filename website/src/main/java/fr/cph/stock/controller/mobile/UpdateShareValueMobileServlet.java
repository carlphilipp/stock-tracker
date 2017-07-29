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

package fr.cph.stock.controller.mobile;

import fr.cph.stock.service.ShareValueService;
import fr.cph.stock.service.UserService;
import fr.cph.stock.entities.Account;
import fr.cph.stock.entities.Portfolio;
import fr.cph.stock.entities.User;
import fr.cph.stock.exception.NotFoundException;
import lombok.extern.log4j.Log4j2;
import fr.cph.stock.util.Constants;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.math.MathContext;

/**
 * This servlet is called to update its share value, from mobile
 *
 * @author Carl-Philipp Harmant
 */
// FIXME to convert to a spring rest controller
@Log4j2
//@WebServlet(name = "UpdateShareValueMobileServlet", urlPatterns = {"/updatesharevaluemobile"})
public class UpdateShareValueMobileServlet extends HttpServlet {

	private static final long serialVersionUID = 2877166802472612746L;
	private UserService userService;
	private ShareValueService shareValueService;
	private final MathContext mathContext = MathContext.DECIMAL32;

	@Override
	public final void init() {
	}

	@Override
	protected final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		try {
			final HttpSession session = request.getSession(false);
			final User user = (User) session.getAttribute(Constants.USER);
			Portfolio portfolio = userService.getUserPortfolio(user.getId()).orElseThrow(() -> new NotFoundException(user.getId()));
			try {
				final int accountId = Integer.valueOf(request.getParameter(Constants.ACCOUNT_ID));
				final double movement = Double.valueOf(request.getParameter(Constants.LIQUIDITY));
				final double yield = Double.valueOf(request.getParameter(Constants.YIELD));
				final double buy = Double.valueOf(request.getParameter(Constants.BUY));
				final double sell = Double.valueOf(request.getParameter(Constants.SELL));
				final double taxe = Double.valueOf(request.getParameter(Constants.TAXE));
				final String commentary = request.getParameter(Constants.COMMENTARY);
				Account account = null;
				for (final Account acc : portfolio.getAccounts()) {
					if (acc.getId() == accountId) {
						account = acc;
						break;
					}
				}

				if (account == null) {
					response.getWriter().write("{\"error\":\"Account not found\"}");
				} else {
					double newLiquidity = account.getLiquidity() + movement + yield - buy + sell - taxe;
					newLiquidity = new BigDecimal(Double.toString(newLiquidity), mathContext).doubleValue();
					userService.updateLiquidity(account, newLiquidity);
					portfolio = userService.getUserPortfolio(user.getId()).orElseThrow(() -> new NotFoundException(user.getId()));
					shareValueService.updateCurrentShareValue(portfolio, account, movement, yield, buy, sell, taxe, commentary);
					response.sendRedirect(Constants.HOMEMOBILE);
				}

				// service.updateOneCurrency(portfolio.getCurrency());
				// service.addOrUpdateCompaniesLimitedRequest(portfolio.getCompaniesYahooIdRealTime());

			} catch (final NumberFormatException e) {
				response.getWriter().write("{\"error\":" + e.getMessage() + "\"}");
			}
		} catch (final Throwable t) {
			log.error("Error: {}", t.getMessage(), t);
			throw new ServletException("Error: " + t.getMessage(), t);
		}
	}

	@Override
	protected final void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		doGet(request, response);
	}
}
