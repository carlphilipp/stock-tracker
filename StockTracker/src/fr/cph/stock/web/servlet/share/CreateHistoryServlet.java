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

package fr.cph.stock.web.servlet.share;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.apache.log4j.Logger;

import fr.cph.stock.business.Business;
import fr.cph.stock.business.IBusiness;
import fr.cph.stock.csv.Csv;
import fr.cph.stock.entities.Account;
import fr.cph.stock.entities.Portfolio;
import fr.cph.stock.entities.ShareValue;
import fr.cph.stock.entities.User;

@WebServlet(name = "CreateHistoryServlet", urlPatterns = { "/createhistory" })
@MultipartConfig
public class CreateHistoryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger log = Logger.getLogger(CreateHistoryServlet.class);
	private IBusiness business;

	public void init() {
		business = new Business();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		try {
			HttpSession session = request.getSession(false);
			User user = (User) session.getAttribute("user");
			String liquidity = request.getParameter("liquidity");
			String _account = request.getParameter("account");

			Portfolio portfolio = business.getUserPortfolio(user.getId(),  null, null);
			Account account = portfolio.getAccount(_account);

			Part p1 = request.getPart("file");
			InputStream is = p1.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			Csv csv = new Csv(br, user, _account);
			List<ShareValue> shareValues = csv.getShareValueList();
			for (ShareValue sv : shareValues) {
				business.addShareValue(sv);
			}
			if (!liquidity.equals("")) {
				business.updateLiquidity(account, Double.parseDouble(liquidity));
			}
			request.getRequestDispatcher("sharevalue?page=1").forward(request, response);
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new ServletException("Error: " + t.getMessage(), t);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
