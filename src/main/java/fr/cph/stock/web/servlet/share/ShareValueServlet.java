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

package fr.cph.stock.web.servlet.share;

import fr.cph.stock.business.UserBusiness;
import fr.cph.stock.entities.Portfolio;
import fr.cph.stock.entities.User;
import fr.cph.stock.exception.NotFoundException;
import fr.cph.stock.exception.YahooException;
import fr.cph.stock.guice.GuiceInjector;
import fr.cph.stock.language.LanguageFactory;
import fr.cph.stock.util.Info;
import fr.cph.stock.web.servlet.CookieManagement;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;

import static fr.cph.stock.util.Constants.*;

/**
 * This servlet is called when the user want to access to the history page
 *
 * @author Carl-Philipp Harmant
 */
@Log4j2
@WebServlet(name = "ShareValueServlet", urlPatterns = {"/sharevalue"})
public class ShareValueServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private UserBusiness userBusiness;
	private LanguageFactory language;
	private static final int ITEM_MAX = 20;

	@Override
	public final void init() throws ServletException {
		userBusiness = GuiceInjector.INSTANCE.getUserBusiness();
		language = LanguageFactory.INSTANCE;
	}

	@Override
	protected final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		try {
			final HttpSession session = request.getSession(false);
			final User user = (User) session.getAttribute(USER);
			final String page = request.getParameter(PAGE);
			final int pageNumber = StringUtils.isEmpty(page) ? 1 : Integer.parseInt(page);
			try {
				final Portfolio portfolio = userBusiness.getUserPortfolio(user.getId()).orElseThrow(() -> new NotFoundException(user.getId()));
				if (portfolio.getShareValues().size() != 0) {
					int begin = pageNumber * ITEM_MAX - ITEM_MAX;
					int end = pageNumber * ITEM_MAX - 1;
					int nbPage = portfolio.getShareValues().size() / ITEM_MAX + 1;
					if (pageNumber == 0) {
						begin = 0;
						end = portfolio.getShareValues().size() - 1;
					}
					if (pageNumber == nbPage) {
						end = portfolio.getShareValues().size() - 1;
					}
					request.setAttribute(BEGIN, begin);
					request.setAttribute(END, end);
					request.setAttribute(PAGE, page);
					request.setAttribute(NB_PAGE, nbPage);
				}
				request.setAttribute(PORTFOLIO, portfolio);
			} catch (final YahooException e) {
				log.error(e.getMessage(), e);
				throw new ServletException("Error: " + e.getMessage(), e);
			}
			final String lang = CookieManagement.getCookieLanguage(Arrays.asList(request.getCookies()));
			request.setAttribute(LANGUAGE, language.getLanguage(lang));
			request.setAttribute(APP_TITLE, Info.NAME + " &bull; History");
			request.getRequestDispatcher("jsp/sharevalue.jsp").forward(request, response);
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
