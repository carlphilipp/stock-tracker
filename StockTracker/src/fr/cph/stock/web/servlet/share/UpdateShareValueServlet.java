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

import java.io.IOException;
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
import fr.cph.stock.exception.YahooException;

@WebServlet(name = "UpdateShareValueServlet", urlPatterns = { "/updatesharevalue" })
public class UpdateShareValueServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private final MathContext mathContext = MathContext.DECIMAL32;

	private static final Logger log = Logger.getLogger(UpdateShareValueServlet.class);
	private IBusiness business;

	public void init() {
		business = new Business();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			HttpSession session = request.getSession(false);
			StringBuilder message = new StringBuilder();

			User user = (User) session.getAttribute("user");

			String _commentaryUpdated = request.getParameter("commentaryUpdated");
			if (_commentaryUpdated == null) {

				Integer _account = Integer.parseInt(request.getParameter("account"));
				Double movement = Double.parseDouble(request.getParameter("movement"));
				Double yield = Double.parseDouble(request.getParameter("yield"));
				Double buy = Double.parseDouble(request.getParameter("buy"));
				Double sell = Double.parseDouble(request.getParameter("sell"));
				Double taxe = Double.parseDouble(request.getParameter("taxe"));
				String _commentary = request.getParameter("commentary");

				String commentary = null;
				if (!_commentary.equals("")) {
					commentary = _commentary;
				}

				try {
					Portfolio portfolio = business.getUserPortfolio(user.getId(), null, null);
					Account account = portfolio.getAccount(_account);
					double newLiquidity = account.getLiquidity() + movement + yield - buy + sell - taxe;
					newLiquidity = (new BigDecimal(newLiquidity, mathContext)).doubleValue();
					business.updateLiquidity(account, newLiquidity);
					message.append("'" + account.getName() + "' liquidity new value: " + newLiquidity);
					portfolio = business.getUserPortfolio(user.getId(), null, null);
					business.updateCurrentShareValue(portfolio, account, movement, yield, buy, sell, taxe, commentary);
				} catch (YahooException e) {
					log.error(e.getMessage(), e);
				}
			} else {
				int shareId = Integer.valueOf(request.getParameter("shareId"));
				ShareValue sv = business.selectOneShareValue(shareId);
				sv.setCommentary(_commentaryUpdated);
				business.updateCommentaryShareValue(sv);
				message.append("Modified!");
			}
			request.setAttribute("message", message);
			request.getRequestDispatcher("sharevalue?page=1").forward(request, response);
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new ServletException("Error: " + t.getMessage(), t);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
