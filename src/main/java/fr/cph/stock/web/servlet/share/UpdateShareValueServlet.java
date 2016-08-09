/**
 * Copyright 2013 Carl-Philipp Harmant
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

package fr.cph.stock.web.servlet.share;

import fr.cph.stock.business.Business;
import fr.cph.stock.business.IBusiness;
import fr.cph.stock.entities.Account;
import fr.cph.stock.entities.Portfolio;
import fr.cph.stock.entities.ShareValue;
import fr.cph.stock.entities.User;
import fr.cph.stock.exception.YahooException;
import org.apache.commons.lang.StringUtils;
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
 * This servlet is called when the user want to update a share value
 *
 * @author Carl-Philipp Harmant
 *
 */
@WebServlet(name = "UpdateShareValueServlet", urlPatterns = {"/updatesharevalue"})
public class UpdateShareValueServlet extends HttpServlet {

	private static final long serialVersionUID = 7284798829015895373L;
	private static final Logger LOG = Logger.getLogger(UpdateShareValueServlet.class);
	private IBusiness business;
	private final MathContext mathContext = MathContext.DECIMAL32;

	@Override
	public final void init() {
		this.business = Business.getInstance();
	}

	@Override
	protected final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		try {
			final HttpSession session = request.getSession(false);
			final StringBuilder message = new StringBuilder();
			final User user = (User) session.getAttribute(USER);
			final String commUpdated = request.getParameter(COMMENTARY_UPDATED);

			if (commUpdated == null) {
				final Integer acc = Integer.parseInt(request.getParameter(ACCOUNT));
				final Double movement = Double.parseDouble(request.getParameter(MOVEMENT));
				final Double yield = Double.parseDouble(request.getParameter(YIELD));
				final Double buy = Double.parseDouble(request.getParameter(BUY));
				final Double sell = Double.parseDouble(request.getParameter(SELL));
				final Double taxe = Double.parseDouble(request.getParameter(TAXE));
				final String commParam = request.getParameter(COMMENTARY);
				final String commentary = StringUtils.isNotEmpty(commParam) ? commParam : null;
				try {
					Portfolio portfolio = business.getUserPortfolio(user.getId(), null, null);
					Account account = portfolio.getAccount(acc);
					double newLiquidity = account.getLiquidity() + movement + yield - buy + sell - taxe;
					newLiquidity = new BigDecimal(newLiquidity, mathContext).doubleValue();
					business.updateLiquidity(account, newLiquidity);
					message.append("'").append(account.getName()).append("' liquidity new value: ").append(newLiquidity);
					portfolio = business.getUserPortfolio(user.getId(), null, null);
					business.updateCurrentShareValue(portfolio, account, movement, yield, buy, sell, taxe, commentary);
				} catch (final YahooException e) {
					LOG.error(e.getMessage(), e);
				}
			} else {
				final int shareId = Integer.valueOf(request.getParameter(SHARE_ID));
				final ShareValue sv = business.selectOneShareValue(shareId);
				sv.setCommentary(commUpdated);
				business.updateCommentaryShareValue(sv);
				message.append("Modified!");
			}
			request.setAttribute(MESSAGE, message);
			request.getRequestDispatcher("sharevalue?page=1").forward(request, response);
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
