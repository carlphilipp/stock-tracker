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

package fr.cph.stock.web.servlet.list;

import fr.cph.stock.business.impl.FollowBusinessImpl;
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
import java.util.Arrays;
import java.util.List;

import static fr.cph.stock.util.Constants.*;

/**
 * This servlet is called to add a company to follow
 *
 * @author Carl-Philipp Harmant
 */
@WebServlet(name = "AddFollowServlet", urlPatterns = {"/addfollow"})
public class AddFollowServlet extends HttpServlet {

	private static final long serialVersionUID = -8367279160386302241L;
	private static final Logger LOG = Logger.getLogger(AddFollowServlet.class);

	private FollowBusinessImpl followBusiness;
	private LanguageFactory language;

	@Override
	public final void init() throws ServletException {
		followBusiness = FollowBusinessImpl.INSTANCE;
		language = LanguageFactory.INSTANCE;
	}

	@Override
	protected final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		try {
			final HttpSession session = request.getSession(false);
			final User user = (User) session.getAttribute(USER);
			final String lang = CookieManagement.getCookieLanguage(Arrays.asList(request.getCookies()));

			if (request.getParameter(DELETE) != null) {
				delete(request);
			} else {
				add(request, user);
			}

			final List<Follow> follows = followBusiness.getListFollow(user.getId());

			request.setAttribute(FOLLOWS, follows);
			request.setAttribute(LANGUAGE, language.getLanguage(lang));
			request.setAttribute(APP_TITLE, Info.NAME + " &bull; List");
			request.getRequestDispatcher("jsp/list.jsp").forward(request, response);
		} catch (final Throwable t) {
			LOG.error(t.getMessage(), t);
			throw new ServletException("Error: " + t.getMessage(), t);
		}
	}

	private void delete(final HttpServletRequest request) {
		final String deleteFollowId = request.getParameter(DELETE_FOLLOW_ID);
		followBusiness.deleteFollow(Integer.parseInt(deleteFollowId));
		request.setAttribute(MESSAGE, "Deleted !");
	}

	private void add(final HttpServletRequest request, final User user) {
		try {
			final String ticker = request.getParameter(TICKER);
			final String low = request.getParameter(LOWER);
			final Double lower = !low.equals("") ? Double.valueOf(low) : null;
			final String high = request.getParameter(HIGHER);
			final Double higher = !high.equals("") ? Double.valueOf(high) : null;

			followBusiness.addFollow(user, ticker, lower, higher);

			request.setAttribute(MESSAGE, "Done !");
		} catch (final YahooException e) {
			request.setAttribute(ERROR, "Error during the update: " + e.getMessage());
		}
	}

	@Override
	protected final void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		doGet(request, response);
	}
}
