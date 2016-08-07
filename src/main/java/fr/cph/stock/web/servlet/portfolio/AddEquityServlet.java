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

import fr.cph.stock.business.Business;
import fr.cph.stock.business.IBusiness;
import fr.cph.stock.entities.Company;
import fr.cph.stock.entities.Equity;
import fr.cph.stock.entities.User;
import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.exception.EquityException;
import fr.cph.stock.exception.YahooException;
import fr.cph.stock.language.LanguageFactory;
import fr.cph.stock.web.servlet.CookieManagement;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

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
 *
 */
@WebServlet(name = "AddEquityServlet", urlPatterns = { "/add" })
public class AddEquityServlet extends HttpServlet {

	private static final long serialVersionUID = -4917456731220463031L;
	private static final Logger LOG = Logger.getLogger(AddEquityServlet.class);
	private IBusiness business;

	@Override
	public final void init() {
		this.business = Business.getInstance();
	}

	@Override
	protected final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		try {
			final String lang = CookieManagement.getCookieLanguage(Arrays.asList(request.getCookies()));
			final LanguageFactory language = LanguageFactory.getInstance();
			final HttpSession session = request.getSession();
			final User user = (User) session.getAttribute(USER);
			final String manual = request.getParameter(MANUAL);
			if (manual != null && manual.equals("true")) {
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
				final Company company = business.createManualCompany(manualName, manualIndustry, manualSector, Currency.getEnum(manualCurrency), quote);

				final Equity equity = new Equity();
				equity.setQuantity(quantity);
				equity.setUnitCostPrice(unitCostPrice);
				equity.setParityPersonal(parityPersonal);
				try {
					business.createManualEquity(user.getId(), company, equity);
					request.setAttribute("added", language.getLanguage(lang).get("CONSTANT_ADDED") + " !");
				} catch (final EquityException e) {
					request.setAttribute("addError", e.getMessage());
				}
			} else {
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

				final Equity equity = new Equity();
				equity.setQuantity(quantity);
				equity.setUnitCostPrice(unitCostPrice);
				equity.setParityPersonal(parityPersonal);
				try {
					business.createEquity(user.getId(), ticker, equity);
					request.setAttribute("added", language.getLanguage(lang).get(CONSTANT_ADDED) + " !");
				} catch (final YahooException | EquityException e) {
					request.setAttribute("addError", e.getMessage());
				}
			}

			request.getRequestDispatcher("home").forward(request, response);
		} catch (final Throwable t) {
			LOG.error(t.getMessage(), t);
			throw new ServletException("Error: " + t.getMessage(), t);
		}
	}

	@Override
	protected final void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		doGet(request, response);
	}
}
