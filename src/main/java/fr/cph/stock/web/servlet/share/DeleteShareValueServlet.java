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

package fr.cph.stock.web.servlet.share;

import fr.cph.stock.business.ShareValueBusiness;
import fr.cph.stock.business.UserBusiness;
import fr.cph.stock.entities.Account;
import fr.cph.stock.entities.Portfolio;
import fr.cph.stock.entities.ShareValue;
import fr.cph.stock.entities.User;
import fr.cph.stock.guice.GuiceInjector;
import lombok.extern.log4j.Log4j2;

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
 * This servlet is called when the user want to delete a share value
 *
 * @author Carl-Philipp Harmant
 */
@Log4j2
@WebServlet(name = "DeleteShareValueServlet", urlPatterns = {"/deletesharevalue"})
public class DeleteShareValueServlet extends HttpServlet {

	private static final long serialVersionUID = 6742409927502374595L;
	private UserBusiness userBusiness;
	private ShareValueBusiness shareValueBusiness;
	private final MathContext mathContext = MathContext.DECIMAL32;

	@Override
	public final void init() throws ServletException {
		userBusiness = GuiceInjector.INSTANCE.getUserBusiness();
		shareValueBusiness = GuiceInjector.INSTANCE.getShareValueBusiness();
	}

	@Override
	protected final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		try {
			final HttpSession session = request.getSession(false);
			final StringBuilder message = new StringBuilder();
			final User user = (User) session.getAttribute(USER);
			final String shareIdd = request.getParameter(SHARE_ID);
			final int shareId = Integer.parseInt(shareIdd);
			final Double liquidityMovement = Double.parseDouble(request.getParameter(LIQUIDITY_MOVEMENT));
			final Double yield = Double.parseDouble(request.getParameter(YIELD));
			final Double buy = Double.parseDouble(request.getParameter(BUY));
			final Double sell = Double.parseDouble(request.getParameter(SELL));
			final Double taxe = Double.parseDouble(request.getParameter(TAXE));
			final String acc = request.getParameter(ACCOUNT);

			final Portfolio portfolio = userBusiness.getUserPortfolio(user.getId());
			final Account account = portfolio.getAccount(acc);
			final ShareValue shareValue = new ShareValue();
			shareValue.setId(shareId);
			if (account == null) {
				shareValueBusiness.deleteShareValue(shareValue);
				message.append("Account not found, probably deleted before. Line has still been deleted!");
				request.setAttribute(WARN, message);
			} else {
				Double total = liquidityMovement + yield - buy + sell - taxe;
				total = new BigDecimal(Double.toString(total), mathContext).doubleValue();
				if (total != 0.0) {
					double newLiquidity = account.getLiquidity() - total;
					userBusiness.updateLiquidity(account, new BigDecimal(Double.toString(newLiquidity), mathContext).doubleValue());
					message.append("Liquidity new value: ").append((new BigDecimal(Double.toString(newLiquidity), mathContext)).doubleValue()).append("<br>");
				}
				shareValueBusiness.deleteShareValue(shareValue);
				message.append("Done !");
				request.setAttribute(MESSAGE, message);
			}
			request.getRequestDispatcher("sharevalue?page=1").forward(request, response);
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
