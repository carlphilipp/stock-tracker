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
import fr.cph.stock.entities.Portfolio;
import fr.cph.stock.entities.User;
import fr.cph.stock.exception.LanguageException;
import fr.cph.stock.exception.YahooException;
import fr.cph.stock.language.LanguageFactory;
import fr.cph.stock.util.Info;
import fr.cph.stock.web.servlet.CookieManagement;

/**
 * This servlet is called when the user want to access to the history page
 * 
 * @author Carl-Philipp Harmant
 * 
 */
@WebServlet(name = "ShareValueServlet", urlPatterns = { "/sharevalue" })
public class ShareValueServlet extends HttpServlet {

	/** Serialization **/
	private static final long serialVersionUID = 1L;
	/** Logger **/
	private static final Logger LOG = Logger.getLogger(ShareValueServlet.class);
	/** Business **/
	private IBusiness business;
	/** Language **/
	private LanguageFactory language;
	/** Item max **/
	private static final int ITEM_MAX = 20;

	@Override
	public final void init() throws ServletException {
		business = new Business();
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
			User user = (User) session.getAttribute("user");
			int page = Integer.parseInt(request.getParameter("page"));
			Portfolio portfolio;
			try {
				portfolio = business.getUserPortfolio(user.getId(), null, null);
				if (portfolio.getShareValues().size() != 0) {
					int begin = page * ITEM_MAX - ITEM_MAX;
					int end = page * ITEM_MAX - 1;
					int nbPage = portfolio.getShareValues().size() / ITEM_MAX + 1;
					if (page == 0) {
						begin = 0;
						end = portfolio.getShareValues().size() - 1;
					}
					if (page == nbPage) {
						end = portfolio.getShareValues().size() - 1;
					}
					request.setAttribute("begin", begin);
					request.setAttribute("end", end);
					request.setAttribute("page", page);
					request.setAttribute("nbPage", nbPage);
				}
				request.setAttribute("portfolio", portfolio);
			} catch (YahooException e) {
				LOG.error(e.getMessage(), e);
				throw new ServletException("Error: " + e.getMessage(), e);
			}
			String lang = CookieManagement.getCookieLanguage(Arrays.asList(request.getCookies()));
			request.setAttribute("language", language.getLanguage(lang));
			request.setAttribute("appTitle", Info.NAME + " &bull; History");
			request.getRequestDispatcher("jsp/sharevalue.jsp").forward(request, response);
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
