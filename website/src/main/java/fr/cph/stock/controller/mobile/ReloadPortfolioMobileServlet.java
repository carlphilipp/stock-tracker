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

import fr.cph.stock.entities.Portfolio;
import fr.cph.stock.entities.User;
import fr.cph.stock.exception.NotFoundException;
import fr.cph.stock.exception.YahooException;
import fr.cph.stock.service.CompanyService;
import fr.cph.stock.service.UserService;
import fr.cph.stock.util.Constants;
import lombok.extern.log4j.Log4j2;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * This servlet is called by mobile to reload the portfolio
 *
 * @author Carl-Philipp Harmant
 */
// FIXME to convert to a spring rest controller
@Log4j2
//@WebServlet(name = "ReloadPortfolioMobileServlet", urlPatterns = {"/reloadportfoliomobile"})
public class ReloadPortfolioMobileServlet extends HttpServlet {

	private static final long serialVersionUID = 5211078955305413271L;
	private CompanyService companyService;
	private UserService userService;

	@Override
	public final void init() {
	}

	@Override
	protected final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		try {
			final HttpSession session = request.getSession(false);
			final User user = (User) session.getAttribute(Constants.USER);
			try {
				final Portfolio portfolio = userService.getUserPortfolio(user.getId()).orElseThrow(() -> new NotFoundException(user.getId()));
				companyService.addOrUpdateCompaniesLimitedRequest(portfolio.getCompaniesYahooIdRealTime());
				response.sendRedirect(Constants.HOMEMOBILE);
			} catch (YahooException e) {
				response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
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
