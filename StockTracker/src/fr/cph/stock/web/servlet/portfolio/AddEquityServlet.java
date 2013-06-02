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
import fr.cph.stock.exception.EquityException;
import fr.cph.stock.exception.YahooException;
import fr.cph.stock.language.LanguageFactory;
import fr.cph.stock.web.servlet.CookieManagement;

/**
 * This servlet is called when the user want to add an equity
 * 
 * @author Carl-Philipp Harmant
 * 
 */
@WebServlet(name = "AddEquityServlet", urlPatterns = { "/add" })
public class AddEquityServlet extends HttpServlet {

	/** Serialization **/
	private static final long serialVersionUID = 1L;
	/** Logger **/
	private static final Logger log = Logger.getLogger(AddEquityServlet.class);
	/** Business **/
	private IBusiness business;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.GenericServlet#init()
	 */
	@Override
	public void init() {
		business = new Business();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		try {
			String lang = CookieManagement.getCookieLanguage(Arrays.asList(request.getCookies()));
			LanguageFactory language = LanguageFactory.getInstance();
			HttpSession session = request.getSession();
			User user = (User) session.getAttribute("user");
			String ticker = request.getParameter("ticker").toUpperCase();
			String _unitCostPrice = request.getParameter("unitCostPrice");
			String _quantity = request.getParameter("quantity");
			String _parityPersonal = request.getParameter("parityPersonal");

			Double quantity = NumberUtils.createDouble(_quantity);
			Double unitCostPrice = NumberUtils.createDouble(_unitCostPrice);
			Double parityPersonal = null;
			if (!_parityPersonal.equals("")) {
				parityPersonal = NumberUtils.createDouble(_parityPersonal);
			}

			Equity equity = new Equity();
			equity.setQuantity(quantity);
			equity.setUnitCostPrice(unitCostPrice);
			equity.setParityPersonal(parityPersonal);
			try {
				business.createEquity(user.getId(), ticker, equity);
				request.setAttribute("added", language.getLanguage(lang).get("CONSTANT_ADDED") + " !");
			} catch (YahooException | EquityException e) {
				request.setAttribute("addError", e.getMessage());
			}
			request.getRequestDispatcher("home").forward(request, response);
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new ServletException("Error: " + t.getMessage(), t);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
