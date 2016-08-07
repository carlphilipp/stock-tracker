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
import fr.cph.stock.csv.Csv;
import fr.cph.stock.entities.Account;
import fr.cph.stock.entities.Portfolio;
import fr.cph.stock.entities.ShareValue;
import fr.cph.stock.entities.User;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static fr.cph.stock.util.Constants.*;

/**
 * Creat history from CSV file
 *
 * @author Carl-Philipp Harmant
 */
@WebServlet(name = "CreateHistoryServlet", urlPatterns = {"/createhistory"})
@MultipartConfig
public class CreateHistoryServlet extends HttpServlet {

	private static final long serialVersionUID = -2999218921595727810L;
	private static final Logger LOG = Logger.getLogger(CreateHistoryServlet.class);
	private IBusiness business;

	@Override
	public final void init() {
		this.business = Business.getInstance();
	}

	@Override
	protected final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		try {
			final HttpSession session = request.getSession(false);
			final User user = (User) session.getAttribute(USER);
			final String liquidity = request.getParameter(LIQUIDITY);
			final String acc = request.getParameter(ACCOUNT);

			final Portfolio portfolio = business.getUserPortfolio(user.getId(), null, null);
			final Account account = portfolio.getAccount(acc);

			final Part p1 = request.getPart(FILE);
			try (final InputStream is = p1.getInputStream();
				 final BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
				final Csv csv = new Csv(br, user, acc);
				final List<ShareValue> shareValues = csv.getShareValueList();
				for (final ShareValue sv : shareValues) {
					business.addShareValue(sv);
				}
				if (!liquidity.equals("")) {
					business.updateLiquidity(account, Double.parseDouble(liquidity));
				}
				request.getRequestDispatcher("sharevalue?page=1").forward(request, response);
			}
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
