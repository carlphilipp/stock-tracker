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

package fr.cph.stock.web.servlet.options;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import fr.cph.stock.business.Business;
import fr.cph.stock.business.IBusiness;
import fr.cph.stock.entities.Portfolio;
import fr.cph.stock.entities.User;
import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.exception.LanguageException;
import fr.cph.stock.language.LanguageFactory;
import fr.cph.stock.util.Info;
import fr.cph.stock.web.servlet.CookieManagement;

/**
 * This servlet is called whenever the user want to access or modify its options
 * 
 * @author Carl-Philipp Harmant
 * 
 */
@WebServlet(name = "OptionsServlet", urlPatterns = { "/options" })
public class OptionsServlet extends HttpServlet {

	/** Serialization **/
	private static final long serialVersionUID = 1L;
	/** Logger **/
	private static final Logger log = Logger.getLogger(OptionsServlet.class);
	/** Business **/
	private IBusiness business;
	/** Language **/
	private LanguageFactory language;
	/** Cookie valididity time **/
	private final int ONE_YEAR_COOKIE = 60 * 60 * 24 * 365;
	/** Format list **/
	private List<String> formatList;
	/** Timezone List **/
	private List<String> timeZoneList;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.GenericServlet#init()
	 */
	@Override
	public void init() throws ServletException {
		business = new Business();
		formatList = Arrays.asList(Locale.getISOLanguages());
		Collections.sort(formatList);
		timeZoneList = Arrays.asList(TimeZone.getAvailableIDs());
		Collections.sort(timeZoneList);
		try {
			language = LanguageFactory.getInstance();
		} catch (LanguageException e) {
			log.error(e.getMessage(), e);
			throw new ServletException("Error: " + e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		try {
			HttpSession session = request.getSession(false);
			User user = (User) session.getAttribute("user");
			String update = request.getParameter("update");
			Portfolio portfolio = business.getUserPortfolio(user.getId(), null, null);
			String quoteRes = null, currencyRes = null, parityRes = null, stopLossRes = null, objectiveRes = null, yield1Res = null, yield2Res = null;
			if (update != null) {
				String currency = request.getParameter("currency");
				String format = request.getParameter("format");
				String timeZone = request.getParameter("timeZone");
				String datePattern = request.getParameter("datePattern");
				String autoUpdate = request.getParameter("autoUpdate");
				String updateSendMail = null;
				Integer updateTime = null;
				if (autoUpdate == null) {
					updateTime = null;
				} else {
					updateTime = Integer.valueOf(request.getParameter("updateTime"));
					updateSendMail = request.getParameter("autoUpdateEmail");
				}

				String quote = request.getParameter("quote");
				String currency2 = request.getParameter("currency2");
				String parity = request.getParameter("parity");
				String stopLoss = request.getParameter("stopLoss");
				String objective = request.getParameter("objective");
				String yield1 = request.getParameter("yield1");
				String yield2 = request.getParameter("yield2");

				Currency cur = Currency.getEnum(currency);
				if (cur != portfolio.getCurrency()) {
					portfolio.setCurrency(cur);
					business.updatePortfolio(portfolio);
				}
				user.setLocale(format);
				user.setTimeZone(timeZone);
				user.setDatePattern(datePattern);
				user.setUpdateHourTime(updateTime);
				if (updateSendMail != null) {
					user.setUpdateSendMail(true);
				} else {
					user.setUpdateSendMail(false);
				}
				business.updateUser(user);

				boolean bool = addCookieToResponse(response, "quote", quote);
				if (bool) {
					quoteRes = "checked";
				}
				bool = addCookieToResponse(response, "currency", currency2);
				if (bool) {
					currencyRes = "checked";
				}
				bool = addCookieToResponse(response, "parity", parity);
				if (bool) {
					parityRes = "checked";
				}
				bool = addCookieToResponse(response, "stopLoss", stopLoss);
				if (bool) {
					stopLossRes = "checked";
				}
				bool = addCookieToResponse(response, "objective", objective);
				if (bool) {
					objectiveRes = "checked";
				}
				bool = addCookieToResponse(response, "yield1", yield1);
				if (bool) {
					yield1Res = "checked";
				}
				bool = addCookieToResponse(response, "yield2", yield2);
				if (bool) {
					yield2Res = "checked";
				}
				request.setAttribute("updated", "Done!");
			} else {
				Cookie[] cookies = request.getCookies();
				for (Cookie cookie : cookies) {
					switch (cookie.getName()) {
					case "quote":
						quoteRes = cookie.getValue();
						break;
					case "currency":
						currencyRes = cookie.getValue();
						break;
					case "parity":
						parityRes = cookie.getValue();
						break;
					case "stopLoss":
						stopLossRes = cookie.getValue();
						break;
					case "objective":
						objectiveRes = cookie.getValue();
						break;
					case "yield1":
						yield1Res = cookie.getValue();
						break;
					case "yield2":
						yield2Res = cookie.getValue();
						break;
					default:
						break;
					}
				}
			}
			request.setAttribute("quote", quoteRes);
			request.setAttribute("currency", currencyRes);
			request.setAttribute("parity", parityRes);
			request.setAttribute("stopLoss", stopLossRes);
			request.setAttribute("objective", objectiveRes);
			request.setAttribute("yield1", yield1Res);
			request.setAttribute("yield2", yield2Res);

			request.setAttribute("portfolio", portfolio);
			request.setAttribute("currencies", Currency.values());
			request.setAttribute("format", formatList);
			request.setAttribute("timeZone", timeZoneList);

			String lang = CookieManagement.getCookieLanguage(Arrays.asList(request.getCookies()));
			request.setAttribute("language", language.getLanguage(lang));
			request.setAttribute("appTitle", Info.NAME + " &bull; Options");
			request.getRequestDispatcher("jsp/options.jsp").forward(request, response);
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new ServletException("Error: " + t.getMessage(), t);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	/**
	 * Add cookie to response
	 * 
	 * @param response
	 *            the response
	 * @param cookieName
	 *            the cookie name
	 * @param checked
	 * @return true or false
	 */
	private boolean addCookieToResponse(HttpServletResponse response, String cookieName, String checked) {
		boolean res = false;
		String value;
		if (checked != null) {
			value = "checked";
			res = true;
		} else {
			value = "";
		}
		Cookie cookie = new Cookie(cookieName, value);
		cookie.setMaxAge(ONE_YEAR_COOKIE);
		response.addCookie(cookie);
		return res;
	}
}