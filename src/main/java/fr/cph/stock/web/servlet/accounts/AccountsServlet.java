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

package fr.cph.stock.web.servlet.accounts;

import fr.cph.stock.business.AccountBusiness;
import fr.cph.stock.business.UserBusiness;
import fr.cph.stock.entities.Account;
import fr.cph.stock.entities.Portfolio;
import fr.cph.stock.entities.User;
import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.guice.GuiceInjector;
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
 */
@WebServlet(name = "AccountsServlet", urlPatterns = {"/accounts"})
public class AccountsServlet extends HttpServlet {

	private static final long serialVersionUID = -5015939908893417514L;
	private static final Logger LOG = Logger.getLogger(AccountsServlet.class);

	private AccountBusiness business;
	private UserBusiness userBusiness;
	private LanguageFactory language;

	@Override
	public final void init() throws ServletException {
		business = GuiceInjector.INSTANCE.getAccountBusiness();
		userBusiness = GuiceInjector.INSTANCE.getUserBusiness();
		language = LanguageFactory.INSTANCE;
	}

	@Override
	protected final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		try {
			final HttpSession session = request.getSession(false);
			final User user = (User) session.getAttribute(USER);
			final String add = request.getParameter(ADD);
			final String mod = request.getParameter(MOD);
			final String del = request.getParameter(DELETE);
			final String lang = CookieManagement.getCookieLanguage(Arrays.asList(request.getCookies()));

			if (add != null) {
				addAccount(request, user.getId());
			}
			if (mod != null) {
				modifyAccount(request, user.getId());
			}
			if (del != null) {
				deleteAccount(request);
			}
			final Portfolio portfolio = userBusiness.getUserPortfolio(user.getId(), null, null);

			request.setAttribute(LANGUAGE, language.getLanguage(lang));
			request.setAttribute(PORTFOLIO, portfolio);
			request.setAttribute(CURRENCIES, Currency.values());
			request.setAttribute(APP_TITLE, Info.NAME + " &bull;   Accounts");
			request.getRequestDispatcher("jsp/accounts.jsp").forward(request, response);
		} catch (final Throwable t) {
			LOG.error(t.getMessage(), t);
			throw new ServletException("Error: " + t.getMessage(), t);
		}
	}

	private void addAccount(final HttpServletRequest request, final int userId) {
		final String acc = request.getParameter(ACCOUNT);
		final String currency = request.getParameter(CURRENCY);
		final String liquidity = request.getParameter(LIQUIDITY);

		final Account account = new Account();
		account.setName(acc);
		account.setCurrency(Currency.getEnum(currency));
		account.setLiquidity(Double.valueOf(liquidity));
		account.setUserId(userId);
		account.setDel(true);
		business.addAccount(account);

		request.setAttribute(MESSAGE, ADDED);
	}

	private void modifyAccount(final HttpServletRequest request, final int userId) {
		final String id = request.getParameter(ID);
		final String acc = request.getParameter(ACCOUNT);
		final String currency = request.getParameter(CURRENCY);
		final String liquidity = request.getParameter(LIQUIDITY);

		final Account account = new Account();
		account.setName(acc);
		account.setCurrency(Currency.getEnum(currency));
		account.setLiquidity(Double.valueOf(liquidity));
		account.setUserId(userId);
		account.setId(Integer.parseInt(id));
		business.updateAccount(account);

		request.setAttribute(MESSAGE, MODIFIED_MESSAGE);
	}

	private void deleteAccount(final HttpServletRequest request) {
		final String id = request.getParameter(ID);
		final String delete = request.getParameter(DELETE_2);
		if (delete.equals(Boolean.toString(true))) {
			final Account account = new Account();
			account.setId(Integer.parseInt(id));
			business.deleteAccount(account);
		} else {
			request.setAttribute(ERROR, "You are not allowed to delete this account!");
		}
	}

	@Override
	protected final void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		doGet(request, response);
	}
}
