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

package fr.cph.stock.web.servlet.list;

import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import fr.cph.stock.business.Business;
import fr.cph.stock.business.IBusiness;
import fr.cph.stock.entities.Follow;
import fr.cph.stock.entities.User;
import fr.cph.stock.exception.LanguageException;
import fr.cph.stock.exception.YahooException;
import fr.cph.stock.language.LanguageFactory;
import fr.cph.stock.util.Info;
import fr.cph.stock.web.servlet.CookieManagement;

/**
 * This servlet is called to add a company to follow
 * 
 * @author Carl-Philipp Harmant
 * 
 */
@WebServlet(name = "AddFollowServlet", urlPatterns = { "/addfollow" })
public class AddFollowServlet extends HttpServlet {

	/** Serialization **/
	private static final long serialVersionUID = -8367279160386302241L;
	/** Logger **/
	private static final Logger LOG = Logger.getLogger(AddFollowServlet.class);
	/** Business **/
	private IBusiness business;
	/** Language **/
	private LanguageFactory language;

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
			if (request.getParameter("delete") != null) {
				String deleteFollowId = request.getParameter("deleteFollowId");
				business.deleteFollow(Integer.parseInt(deleteFollowId));
				request.setAttribute("message", "Deleted !");
			} else {
				try {
					String ticker = request.getParameter("ticker");
					String low = request.getParameter("lower");
					Double lower = null, higher = null;
					if (!low.equals("")) {
						lower = Double.valueOf(low);
					}
					String high = request.getParameter("higher");
					if (!high.equals("")) {
						higher = Double.valueOf(high);
					}
					business.addFollow(user, ticker, lower, higher);
					request.setAttribute("message", "Done !");
				} catch (YahooException e) {
					request.setAttribute("error", "Error during the update: " + e.getMessage());
				}
			}
			List<Follow> follows = business.getListFollow(user.getId());
			request.setAttribute("follows", follows);
			String lang = CookieManagement.getCookieLanguage(Arrays.asList(request.getCookies()));
			request.setAttribute("language", language.getLanguage(lang));
			request.setAttribute("appTitle", Info.NAME + " &bull; List");
			request.getRequestDispatcher("jsp/list.jsp").forward(request, response);
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
