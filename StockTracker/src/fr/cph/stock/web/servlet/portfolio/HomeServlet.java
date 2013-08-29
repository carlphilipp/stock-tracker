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

package fr.cph.stock.web.servlet.portfolio;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.quartz.SchedulerException;

import fr.cph.stock.business.Business;
import fr.cph.stock.business.IBusiness;
import fr.cph.stock.cron.Job;
import fr.cph.stock.entities.Index;
import fr.cph.stock.entities.Portfolio;
import fr.cph.stock.entities.User;
import fr.cph.stock.exception.LanguageException;
import fr.cph.stock.exception.YahooException;
import fr.cph.stock.language.LanguageFactory;
import fr.cph.stock.util.Info;
import fr.cph.stock.web.servlet.CookieManagement;

/**
 * Home servlet
 * 
 * @author Carl-Philipp Harmant
 * 
 */
@WebServlet(name = "HomeServlet", urlPatterns = { "/home" }, loadOnStartup = 1)
public class HomeServlet extends HttpServlet {

	/** Serialization **/
	private static final long serialVersionUID = 122322259823208331L;
	/** Logger **/
	private static final Logger LOG = Logger.getLogger(HomeServlet.class);
	/** Business **/
	private IBusiness business;
	/** Language **/
	private LanguageFactory language;
	/** Job **/
	private Job job;

	@Override
	public final void init() throws ServletException {
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
			String hostName = inetAddress.getHostName();
			if (!hostName.equals("carl-Laptop")) {
				job = new Job();
				job.run();
			}
		} catch (UnknownHostException | SchedulerException e) {
			LOG.error(e.getMessage(), e);
		}

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
			User user = (User) session.getAttribute("user");
			Portfolio portfolio;
			try {
				String day = request.getParameter("days");
				if (day != null) {
					int days = Integer.parseInt(day);
					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.DATE, -days);
					portfolio = business.getUserPortfolio(user.getId(), cal.getTime(), null);
				} else {
					portfolio = business.getUserPortfolio(user.getId(), null, null);
				}
				if (portfolio.getShareValues().size() != 0) {
					Date from = portfolio.getShareValues().get(portfolio.getShareValues().size() - 1).getDate();
					List<Index> indexes = business.getIndexes(Info.YAHOOID_CAC40, from, null);
					List<Index> indexes2 = business.getIndexes(Info.YAHOOID_SP500, from, null);
					portfolio.addIndexes(indexes);
					portfolio.addIndexes(indexes2);
				}
				request.setAttribute("portfolio", portfolio);
			} catch (YahooException e) {
				LOG.error("Error: " + e.getMessage(), e);
			}
			String lang = CookieManagement.getCookieLanguage(Arrays.asList(request.getCookies()));
			request.setAttribute("language", language.getLanguage(lang));
			request.setAttribute("appTitle", Info.NAME + " &bull; Portfolio");
			request.getRequestDispatcher("jsp/home.jsp").forward(request, response);
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
