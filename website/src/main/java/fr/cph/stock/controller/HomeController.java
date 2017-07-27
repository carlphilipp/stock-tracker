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
package fr.cph.stock.controller;

import fr.cph.stock.config.AppProperties;
import fr.cph.stock.entities.Portfolio;
import fr.cph.stock.entities.User;
import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.exception.NotFoundException;
import fr.cph.stock.language.LanguageFactory;
import fr.cph.stock.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Calendar;

import static fr.cph.stock.util.Constants.*;

/**
 * Home servlet
 *
 * @author Carl-Philipp Harmant
 */
@SessionAttributes(USER)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Log4j2
@Controller
public class HomeController {

	@NonNull
	private AppProperties appProperties;
	@NonNull
	private final UserService userService;

	@RequestMapping(value = "/home", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView home(@RequestParam(value = DAYS, required = false) final String days,
							 @ModelAttribute final User user,
							 @CookieValue(LANGUAGE) final String lang) {
		final ModelAndView model = new ModelAndView(HOME);
		final Calendar calendar = getCalendarFromDays(days);
		final Portfolio portfolio = userService.getUserPortfolio(user.getId(), calendar == null ? null : calendar.getTime()).orElseThrow(() -> new NotFoundException(user.getId()));

		model.addObject(PORTFOLIO, portfolio);
		model.addObject(LANGUAGE, LanguageFactory.INSTANCE.getLanguage(lang));
		model.addObject(APP_TITLE, appProperties.getName() + " &bull; Portfolio");
		model.addObject(CURRENCIES, Currency.values());
		return model;
	}

	private Calendar getCalendarFromDays(final String days) {
		if (days == null) {
			return null;
		}
		final int daysInteger = Integer.parseInt(days);
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -daysInteger);
		return cal;
	}
}
