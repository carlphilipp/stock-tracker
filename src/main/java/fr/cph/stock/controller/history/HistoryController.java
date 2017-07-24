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

package fr.cph.stock.controller.history;

import fr.cph.stock.business.UserBusiness;
import fr.cph.stock.entities.Portfolio;
import fr.cph.stock.entities.User;
import fr.cph.stock.exception.NotFoundException;
import fr.cph.stock.exception.YahooException;
import fr.cph.stock.language.LanguageFactory;
import fr.cph.stock.util.Info;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static fr.cph.stock.util.Constants.*;

/**
 * This servlet is called when the user want to access to the history page
 *
 * @author Carl-Philipp Harmant
 */
@SessionAttributes(USER)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Log4j2
@Controller
public class HistoryController {

	private static final int ITEM_MAX = 20;

	@NonNull
	private UserBusiness userBusiness;

	@RequestMapping(value = "/history", method = RequestMethod.GET)
	protected ModelAndView history(@RequestParam(value = PAGE, defaultValue = "1") final int pageNumber,
								   @ModelAttribute final User user,
								   @CookieValue(LANGUAGE) final String lang,
								   final HttpServletRequest request,
								   final HttpServletResponse response) throws ServletException {
		final ModelAndView model = new ModelAndView("sharevalue");
		try {
			final Portfolio portfolio = userBusiness.getUserPortfolio(user.getId()).orElseThrow(() -> new NotFoundException(user.getId()));
			if (portfolio.getShareValues().size() != 0) {
				int begin = pageNumber * ITEM_MAX - ITEM_MAX;
				int end = pageNumber * ITEM_MAX - 1;
				int nbPage = portfolio.getShareValues().size() / ITEM_MAX + 1;
				if (pageNumber == 0) {
					begin = 0;
					end = portfolio.getShareValues().size() - 1;
				}
				if (pageNumber == nbPage) {
					end = portfolio.getShareValues().size() - 1;
				}
				model.addObject(BEGIN, begin);
				model.addObject(END, end);
				model.addObject(PAGE, pageNumber);
				model.addObject(NB_PAGE, nbPage);
			}
			model.addObject(PORTFOLIO, portfolio);
		} catch (final YahooException e) {
			log.error(e.getMessage(), e);
			throw new ServletException("Error: " + e.getMessage(), e);
		}
		model.addObject(LANGUAGE, LanguageFactory.INSTANCE.getLanguage(lang));
		model.addObject(APP_TITLE, Info.NAME + " &bull; History");
		return model;
	}
}
