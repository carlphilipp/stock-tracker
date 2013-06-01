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

import java.io.IOException;
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

@WebServlet(name = "HomeServlet", urlPatterns = { "/home" }, loadOnStartup = 1)
public class HomeServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger log = Logger.getLogger(HomeServlet.class);
	private IBusiness business;
	private LanguageFactory language;
	private Job job;

	@Override
	public void init() throws ServletException {
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
			String hostName = inetAddress.getHostName();
			if (!hostName.equals("carl-Laptop")) {
				job = new Job();
				job.run();
			}
		} catch (UnknownHostException | SchedulerException e) {
			log.error(e.getMessage(), e);
		}

		business = new Business();
		try {
			language = LanguageFactory.getInstance();
		} catch (LanguageException e) {
			log.error(e.getMessage(), e);
			throw new ServletException("Error: " + e.getMessage(), e);
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		try {
			HttpSession session = request.getSession(false);
			User user = (User) session.getAttribute("user");
			Portfolio portfolio;
			try {
				String _days = request.getParameter("days");
				if (_days != null) {
					int days = Integer.parseInt(_days);
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
				log.error("Error: " + e.getMessage(), e);
			}
			String lang = CookieManagement.getCookieLanguage(Arrays.asList(request.getCookies()));
			request.setAttribute("language", language.getLanguage(lang));
			request.setAttribute("appTitle", Info.NAME + " &bull; Portfolio");
			request.getRequestDispatcher("jsp/home.jsp").forward(request, response);
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new ServletException("Error: " + t.getMessage(), t);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
