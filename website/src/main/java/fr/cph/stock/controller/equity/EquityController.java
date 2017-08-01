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

package fr.cph.stock.controller.equity;

import fr.cph.stock.service.CompanyService;
import fr.cph.stock.service.EquityService;
import fr.cph.stock.entities.Company;
import fr.cph.stock.entities.Equity;
import fr.cph.stock.entities.User;
import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.exception.EquityException;
import fr.cph.stock.exception.NotFoundException;
import fr.cph.stock.exception.YahooException;
import fr.cph.stock.language.LanguageFactory;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

import static fr.cph.stock.util.Constants.*;

/**
 * This servlet is called when the user want to update the portfolio
 *
 * @author Carl-Philipp Harmant
 */
@SessionAttributes(USER)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Log4j2
@Controller
public class EquityController {

	@NonNull
	private final CompanyService companyService;
	@NonNull
	private final EquityService equityService;

	@RequestMapping(value = "/equity", method = RequestMethod.POST)
	public ModelAndView addEquity(@RequestParam(value = TICKER) final String ticker,
								  @RequestParam(value = UNIT_COST_PRICE) final Double unitCostPrice,
								  @RequestParam(value = QUANTITY) final Double quantity,
								  @RequestParam(value = PARITY_PERSONAL, required = false) final Double parityPersonal,
								  @Valid @ModelAttribute final User user,
								  @CookieValue(LANGUAGE) final String lang) {
		final ModelAndView model = homeModelView();
		final Equity equity = Equity.builder()
			.quantity(quantity)
			.unitCostPrice(unitCostPrice)
			.parityPersonal(parityPersonal)
			.build();
		try {
			equityService.createEquity(user.getId(), ticker, equity);
			model.addObject("added", LanguageFactory.INSTANCE.getLanguage(lang).get(CONSTANT_ADDED) + " !");
		} catch (final YahooException | EquityException e) {
			model.addObject("addError", e.getMessage());
		}
		return model;
	}

	@RequestMapping(value = "/updateEquity", method = RequestMethod.POST)
	public ModelAndView updateEquity(@RequestParam(value = TICKER) final String ticker,
									 @RequestParam(value = NAME_PERSONAL, required = false) final String namePersonal,
									 @RequestParam(value = SECTOR_PERSONAL, required = false) final String sectorPersonal,
									 @RequestParam(value = INDUSTRY_PERSONAL, required = false) final String industryPersonal,
									 @RequestParam(value = MARKET_CAP_PERSONAL, required = false) final String marketCapPersonal,
									 @RequestParam(value = UNIT_COST_PRICE) final Double unitCostPrice,
									 @RequestParam(value = QUANTITY) final Double quantity,
									 @RequestParam(value = STOP_LOSS, required = false) final Double stopLoss,
									 @RequestParam(value = OBJECTIVE, required = false) final Double objective,
									 @RequestParam(value = YIELD_PERSONAL, required = false) final Double yieldPersonal,
									 @RequestParam(value = MODIFY_PARITY_PERSONAL, required = false) final Double parityPersonal,
									 @Valid @ModelAttribute final User user,
									 @CookieValue(LANGUAGE) final String lang) {
		final Equity equity = Equity.builder()
			.namePersonal(namePersonal)
			.sectorPersonal(sectorPersonal)
			.industryPersonal(industryPersonal)
			.marketCapPersonal(marketCapPersonal)
			.quantity(quantity)
			.unitCostPrice(unitCostPrice)
			.stopLossLocal(stopLoss)
			.objectivLocal(objective)
			.yieldPersonal(yieldPersonal)
			.parityPersonal(parityPersonal)
			.build();
		return updateEquity(equity, user, ticker, lang);
	}

	private ModelAndView updateEquity(final Equity equity, final User user, final String ticker, final String lang) {
		final ModelAndView model = homeModelView();
		if (equity.getQuantity() <= 0) {
			model.addObject(MODIFY_ERROR, "Error: quantity can not be 0 or lower");
		} else {
			equityService.updateEquity(user.getId(), ticker, equity);
			model.addObject(MODIFIED, LanguageFactory.INSTANCE.getLanguage(lang).get(CONSTANT_DELETED) + " !");
		}
		return model;
	}

	@RequestMapping(value = "/deleteEquity", method = RequestMethod.POST)
	public ModelAndView deleteEquity(@RequestParam(value = "equityId") final int id,
									 @Valid @ModelAttribute final User user,
									 @CookieValue(LANGUAGE) final String lang) {
		final ModelAndView model = homeModelView();
		equityService.deleteEquity(Equity.builder().id(id).build());
		model.addObject(MODIFIED, LanguageFactory.INSTANCE.getLanguage(lang).get(CONSTANT_DELETED) + " !");
		return model;
	}

