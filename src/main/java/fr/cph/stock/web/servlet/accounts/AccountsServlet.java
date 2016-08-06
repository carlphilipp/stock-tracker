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

package fr.cph.stock.web.servlet.accounts;

import fr.cph.stock.business.Business;
import fr.cph.stock.business.IBusiness;
import fr.cph.stock.entities.Account;
import fr.cph.stock.entities.Portfolio;
import fr.cph.stock.entities.User;
import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.exception.LanguageException;
import fr.cph.stock.language.LanguageFactory;
import fr.cph.stock.util.Info;
import fr.cph.stock.web.servlet.CookieManagement;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;

import static fr.cph.stock.util.Constants.*;

/**
 * This servlet is called to access the accounts page
 * 
 * @author Carl-Philipp Harmant
 * 
 */
@WebServlet(name = "AccountsServlet", urlPatterns = { "/accounts" })
public class AccountsServlet extends HttpServlet {

	/** Serialization **/
	private static final long serialVersionUID = -5015939908893417514L;
	/** Logger **/
	private static final Logger LOG = Logger.getLogger(AccountsServlet.class);
	/** Business **/
	private IBusiness business;
	/** Language **/
	private LanguageFactory language;

	@Override
	public final void init() throws ServletException {
		business = Business.getInstance();
		try {
			language = LanguageFactory.getInstance();
		} catch (LanguageException e) {
			LOG.error(e.getMessage(), e);
			throw new ServletException("Error: " + e.getMessage(), e);
		}
	}

	@Override
	protected final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		try {
			HttpSession session = request.getSession(false);
			User user = (User) session.getAttribute(USER);

			String add = request.getParameter(ADD);
			String mod = request.getParameter(MOD);
			String del = request.getParameter(DELETE);
			if (add != null) {
				String acc = request.getParameter(ACCOUNT);
				String currency = request.getParameter(CURRENCY);
				String liquidity = request.getParameter(LIQUIDITY);

				Account account = new Account();
				account.setName(acc);
				account.setCurrency(Currency.getEnum(currency));
				account.setLiquidity(Double.valueOf(liquidity));
				account.setUserId(user.getId());
				account.setDel(true);
				business.addAccount(account);
				request.setAttribute(MESSAGE, ADDED);
			}
			if (mod != null) {
				String id = request.getParameter(ID);
				String acc = request.getParameter(ACCOUNT);
				String currency = request.getParameter(CURRENCY);
				String liquidity = request.getParameter(LIQUIDITY);
				Account account = new Account();
				account.setName(acc);
				account.setCurrency(Currency.getEnum(currency));
				account.setLiquidity(Double.valueOf(liquidity));
				account.setUserId(user.getId());
				account.setId(Integer.parseInt(id));
				business.updateAccount(account);
				request.setAttribute(MESSAGE, MODIFIED_MESSAGE);

			}
			if (del != null) {
				String id = request.getParameter(ID);
				String delete = request.getParameter(DELETE_2);
				LOG.debug(delete);
				if (delete.equals("true")) {
					Account account = new Account();
					account.setId(Integer.parseInt(id));
					business.deleteAccount(account);
				} else {
					request.setAttribute("error", "You are not allowed to delete this account!");
				}
			}
			Portfolio portfolio = business.getUserPortfolio(user.getId(), null, null);

			String lang = CookieManagement.getCookieLanguage(Arrays.asList(request.getCookies()));
			request.setAttribute(LANGUAGE, language.getLanguage(lang));
			request.setAttribute(PORTFOLIO, portfolio);
			request.setAttribute(CURRENCIES, Currency.values());
			request.setAttribute(APP_TITLE, Info.NAME + " &bull;   Accounts");
			request.getRequestDispatcher("jsp/accounts.jsp").forward(request, response);
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