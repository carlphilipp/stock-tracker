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
 * See the License for the specific languageFactory governing permissions and
 * limitations under the License.
 */

package fr.cph.stock.web.servlet.portfolio;

import fr.cph.stock.business.CompanyBusiness;
import fr.cph.stock.business.EquityBusiness;
import fr.cph.stock.entities.Company;
import fr.cph.stock.entities.Equity;
import fr.cph.stock.entities.User;
import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.exception.EquityException;
import fr.cph.stock.exception.NotFoundException;
import fr.cph.stock.exception.YahooException;
import fr.cph.stock.guice.GuiceInjector;
import fr.cph.stock.language.LanguageFactory;
import fr.cph.stock.web.servlet.CookieManagement;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.math.NumberUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;

import static fr.cph.stock.util.Constants.*;

/**
 * This servlet is called when the user want to add an equity
 *
 * @author Carl-Philipp Harmant
 */
@Log4j2
@WebServlet(name = "AddEquityServlet", urlPatterns = {"/add"})
public class AddEquityServlet extends HttpServlet {

	private static final long serialVersionUID = -4917456731220463031L;

	private CompanyBusiness companyBusiness;
	private EquityBusiness equityBusiness;
	private LanguageFactory languageFactory;

	@Override
	public final void init() {
		equityBusiness = GuiceInjector.INSTANCE.getEquityBusiness();
		companyBusiness = GuiceInjector.INSTANCE.getCompanyBusiness();
		languageFactory = LanguageFactory.INSTANCE;
	}

	@Override
	protected final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		try {
			final String lang = CookieManagement.getCookieLanguage(Arrays.asList(request.getCookies()));
			final HttpSession session = request.getSession();
			final User user = (User) session.getAttribute(USER);
			final String manual = request.getParameter(MANUAL);
			if (manual != null && manual.equals("true")) {
				addManual(request, user.getId(), lang);
			} else {
				add(request, user.getId(), lang);
			}
			request.getRequestDispatcher(HOME).forward(request, response);
		} catch (final Throwable t) {
			log.error(t.getMessage(), t);
			throw new ServletException("Error: " + t.getMessage(), t);
		}
	}

	@Override
	protected final void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		doGet(request, response);
	}

	private void addManual(final HttpServletRequest request, final int id, final String lang) {
		final String manualName = request.getParameter(MANUAL_NAME);
		final String manualUnitCostPrice = request.getParameter(MANUAL_UNIT_COST_PRICE);
		final String manualQuantity = request.getParameter(MANUAL_QUANTITY);
		final String manualParityPersonal = request.getParameter(MANUAL_PARITY_PERSONAL);
		final String manualCurrency = request.getParameter(MANUAL_CURRENCY);
		final String manualIndustry = request.getParameter(MANUAL_INDUSTRY);
		final String manualSector = request.getParameter(MANUAL_SECTOR);
		final String manualQuote = request.getParameter(MANUAL_QUOTE);

		final Double quantity = NumberUtils.createDouble(manualQuantity);
		final Double unitCostPrice = NumberUtils.createDouble(manualUnitCostPrice);
		Double parityPersonal = null;
		if (!manualParityPersonal.equals("")) {
			parityPersonal = NumberUtils.createDouble(manualParityPersonal);
		}
		final Double quote = NumberUtils.createDouble(manualQuote);
		final Company company = companyBusiness.createManualCompany(manualName, manualIndustry, manualSector, Currency.getEnum(manualCurrency), quote)
			.orElseThrow(() -> new NotFoundException(manualName));

		final Equity equity = Equity.builder()
			.quantity(quantity)
			.unitCostPrice(quantity)
			.parityPersonal(parityPersonal)
			.build();
		try {
			equityBusiness.createManualEquity(id, company, equity);
			request.setAttribute("added", languageFactory.getLanguage(lang).get(CONSTANT_ADDED) + " !");
		} catch (final EquityException e) {
			request.setAttribute("addError", e.getMessage());
		}
	}

	private void add(final HttpServletRequest request, final int id, final String lang) {
		final String ticker = request.getParameter(TICKER).toUpperCase();
		final String unitCostP = request.getParameter(UNIT_COST_PRICE);
		final String quant = request.getParameter(QUANTITY);
		final String parityPerso = request.getParameter(PARITY_PERSONAL);

		final Double quantity = NumberUtils.createDouble(quant);
		final Double unitCostPrice = NumberUtils.createDouble(unitCostP);
		Double parityPersonal = null;
		if (!parityPerso.equals("")) {
			parityPersonal = NumberUtils.createDouble(parityPerso);
		}

		final Equity equity = Equity.builder()
			.quantity(quantity)
			.unitCostPrice(unitCostPrice)
			.parityPersonal(parityPersonal)
			.build();
		try {
			equityBusiness.createEquity(id, ticker, equity);
			request.setAttribute("added", languageFactory.getLanguage(lang).get(CONSTANT_ADDED) + " !");
		} catch (final YahooException | EquityException e) {
			request.setAttribute("addError", e.getMessage());
		}
	}
}
