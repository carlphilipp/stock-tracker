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
package fr.cph.stock.web.servlet.portfolio;

import fr.cph.stock.business.IndexBusiness;
import fr.cph.stock.business.UserBusiness;
import fr.cph.stock.cron.Job;
import fr.cph.stock.entities.Index;
import fr.cph.stock.entities.Portfolio;
import fr.cph.stock.entities.User;
import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.exception.NotFoundException;
import fr.cph.stock.exception.YahooException;
import fr.cph.stock.language.LanguageFactory;
import fr.cph.stock.util.Info;
import fr.cph.stock.web.servlet.CookieManagement;
import lombok.extern.log4j.Log4j2;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static fr.cph.stock.util.Constants.*;

/**
 * Home servlet
 *
 * @author Carl-Philipp Harmant
 */
@SessionAttributes(USER)
@Log4j2
@Controller
public class HomeServlet {

	@Autowired
	private UserBusiness userBusiness;
	@Autowired
	private IndexBusiness indexBusiness;
	private LanguageFactory language;

	@PostConstruct
	public final void init() throws ServletException {
		try {
			final InetAddress inetAddress = InetAddress.getLocalHost();
			final String hostName = inetAddress.getHostName();
			if (!hostName.equals("carl-Desktop")) {
				final Job job = new Job();
				job.run();
			}
		} catch (final UnknownHostException | SchedulerException e) {
			log.error(e.getMessage(), e);
		}
		language = LanguageFactory.INSTANCE;
	}

	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public String loadHome(final HttpServletRequest request, final HttpServletResponse response,
						   @RequestParam(value = DAYS, required = false) final String day,
						   @ModelAttribute final User user) {
		try {
			final Portfolio portfolio;
			if (day != null) {
				final int days = Integer.parseInt(day);
				final Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DATE, -days);
				portfolio = userBusiness.getUserPortfolio(user.getId(), cal.getTime(), null).orElseThrow(() -> new NotFoundException(user.getId()));
			} else {
				portfolio = userBusiness.getUserPortfolio(user.getId()).orElseThrow(() -> new NotFoundException(user.getId()));
			}
			if (portfolio.getShareValues().size() != 0) {
				final Date from = portfolio.getShareValues().get(portfolio.getShareValues().size() - 1).getDate();
				final List<Index> indexesCAC40 = indexBusiness.getIndexes(Info.YAHOO_ID_CAC40, from, null);
				final List<Index> indexesSP500 = indexBusiness.getIndexes(Info.YAHOO_ID_SP500, from, null);
				portfolio.addIndexes(indexesCAC40);
				portfolio.addIndexes(indexesSP500);
			}
			request.setAttribute(PORTFOLIO, portfolio);
		} catch (final YahooException e) {
			log.error("Error: {}", e.getMessage(), e);
		}
		final String lang = CookieManagement.getCookieLanguage(Arrays.asList(request.getCookies()));
		request.setAttribute(LANGUAGE, language.getLanguage(lang));
		request.setAttribute(APP_TITLE, Info.NAME + " &bull; Portfolio");
		request.setAttribute(CURRENCIES, Currency.values());
		return "home";
	}
}
