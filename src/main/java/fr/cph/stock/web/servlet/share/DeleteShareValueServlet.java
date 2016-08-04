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

package fr.cph.stock.web.servlet.share;

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
import fr.cph.stock.entities.ShareValue;
import fr.cph.stock.entities.User;

/**
 * This servlet is called when the user want to delete a share value
 * 
 * @author Carl-Philipp Harmant
 * 
 */
@WebServlet(name = "DeleteShareValueServlet", urlPatterns = { "/deletesharevalue" })
public class DeleteShareValueServlet extends HttpServlet {

	/** Serialization **/
	private static final long serialVersionUID = 6742409927502374595L;
	/** Logger **/
	private static final Logger LOG = Logger.getLogger(DeleteShareValueServlet.class);
	/** Business **/
	private IBusiness business;
	/** Precision **/
	private final MathContext mathContext = MathContext.DECIMAL32;

	@Override
	public final void init() throws ServletException {
		business = Business.getInstance();
	}

	@Override
	protected final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		try {
			HttpSession session = request.getSession(false);
			StringBuilder message = new StringBuilder();
			User user = (User) session.getAttribute("user");
			String shareIdd = request.getParameter("shareId");
			int shareId = Integer.parseInt(shareIdd);
			Double liquidityMovement = Double.parseDouble(request.getParameter("liquidityMovement"));
			Double yield = Double.parseDouble(request.getParameter("yield"));
			Double buy = Double.parseDouble(request.getParameter("buy"));
			Double sell = Double.parseDouble(request.getParameter("sell"));
			Double taxe = Double.parseDouble(request.getParameter("taxe"));
			String acc = request.getParameter("account");

			Portfolio portfolio = business.getUserPortfolio(user.getId(), null, null);
			Account account = portfolio.getAccount(acc);
			ShareValue shareValue = new ShareValue();
			shareValue.setId(shareId);
			if (account == null) {
				business.deleteShareValue(shareValue);
				message.append("Account not found, probably deleted before. Line has still been deleted!");
				request.setAttribute("warn", message);
			} else {
				Double total = liquidityMovement + yield - buy + sell - taxe;
				total = new BigDecimal(total, mathContext).doubleValue();
				if (total != 0.0) {
					double newLiquidity = account.getLiquidity() - total;
					business.updateLiquidity(account, new BigDecimal(newLiquidity, mathContext).doubleValue());
					message.append("Liquidity new value: " + (new BigDecimal(newLiquidity, mathContext)).doubleValue() + "<br>");
				}
				business.deleteShareValue(shareValue);
				message.append("Done !");
				request.setAttribute("message", message);
			}

			request.getRequestDispatcher("sharevalue?page=1").forward(request, response);
		} catch (Throwable t) {
			LOG.error(t.getMessage(), t);
			throw new ServletException("Error: " + t.getMessage(), t);
		}
	}

	@Override
	protected final void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		doGet(request, response);
	}
}
