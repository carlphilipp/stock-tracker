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

package fr.cph.stock.web.servlet.list;

import fr.cph.stock.business.Business;
import fr.cph.stock.business.IBusiness;
import fr.cph.stock.entities.Follow;
import fr.cph.stock.entities.User;
import fr.cph.stock.exception.YahooException;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static fr.cph.stock.util.Constants.*;

/**
 * This servlet is called when the user want to update the list
 *
 * @author Carl-Philipp Harmant
 */
@WebServlet(name = "UpdateListServlet", urlPatterns = {"/updatelist"})
public class UpdateListServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(UpdateListServlet.class);

	private IBusiness business;
	private LanguageFactory language;

	@Override
	public final void init() throws ServletException {
		this.business = Business.getInstance();
		this.language = LanguageFactory.getInstance();
	}

	@Override
	protected final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		try {
			final HttpSession session = request.getSession(false);
			final StringBuilder error = new StringBuilder();
			final User user = (User) session.getAttribute(USER);
			final String lang = CookieManagement.getCookieLanguage(Arrays.asList(request.getCookies()));

			update(user.getId(), error);

			final List<Follow> follows = business.getListFollow(user.getId());
			request.setAttribute(FOLLOWS, follows);
			if (!error.toString().equals("")) {
				request.setAttribute(UPDATE_STATUS, "<span class='cQuoteDown'>" + error.toString() + "</span>");
			} else {
				request.setAttribute(UPDATE_STATUS, "<span class='cQuoteUp'>Refresh done!</span>");
			}
			request.setAttribute(LANGUAGE, language.getLanguage(lang));
			request.setAttribute(APP_TITLE, Info.NAME + " &bull; List");
			request.getRequestDispatcher("jsp/list.jsp").forward(request, response);
		} catch (final Throwable t) {
			LOG.error(t.getMessage(), t);
			throw new ServletException("Error: " + t.getMessage(), t);
		}
	}

	@Override
	protected final void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		doGet(request, response);
	}

	private void update(final int userId, final StringBuilder error) {
		try {
			final List<Follow> follows = business.getListFollow(userId);
			final List<String> followsString = new ArrayList<>();
			for (final Follow follow : follows) {
				if (follow.getCompany().getRealTime() != null && follow.getCompany().getRealTime()) {
					followsString.add(follow.getCompany().getYahooId());
				}
			}
			business.addOrUpdateCompaniesLimitedRequest(followsString);
		} catch (final YahooException e1) {
			error.append(e1.getMessage()).append(" ");
		}
	}
}
