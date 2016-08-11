/**
 * Copyright 2013 Carl-Philipp Harmant
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
import fr.cph.stock.business.EquityBusiness;
import fr.cph.stock.business.impl.CompanyBusinessImpl;
import fr.cph.stock.business.impl.EquityBusinessImpl;
import fr.cph.stock.entities.Company;
import fr.cph.stock.entities.Equity;
import fr.cph.stock.entities.User;
import fr.cph.stock.exception.YahooException;
import fr.cph.stock.language.LanguageFactory;
import fr.cph.stock.web.servlet.CookieManagement;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import static fr.cph.stock.util.Constants.*;

/**
 * This servlet is called when the user want to modify an equity
 *
 * @author Carl-Philipp Harmant
 */
@WebServlet(name = "ModifyEquityServlet", urlPatterns = {"/modifyequity"})
public class ModifyEquityServlet extends HttpServlet {

	private static final long serialVersionUID = 886732846315131952L;
	private static final Logger LOG = Logger.getLogger(ModifyEquityServlet.class);
	private CompanyBusiness companyBusiness;
	private EquityBusiness equityBusiness;
	private LanguageFactory language;

	@Override
	public final void init() {
		equityBusiness = EquityBusinessImpl.getInstance();
		companyBusiness = CompanyBusinessImpl.getInstance();
		language = LanguageFactory.getInstance();
	}

	@Override
	protected final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		try {
			if (request.getCharacterEncoding() == null) {
				request.setCharacterEncoding("UTF-8");
			}
			final HttpSession session = request.getSession();
			final User user = (User) session.getAttribute(USER);
			final String lang = CookieManagement.getCookieLanguage(Arrays.asList(request.getCookies()));
			try {
				if (request.getParameter(DELETE) != null) {
					delete(request);
					request.setAttribute(MODIFIED, language.getLanguage(lang).get(CONSTANT_DELETED) + " !");
				} else {
					modify(request, user, lang);
				}
			} catch (final NumberFormatException | YahooException e) {
				LOG.warn(e.getMessage(), e);
				request.setAttribute("modifyError", "Error: " + e.getMessage());
			}
			request.getRequestDispatcher(HOME).forward(request, response);
		} catch (final Throwable t) {
			LOG.error(t.getMessage(), t);
			throw new ServletException("Error: " + t.getMessage(), t);
		}
	}

	@Override
	protected final void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		doGet(request, response);
	}

	private void delete(final HttpServletRequest request) {
		final Equity equity = new Equity();
		if (request.getParameter(MANUAL) != null) {
			final String id = request.getParameter(ID);
			final String companyId = request.getParameter(COMPANY_ID);
			equity.setid(Integer.parseInt(id));
			equityBusiness.deleteEquity(equity);
			final Company company = new Company();
			company.setId(Integer.parseInt(companyId));
			companyBusiness.deleteCompany(company);
		} else {
			final String id = request.getParameter(ID);
			equity.setid(Integer.parseInt(id));
			equityBusiness.deleteEquity(equity);
		}
	}

	private void modify(final HttpServletRequest request, final User user, final String lang) throws UnsupportedEncodingException, YahooException {
		final String quantityParam = request.getParameter(QUANTITY);
		final Double quantity = StringUtils.isNotEmpty(quantityParam) ? Double.parseDouble(quantityParam) : 0.0;
		if (quantity == 0) {
			request.setAttribute(MODIFY_ERROR, "Error: quantity can not be 0");
		} else {
			updateEquity(request, user.getId(), quantity);
			request.setAttribute(MODIFIED, language.getLanguage(lang).get(CONSTANT_MODIFIED) + " !");
			if (request.getParameter(MANUAL) != null) {
				updateManuel(request);
			}
		}
	}

	private void updateEquity(final HttpServletRequest request, final int userId, final Double quantity) throws UnsupportedEncodingException, YahooException {
		final String ticker = request.getParameter(TICKER);
		final String nameParam = request.getParameter(NAME_PERSONAL);
		final String sectorParam = request.getParameter(SECTOR_PERSONAL);
		final String industryParam = request.getParameter(INDUSTRY_PERSONAL);
		final String marketCapParam = request.getParameter(MARKET_CAP_PERSONAL);
		final String unitCostPriceParam = request.getParameter(UNIT_COST_PRICE);
		final String stopLossParam = request.getParameter(STOP_LOSS);
		final String objectiveParam = request.getParameter(OBJECTIVE);
		final String yieldParam = request.getParameter(YIELD_PERSONAL);
		final String parityParam = request.getParameter(MODIFY_PARITY_PERSONAL);

		final String namePersonal = StringUtils.isNotEmpty(nameParam) ? nameParam : null;
		final String sectorPersonal = StringUtils.isNotEmpty(sectorParam) ? sectorParam : null;
		final String industryPersonal = StringUtils.isNotEmpty(industryParam) ? industryParam : null;
		final String marketCapPersonal = StringUtils.isNotEmpty(marketCapParam) ? marketCapParam : null;
		final Double unitCostPrice = StringUtils.isNotEmpty(unitCostPriceParam) ? Double.parseDouble(unitCostPriceParam) : 0.0;
		final Double stopLoss = StringUtils.isNotEmpty(stopLossParam) ? NumberUtils.createDouble(stopLossParam) : null;
		final Double objective = StringUtils.isNotEmpty(objectiveParam) ? NumberUtils.createDouble(objectiveParam) : null;
		final Double yieldPersonal = StringUtils.isNotEmpty(yieldParam) ? NumberUtils.createDouble(yieldParam) : null;
		final Double parityPersonal = StringUtils.isNotEmpty(parityParam) ? NumberUtils.createDouble(parityParam) : null;

		final Equity equity = new Equity();
		equity.setNamePersonal(namePersonal);
		equity.setSectorPersonal(sectorPersonal);
		equity.setIndustryPersonal(industryPersonal);
		equity.setMarketCapPersonal(marketCapPersonal);
		equity.setQuantity(quantity);
		equity.setUnitCostPrice(unitCostPrice);
		equity.setStopLossLocal(stopLoss);
		equity.setObjectivLocal(objective);
		equity.setYieldPersonal(yieldPersonal);
		equity.setParityPersonal(parityPersonal);
		equityBusiness.updateEquity(userId, ticker, equity);
	}

	private void updateManuel(final HttpServletRequest request) {
		final String companyId = request.getParameter(COMPANY_ID);
		final String quote = request.getParameter(QUOTE);
		final Double quoteRes;
		final Integer companyIdRes;
		if (quote != null && !quote.equals("") && companyId != null && !companyId.equals("")) {
			quoteRes = Double.parseDouble(quote);
			companyIdRes = Integer.parseInt(companyId);
			companyBusiness.updateCompanyManual(companyIdRes, quoteRes);
		}
	}
}
