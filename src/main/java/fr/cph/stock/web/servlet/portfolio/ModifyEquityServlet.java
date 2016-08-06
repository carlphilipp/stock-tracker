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
 * This servlet is called when the user want to modify an equity
 *
 * @author Carl-Philipp Harmant
 *
 */
@WebServlet(name = "ModifyEquityServlet", urlPatterns = { "/modifyequity" })
public class ModifyEquityServlet extends HttpServlet {

	/** Serialization **/
	private static final long serialVersionUID = 886732846315131952L;
	/** Logger **/
	private static final Logger LOG = Logger.getLogger(ModifyEquityServlet.class);
	/** Business **/
	private IBusiness business;

	@Override
	public final void init() {
		business = Business.getInstance();
	}

	@Override
	protected final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		try {
			if (request.getCharacterEncoding() == null) {
				request.setCharacterEncoding("UTF-8");
			}
			String lang = CookieManagement.getCookieLanguage(Arrays.asList(request.getCookies()));
			LanguageFactory language = LanguageFactory.getInstance();
			HttpSession session = request.getSession();
			User user = (User) session.getAttribute(USER);
			Double quantity = null, unitCostPrice = null, stopLoss = null, objective = null, yieldPersonal = null, parityPersonal = null;
			String namePersonal = null, sectorPersonal = null, industryPersonal = null, marketCapPersonal = null;
			try {
				if (request.getParameter(DELETE) != null) {
					if (request.getParameter(MANUAL) != null) {
						String id = request.getParameter(ID);
						String companyId = request.getParameter(COMPANY_ID);
						Equity e = new Equity();
						e.setid(Integer.parseInt(id));
						business.deleteEquity(e);
						Company company = new Company();
						company.setId(Integer.parseInt(companyId));
						business.deleteCompany(company);
						request.setAttribute(MODIFIED, language.getLanguage(lang).get(CONSTANT_DELETED) + " !");
					} else {
						String id = request.getParameter(ID);
						Equity e = new Equity();
						e.setid(Integer.parseInt(id));
						business.deleteEquity(e);
						request.setAttribute(MODIFIED, language.getLanguage(lang).get(CONSTANT_DELETED) + " !");
					}
				} else {
					String ticker = request.getParameter(TICKER);
					String namePerso = request.getParameter(NAME_PERSONAL);
					if (!namePerso.equals("")) {
						namePersonal = namePerso;
					}
					String sectorPerso = request.getParameter(SECTOR_PERSONAL);
					if (!sectorPerso.equals("")) {
						sectorPersonal = sectorPerso;
					}
					String industryPerso = request.getParameter(INDUSTRY_PERSONAL);
					if (!industryPerso.equals("")) {
						industryPersonal = industryPerso;
					}
					String marketCapPerso = request.getParameter(MARKET_CAP_PERSONAL);
					if (!marketCapPerso.equals("")) {
						marketCapPersonal = marketCapPerso;
					}
					String quant = request.getParameter(QUANTITY);
					if (!quant.equals("")) {
						quantity = NumberUtils.createDouble(quant);
					} else {
						quantity = 0.0;
					}
					String unitCostP = request.getParameter(UNIT_COST_PRICE);
					if (!unitCostP.equals("")) {
						unitCostPrice = NumberUtils.createDouble(unitCostP);
					} else {
						unitCostPrice = 0.0;
					}
					String stopLo = request.getParameter(STOP_LOSS);
					if (!stopLo.equals("")) {
						stopLoss = NumberUtils.createDouble(stopLo);
					}
					String objc = request.getParameter(OBJECTIVE);
					if (!objc.equals("")) {
						objective = NumberUtils.createDouble(objc);
					}
					String yieldPerso = request.getParameter(YIELD_PERSONAL);
					if (!yieldPerso.equals("")) {
						yieldPersonal = NumberUtils.createDouble(yieldPerso);
					}
					String parityPerso = request.getParameter(MODIFY_PARITY_PERSONAL);
					if (!parityPerso.equals("")) {
						parityPersonal = NumberUtils.createDouble(parityPerso);
					}
					if (quantity == 0) {
						request.setAttribute(MODIFY_ERROR, "Error: quantity can not be 0");
					} else {
						Equity equity = new Equity();
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
						business.updateEquity(user.getId(), ticker, equity);
						request.setAttribute(MODIFIED, language.getLanguage(lang).get(CONSTANT_MODIFIED) + " !");
					}
					if(request.getParameter(MANUAL) != null){
						String companyId = request.getParameter(COMPANY_ID);
						String quote = request.getParameter(QUOTE);
						Double quoteRes = null;
						Integer companyIdRes = null;
						if(quote != null && !quote.equals("") && companyId != null && !companyId.equals("")){
							quoteRes = Double.parseDouble(quote);
							companyIdRes = Integer.parseInt(companyId);
							business.updateCompanyManual(companyIdRes, quoteRes);
						}
					}
				}
			} catch (YahooException e) {
				LOG.warn(e.getMessage(), e);
				request.setAttribute("modifyError", "Error: " + e.getMessage());
			} catch (NumberFormatException e) {
				LOG.warn(e.getMessage(), e);
				request.setAttribute("modifyError", "Error: " + e.getMessage());
			}
			request.getRequestDispatcher("home").forward(request, response);
		} catch (Throwable t) {
			LOG.error(t.getMessage(), t);
			throw new ServletException("Error: " + t.getMessage(), t);
		}
	}

	@Override
	protected final void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		doGet(request, response);
	}
}
