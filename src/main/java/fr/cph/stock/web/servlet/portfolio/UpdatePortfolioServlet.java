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

import fr.cph.stock.business.CompanyBusiness;
import fr.cph.stock.business.CurrencyBusiness;
import fr.cph.stock.business.UserBusiness;
import fr.cph.stock.entities.Portfolio;
import fr.cph.stock.entities.User;
import fr.cph.stock.exception.NotFoundException;
import fr.cph.stock.exception.YahooException;
import fr.cph.stock.language.LanguageFactory;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static fr.cph.stock.util.Constants.*;

/**
 * This servlet is called when the user want to update the portfolio
 *
 * @author Carl-Philipp Harmant
 */
@SessionAttributes({USER, "id"})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Log4j2
@Controller
public class UpdatePortfolioServlet {

	@NonNull
	private final UserBusiness userBusiness;
	@NonNull
	private final CompanyBusiness companyBusiness;
	@NonNull
	private final CurrencyBusiness currencyBusiness;

	@RequestMapping(value = "/updateportfolio", method = RequestMethod.POST)
	public ModelAndView updatePortfolio(final HttpServletRequest request,
										final HttpServletResponse response,
										@RequestParam(value = CURRENCY_UPDATE, required = false) final String updateCurrencies,
										@ModelAttribute final String id,
										@ModelAttribute final User user,
										@CookieValue(LANGUAGE) final String lang) throws IOException, ServletException {
		final ModelAndView model = new ModelAndView("forward:/" + HOME);
		String yahooError = null;
		String yahooUpdateCompanyError = null;
		try {
			final Portfolio portfolio = userBusiness.getUserPortfolio(user.getId()).orElseThrow(() -> new NotFoundException(user.getId()));
			if (updateCurrencies != null) {
				currencyBusiness.updateOneCurrency(portfolio.getCurrency());
			}
			yahooUpdateCompanyError = companyBusiness.addOrUpdateCompaniesLimitedRequest(portfolio.getCompaniesYahooIdRealTime());
		} catch (YahooException yahooException) {
			log.error(yahooException.getMessage(), yahooException);
			yahooError = yahooException.getMessage();
		}
		if (StringUtils.isNotBlank(yahooError)) {
			model.addObject(UPDATE_STATUS, "<span class='cQuoteDown'>Error !</span>");
		} else if (StringUtils.isNotBlank(yahooUpdateCompanyError)) {
			model.addObject(UPDATE_STATUS,
				"<span class='cQuoteOrange'>"
					+ yahooUpdateCompanyError
					+ "The company does not exist anymore. Please delete it from your portfolio. The other companies has been updated.</span>");
		} else {
			request.setAttribute(UPDATE_STATUS, "<span class='cQuoteUp'>" + LanguageFactory.INSTANCE.getLanguage(lang).get("CONSTANT_UPDATED") + " !</span>");
		}
		return model;
	}
}
