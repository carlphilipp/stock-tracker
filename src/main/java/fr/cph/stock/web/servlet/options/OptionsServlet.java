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

import fr.cph.stock.business.Business;
import fr.cph.stock.business.IBusiness;
import fr.cph.stock.entities.Portfolio;
import fr.cph.stock.entities.User;
import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.exception.LanguageException;
import fr.cph.stock.language.LanguageFactory;
import fr.cph.stock.util.Info;
import fr.cph.stock.web.servlet.CookieManagement;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.util.*;

import static fr.cph.stock.util.Constants.*;

/**
 * This servlet is called whenever the user want to access or modify its options
 * 
 * @author Carl-Philipp Harmant
 * 
 */
@WebServlet(name = "OptionsServlet", urlPatterns = { "/options" })
public class OptionsServlet extends HttpServlet {

	/** Serialization **/
	private static final long serialVersionUID = -6025904929231678296L;
	/** Logger **/
	private static final Logger LOG = Logger.getLogger(OptionsServlet.class);
	/** Business **/
	private IBusiness business;
	/** Language **/
	private LanguageFactory language;
	/** Cookie valididity time **/
	private static final int ONE_YEAR_COOKIE = 60 * 60 * 24 * 365;
	/** Format list **/
	private List<String> formatList;
	/** Timezone List **/
	private List<String> timeZoneList;

	@Override
	public final void init() throws ServletException {
		business = Business.getInstance();
		formatList = Arrays.asList(Locale.getISOLanguages());
		Collections.sort(formatList);
		timeZoneList = Arrays.asList(TimeZone.getAvailableIDs());
		Collections.sort(timeZoneList);
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
			User user = (User) session.getAttribute(USER);
			String update = request.getParameter(UPDATE);
			Portfolio portfolio = business.getUserPortfolio(user.getId(), null, null);
			String quoteRes = null, currencyRes = null, parityRes = null, stopLossRes = null, objectiveRes = null, yield1Res = null, yield2Res = null;
			if (update != null) {
				String currency = request.getParameter(CURRENCY);
				String format = request.getParameter(FORMAT);
				String timeZone = request.getParameter(TIME_ZONE);
				String datePattern = request.getParameter(DATE_PATTERN);
				String autoUpdate = request.getParameter(AUTO_UPDATE);
				String updateSendMail = null;
				Integer updateTime = null;
				if (autoUpdate == null) {
					updateTime = null;
				} else {
					updateTime = Integer.valueOf(request.getParameter(UPDATE_TIME));
					updateSendMail = request.getParameter(AUTO_UPDATE_EMAIL);
				}

				String quote = request.getParameter(QUOTE);
				String currency2 = request.getParameter(CURRENCY_2);
				String parity = request.getParameter(PARITY);
				String stopLoss = request.getParameter(STOP_LOSS);
				String objective = request.getParameter(OBJECTIVE);
				String yield1 = request.getParameter(YIELD_1);
				String yield2 = request.getParameter(YIELD_2);

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

				boolean bool = addCookieToResponse(response, QUOTE, quote);
				if (bool) {
					quoteRes = CHECKED;
				}
				bool = addCookieToResponse(response, CURRENCY, currency2);
				if (bool) {
					currencyRes = CHECKED;
				}
				bool = addCookieToResponse(response, PARITY, parity);
				if (bool) {
					parityRes = CHECKED;
				}
				bool = addCookieToResponse(response, STOP_LOSS, stopLoss);
				if (bool) {
					stopLossRes = CHECKED;
				}
				bool = addCookieToResponse(response, OBJECTIVE, objective);
				if (bool) {
					objectiveRes = CHECKED;
				}
				bool = addCookieToResponse(response, YIELD_1, yield1);
				if (bool) {
					yield1Res = CHECKED;
				}
				bool = addCookieToResponse(response, YIELD_2, yield2);
				if (bool) {
					yield2Res = CHECKED;
				}
				request.setAttribute(UPDATED, "Done!");
			} else {
				Cookie[] cookies = request.getCookies();
				for (Cookie cookie : cookies) {
					switch (cookie.getName()) {
					case QUOTE:
						quoteRes = cookie.getValue();
						break;
					case CURRENCY:
						currencyRes = cookie.getValue();
						break;
					case PARITY:
						parityRes = cookie.getValue();
						break;
					case STOP_LOSS:
						stopLossRes = cookie.getValue();
						break;
					case OBJECTIVE:
						objectiveRes = cookie.getValue();
						break;
					case YIELD_1:
						yield1Res = cookie.getValue();
						break;
					case YIELD_2:
						yield2Res = cookie.getValue();
						break;
					default:
						break;
					}
				}
			}
			request.setAttribute(QUOTE, quoteRes);
			request.setAttribute(CURRENCY, currencyRes);
			request.setAttribute(PARITY, parityRes);
			request.setAttribute(STOP_LOSS, stopLossRes);
			request.setAttribute(OBJECTIVE, objectiveRes);
			request.setAttribute(YIELD_1, yield1Res);
			request.setAttribute(YIELD_2, yield2Res);

			request.setAttribute(PORTFOLIO, portfolio);
			request.setAttribute(CURRENCIES, Currency.values());
			request.setAttribute(FORMAT, formatList);
			request.setAttribute(TIME_ZONE, timeZoneList);

			String lang = CookieManagement.getCookieLanguage(Arrays.asList(request.getCookies()));
			request.setAttribute(LANGUAGE, language.getLanguage(lang));
			request.setAttribute(APP_TITLE, Info.NAME + " &bull; Options");
			request.getRequestDispatcher("jsp/options.jsp").forward(request, response);
		} catch (Throwable t) {
			LOG.error(t.getMessage(), t);
			throw new ServletException("Error: " + t.getMessage(), t);
		}
	}

	@Override
	protected final void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
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
	 *            if checked
	 * @return true or false
	 */
	private boolean addCookieToResponse(final HttpServletResponse response, final String cookieName, final String checked) {
		boolean res = false;
		String value;
		if (checked != null) {
			value = CHECKED;
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