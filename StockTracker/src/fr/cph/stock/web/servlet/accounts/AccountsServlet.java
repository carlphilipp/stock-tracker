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

import java.io.IOException;
import java.util.Arrays;

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
import fr.cph.stock.entities.User;
import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.exception.LanguageException;
import fr.cph.stock.language.LanguageFactory;
import fr.cph.stock.util.Info;
import fr.cph.stock.web.servlet.CookieManagement;

@WebServlet(name = "AccountsServlet", urlPatterns = { "/accounts" })
public class AccountsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger log = Logger.getLogger(AccountsServlet.class);
	private IBusiness business;
	private LanguageFactory language;

	@Override
	public void init() throws ServletException {
		business = new Business();
		try {
			language = LanguageFactory.getInstance();
		} catch (LanguageException e) {
			log.error(e.getMessage(), e);
			throw new ServletException("Error: " + e.getMessage(), e);
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		try {
			HttpSession session = request.getSession(false);
			User user = (User) session.getAttribute("user");

			String add = request.getParameter("add");
			String mod = request.getParameter("mod");
			String del = request.getParameter("delete");
			if (add != null) {
				String _account = request.getParameter("account");
				String currency = request.getParameter("currency");
				String liquidity = request.getParameter("liquidity");

				Account account = new Account();
				account.setName(_account);
				account.setCurrency(Currency.getEnum(currency));
				account.setLiquidity(Double.valueOf(liquidity));
				account.setUserId(user.getId());
				account.setDel(true);
				business.addAccount(account);
				request.setAttribute("message", "Added!");
			}
			if (mod != null) {
				String id = request.getParameter("id");
				String _account = request.getParameter("account");
				String currency = request.getParameter("currency");
				String liquidity = request.getParameter("liquidity");
				Account account = new Account();
				account.setName(_account);
				account.setCurrency(Currency.getEnum(currency));
				account.setLiquidity(Double.valueOf(liquidity));
				account.setUserId(user.getId());
				account.setId(Integer.parseInt(id));
				business.updateAccount(account);
				request.setAttribute("message", "Modified!");

			}
			if (del != null) {
				String id = request.getParameter("id");
				String delete = request.getParameter("delete2");
				log.debug(delete);
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
			request.setAttribute("language", language.getLanguage(lang));
			request.setAttribute("portfolio", portfolio);
			request.setAttribute("currencies", Currency.values());
			request.setAttribute("appTitle", Info.NAME + " &bull;   Accounts");
			request.getRequestDispatcher("jsp/accounts.jsp").forward(request, response);
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new ServletException("Error: " + t.getMessage(), t);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}