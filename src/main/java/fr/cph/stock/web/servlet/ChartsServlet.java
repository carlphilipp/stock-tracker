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

package fr.cph.stock.web.servlet;

import fr.cph.stock.business.IndexBusiness;
import fr.cph.stock.business.UserBusiness;
import fr.cph.stock.entities.Index;
import fr.cph.stock.entities.Portfolio;
import fr.cph.stock.entities.User;
import fr.cph.stock.exception.YahooException;
import fr.cph.stock.guice.GuiceInjector;
import fr.cph.stock.language.LanguageFactory;
import fr.cph.stock.util.Info;
import lombok.extern.log4j.Log4j2;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static fr.cph.stock.util.Constants.*;

/**
 * This servlet is called to change display the charts
 *
 * @author Carl-Philipp Harmant
 */
@Log4j2
@WebServlet(name = "ChartsServlet", urlPatterns = {"/charts"})
public class ChartsServlet extends HttpServlet {

	private static final long serialVersionUID = -2726055360179985134L;
	private IndexBusiness indexBusiness;
	private UserBusiness userBusiness;
	private LanguageFactory language;

	@Override
	public final void init() throws ServletException {
		indexBusiness = GuiceInjector.INSTANCE.getIndexBusiness();
		userBusiness = GuiceInjector.INSTANCE.getUserBusiness();
		language = LanguageFactory.INSTANCE;
	}

	@Override
	protected final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		try {
			final HttpSession session = request.getSession(false);
			final User user = (User) session.getAttribute(USER);
			final Portfolio portfolio;
			try {
				portfolio = userBusiness.getUserPortfolio(user.getId());
				if (portfolio.getShareValues().size() != 0) {
					Date from = portfolio.getShareValues().get(portfolio.getShareValues().size() - 1).getDate();
					List<Index> indexes = indexBusiness.getIndexes(Info.YAHOO_ID_CAC40, from, null);
					List<Index> indexes2 = indexBusiness.getIndexes(Info.YAHOO_ID_SP500, from, null);
					portfolio.addIndexes(indexes);
					portfolio.addIndexes(indexes2);
				}
				final String mapSector = portfolio.getSectorCompanies();
				final String mapCap = portfolio.getCapCompanies();
				request.setAttribute(PORTFOLIO, portfolio);
				request.setAttribute(MAP_SECTOR, mapSector);
				request.setAttribute(MAP_CAP, mapCap);
			} catch (final YahooException e) {
				log.error("Error: {}", e.getMessage(), e);
			}
			final String lang = CookieManagement.getCookieLanguage(Arrays.asList(request.getCookies()));
			request.setAttribute(LANGUAGE, language.getLanguage(lang));
			request.setAttribute(APP_TITLE, Info.NAME + " &bull;   Charts");
			request.getRequestDispatcher("jsp/charts.jsp").forward(request, response);
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
