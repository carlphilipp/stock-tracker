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

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import fr.cph.stock.business.Business;
import fr.cph.stock.business.IBusiness;
import fr.cph.stock.entities.Equity;
import fr.cph.stock.entities.User;
import fr.cph.stock.exception.YahooException;
import fr.cph.stock.language.LanguageFactory;
import fr.cph.stock.web.servlet.CookieManagement;

@WebServlet(name = "ModifyEquityServlet", urlPatterns = { "/modifyequity" })
public class ModifyEquityServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger log = Logger.getLogger(ModifyEquityServlet.class);
	private IBusiness business;

	public ModifyEquityServlet() {
		super();
	}

	public void init() {
		business = new Business();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		try {
			if (request.getCharacterEncoding() == null) {
				request.setCharacterEncoding("UTF-8");
			}
			String lang = CookieManagement.getCookieLanguage(Arrays.asList(request.getCookies()));
			LanguageFactory language = LanguageFactory.getInstance();
			HttpSession session = request.getSession();
			User user = (User) session.getAttribute("user");
			Double quantity = null, unitCostPrice = null, stopLoss = null, objective = null, yieldPersonal = null, parityPersonal = null;
			String namePersonal = null, sectorPersonal = null, industryPersonal = null, marketCapPersonal = null;
			try {
				if (request.getParameter("delete") != null) {
					String id = request.getParameter("id");
					Equity e = new Equity();
					e.setid(Integer.parseInt(id));
					business.deleteEquity(e);
					request.setAttribute("modified", language.getLanguage(lang).get("CONSTANT_DELETED") + " !");
				} else {
					String ticker = request.getParameter("ticker");
					String _namePersonal = request.getParameter("namePersonal");
					if (!_namePersonal.equals("")) {
						namePersonal = _namePersonal;
					}
					String _sectorPersonal = request.getParameter("sectorPersonal");
					if (!_sectorPersonal.equals("")) {
						sectorPersonal = _sectorPersonal;
					}
					String _industryPersonal = request.getParameter("industryPersonal");
					if (!_industryPersonal.equals("")) {
						industryPersonal = _industryPersonal;
					}
					String _marketCapPersonal = request.getParameter("marketCapPersonal");
					if (!_marketCapPersonal.equals("")) {
						marketCapPersonal = _marketCapPersonal;
					}
					String _quantity = request.getParameter("quantity");
					if (!_quantity.equals(""))
						quantity = NumberUtils.createDouble(_quantity);
					String _unitCostPrice = request.getParameter("unitCostPrice");
					if (!_unitCostPrice.equals(""))
						unitCostPrice = NumberUtils.createDouble(_unitCostPrice);
					String _stopLoss = request.getParameter("stopLoss");
					if (!_stopLoss.equals(""))
						stopLoss = NumberUtils.createDouble(_stopLoss);
					String _objective = request.getParameter("objective");
					if (!_objective.equals(""))
						objective = NumberUtils.createDouble(_objective);
					String _yieldPersonal = request.getParameter("yieldPersonal");
					if (!_yieldPersonal.equals(""))
						yieldPersonal = NumberUtils.createDouble(_yieldPersonal);
					String _parityPersonal = request.getParameter("modifyParityPersonal");
					if (!_parityPersonal.equals(""))
						parityPersonal = NumberUtils.createDouble(_parityPersonal);

					if (quantity == 0 || unitCostPrice == 0) {
						request.setAttribute("modifyError", "Error: quantity and/or unit cost price can not be 0");
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
						request.setAttribute("modified", language.getLanguage(lang).get("CONSTANT_MODIFIED") + " !");
					}
				}
			} catch (YahooException e) {
				log.warn(e.getMessage(), e);
				request.setAttribute("modifyError", "Error: " + e.getMessage());
			} catch (NumberFormatException e) {
				log.warn(e.getMessage(), e);
				request.setAttribute("modifyError", "Error: " + e.getMessage());
			}
			request.getRequestDispatcher("home").forward(request, response);
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new ServletException("Error: " + t.getMessage(), t);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