	@RequestMapping(value = "/manualEquity", method = RequestMethod.POST)
	public ModelAndView addManualEquity(@RequestParam(value = MANUAL_NAME) final String manualName,
										@RequestParam(value = MANUAL_CURRENCY) final String manualCurrency,
										@RequestParam(value = MANUAL_INDUSTRY) final String manualIndustry,
										@RequestParam(value = MANUAL_SECTOR) final String manualSector,
										@RequestParam(value = MANUAL_QUOTE) final Double manualQuote,
										@RequestParam(value = MANUAL_UNIT_COST_PRICE) final Double unitCostPrice,
										@RequestParam(value = MANUAL_QUANTITY) final Double quantity,
										@RequestParam(value = MANUAL_PARITY_PERSONAL, required = false) final Double parityPersonal,
										@Valid @ModelAttribute final User user,
										@CookieValue(LANGUAGE) final String lang) {
		final ModelAndView model = homeModelView();
		final Company company = companyService.createManualCompany(manualName, manualIndustry, manualSector, Currency.getEnum(manualCurrency), manualQuote)
			.orElseThrow(() -> new NotFoundException(manualName));
		final Equity equity = Equity.builder()
			.quantity(quantity)
			.unitCostPrice(unitCostPrice)
			.parityPersonal(parityPersonal)
			.build();
		try {
			equityService.createManualEquity(user.getId(), company, equity);
			model.addObject("added", LanguageFactory.INSTANCE.getLanguage(lang).get(CONSTANT_ADDED) + " !");
		} catch (final EquityException e) {
			model.addObject("addError", e.getMessage());
		}
		return model;
	}

	@RequestMapping(value = "/updateManualEquity", method = RequestMethod.POST)
	public ModelAndView updateManualEquity(@RequestParam(value = TICKER) final String ticker,
										   @RequestParam(value = NAME_PERSONAL, required = false) final String namePersonal,
										   @RequestParam(value = SECTOR_PERSONAL, required = false) final String sectorPersonal,
										   @RequestParam(value = INDUSTRY_PERSONAL, required = false) final String industryPersonal,
										   @RequestParam(value = MARKET_CAP_PERSONAL, required = false) final String marketCapPersonal,
										   @RequestParam(value = UNIT_COST_PRICE) final Double unitCostPrice,
										   @RequestParam(value = QUANTITY) final Double quantity,
										   @RequestParam(value = STOP_LOSS, required = false) final Double stopLoss,
										   @RequestParam(value = OBJECTIVE, required = false) final Double objective,
										   @RequestParam(value = YIELD_PERSONAL, required = false) final Double yieldPersonal,
										   @RequestParam(value = MODIFY_PARITY_PERSONAL, required = false) final Double parityPersonal,
										   @RequestParam(value = COMPANY_ID) final int companyId,
										   @RequestParam(value = QUOTE) final Double quote,
										   @Valid @ModelAttribute final User user,
										   @CookieValue(LANGUAGE) final String lang) {
		final Equity equity = Equity.builder()
			.namePersonal(namePersonal)
			.sectorPersonal(sectorPersonal)
			.industryPersonal(industryPersonal)
			.marketCapPersonal(marketCapPersonal)
			.quantity(quantity)
			.unitCostPrice(unitCostPrice)
			.stopLossLocal(stopLoss)
			.objectivLocal(objective)
			.yieldPersonal(yieldPersonal)
			.parityPersonal(parityPersonal)
			.build();
		companyService.updateCompanyManual(companyId, quote);
		return updateEquity(equity, user, ticker, lang);
	}

	@RequestMapping(value = "/deleteManualEquity", method = RequestMethod.POST)
	public ModelAndView deleteManualEquity(@RequestParam(value = "equityId") final int equityId,
										   @RequestParam(value = COMPANY_ID) final int companyId,
										   @Valid @ModelAttribute final User user,
										   @CookieValue(LANGUAGE) final String lang) {
		final ModelAndView model = homeModelView();
		final Equity equity = Equity.builder().id(equityId).build();
		equityService.deleteEquity(equity);
		final Company company = Company.builder().id(companyId).build();
		companyService.deleteCompany(company);
		model.addObject(MODIFIED, LanguageFactory.INSTANCE.getLanguage(lang).get(CONSTANT_DELETED) + " !");
		return model;
	}

	private ModelAndView homeModelView() {
		return new ModelAndView("forward:/" + HOME);
	}
}
