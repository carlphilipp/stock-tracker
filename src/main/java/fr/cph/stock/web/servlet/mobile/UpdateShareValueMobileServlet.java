/**
 * Copyright 2016 Carl-Philipp Harmant
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

package fr.cph.stock.web.servlet.mobile;

import fr.cph.stock.business.ShareValueBusiness;
import fr.cph.stock.business.UserBusiness;
import fr.cph.stock.business.impl.ShareValueBusinessImpl;
import fr.cph.stock.business.impl.UserBusinessImpl;
import fr.cph.stock.entities.Account;
import fr.cph.stock.entities.Portfolio;
import fr.cph.stock.entities.User;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.math.MathContext;

import static fr.cph.stock.util.Constants.*;

/**
 * This servlet is called to update its share value, from mobile
 *
 * @author Carl-Philipp Harmant
 */
@WebServlet(name = "UpdateShareValueMobileServlet", urlPatterns = {"/updatesharevaluemobile"})
public class UpdateShareValueMobileServlet extends HttpServlet {

	private static final long serialVersionUID = 2877166802472612746L;
	private static final Logger LOG = Logger.getLogger(ReloadPortfolioMobileServlet.class);
	private UserBusiness userBusiness;
	private ShareValueBusiness shareValueBusiness;
	private final MathContext mathContext = MathContext.DECIMAL32;

	@Override
	public final void init() {
		userBusiness = UserBusinessImpl.INSTANCE;
		shareValueBusiness = ShareValueBusinessImpl.INSTANCE;
	}

	@Override
	protected final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		try {
			final HttpSession session = request.getSession(false);
			final User user = (User) session.getAttribute(USER);
			Portfolio portfolio = userBusiness.getUserPortfolio(user.getId(), null, null);
			try {
				final int accountId = Integer.valueOf(request.getParameter(ACCOUNT_ID));
				final double movement = Double.valueOf(request.getParameter(LIQUIDITY));
				final double yield = Double.valueOf(request.getParameter(YIELD));
				final double buy = Double.valueOf(request.getParameter(BUY));
				final double sell = Double.valueOf(request.getParameter(SELL));
				final double taxe = Double.valueOf(request.getParameter(TAXE));
				final String commentary = request.getParameter(COMMENTARY);
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
					newLiquidity = new BigDecimal(newLiquidity, mathContext).doubleValue();
					userBusiness.updateLiquidity(account, newLiquidity);
					portfolio = userBusiness.getUserPortfolio(user.getId(), null, null);
					shareValueBusiness.updateCurrentShareValue(portfolio, account, movement, yield, buy, sell, taxe, commentary);
					response.sendRedirect(HOMEMOBILE);
				}

				// business.updateOneCurrency(portfolio.getCurrency());
				// business.addOrUpdateCompaniesLimitedRequest(portfolio.getCompaniesYahooIdRealTime());

			} catch (final NumberFormatException e) {
				response.getWriter().write("{\"error\":" + e.getMessage() + "\"}");
			}
		} catch (final Throwable t) {
			LOG.error("Error: " + t.getMessage(), t);
			throw new ServletException("Error: " + t.getMessage(), t);
		}
	}

	@Override
	protected final void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		doGet(request, response);
	}
}
