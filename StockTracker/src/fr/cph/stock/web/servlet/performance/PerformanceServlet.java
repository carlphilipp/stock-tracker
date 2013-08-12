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

package fr.cph.stock.web.servlet.performance;

import java.awt.Image;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;

import org.apache.log4j.Logger;

import fr.cph.stock.business.Business;
import fr.cph.stock.business.IBusiness;
import fr.cph.stock.entities.Index;
import fr.cph.stock.entities.Portfolio;
import fr.cph.stock.entities.User;
import fr.cph.stock.entities.chart.PieChart;
import fr.cph.stock.entities.chart.TimeChart;
import fr.cph.stock.exception.LanguageException;
import fr.cph.stock.exception.YahooException;
import fr.cph.stock.language.LanguageFactory;
import fr.cph.stock.report.PdfReport;
import fr.cph.stock.util.Info;
import fr.cph.stock.util.Util;
import fr.cph.stock.web.servlet.CookieManagement;

/**
 * This servlet is called when the user want to access to the performance page
 * 
 * @author Carl-Philipp Harmant
 * 
 */
@WebServlet(name = "PerformanceServlet", urlPatterns = { "/performance" })
public class PerformanceServlet extends HttpServlet {

	/** Serialization **/
	private static final long serialVersionUID = 1L;
	/** Logger **/
	private static final Logger log = Logger.getLogger(PerformanceServlet.class);
	/** Business **/
	private IBusiness business;
	/** Language **/
	private LanguageFactory language;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.GenericServlet#init()
	 */
	@Override
	public void init() throws ServletException {
		business = new Business();
		try {
			language = LanguageFactory.getInstance();
		} catch (LanguageException e) {
			log.error(e.getMessage(), e);
			throw new ServletException("Error: " + e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		User user = (User) session.getAttribute("user");
		Portfolio portfolio = null;
		try {
			String createPdf = request.getParameter("pdf");
			try {
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				String fromString = request.getParameter("from");
				String toString = request.getParameter("to");
				
				Date fromDate = null;
				Date toDate = null;
				if (fromString != null) {
					if (!fromString.equals(""))
						fromDate = formatter.parse(fromString);
				}
				if (toString != null) {
					if (!toString.equals(""))
						toDate = formatter.parse(toString);
				}
				portfolio = business.getUserPortfolio(user.getId(), fromDate, toDate);
				if (portfolio.getShareValues().size() != 0) {
					Date from = portfolio.getShareValues().get(portfolio.getShareValues().size() - 1).getDate();
					// Reset time to 17:00PM to get also the cac40 into the day selected (or it would not select it
					from = Util.resetHourMinSecMill(from);
					// Put 17:00PM to the first sharevalue, to make it nice in graphic
					portfolio.getShareValues().get(portfolio.getShareValues().size() - 1).setDate(from);

					List<Index> indexes = business.getIndexes(Info.YAHOOID_CAC40, from, toDate);
					List<Index> indexes2 = business.getIndexes(Info.YAHOOID_SP500, from, toDate);
					portfolio.addIndexes(indexes);
					portfolio.addIndexes(indexes2);
					portfolio.compute();

					Date _from = from;
					if (indexes.size() > 0) {
						Date derp = indexes.get(0).getDate();
						if (derp.before(_from)) {
							_from = derp;
						}
					}
					if (indexes2.size() > 0) {
						Date date2 = indexes2.get(0).getDate();
						if (date2.before(_from)) {
							_from = date2;
						}
					}

					Date _to = portfolio.getShareValues().get(0).getDate();
					if (indexes.size() > 1) {
						Date date3 = indexes.get(indexes.size() - 1).getDate();
						if (date3.after(_to)) {
							_to = date3;
						}
					}
					if (indexes2.size() > 1) {
						Date date = indexes2.get(indexes2.size() - 1).getDate();
						if (date.after(_to)) {
							_to = date;
						}
					}
					request.setAttribute("_from", _from);
					request.setAttribute("_to", _to);
				}

				request.setAttribute("portfolio", portfolio);
			} catch (YahooException e) {
				log.error("Error: " + e.getMessage(), e);
			}
			if (createPdf != null && createPdf.equals("pdf")) {
				Image sectorChart = PdfReport.createPieChart((PieChart) portfolio.getPieChartSector(), "Sector Chart");
				Image capChart = PdfReport.createPieChart((PieChart) portfolio.getPieChartCap(), "Cap Chart");
				Image timeChart = PdfReport.createTimeChart((TimeChart) portfolio.getTimeChart(), "Share value");
				PdfReport pdf = new PdfReport(Info.REPORT);
				pdf.addParam("portfolio", portfolio);
				pdf.addParam("equities", portfolio.getEquities());
				pdf.addParam("user", user);
				pdf.addParam("sectorPie", sectorChart);
				pdf.addParam("capPie", capChart);
				pdf.addParam("shareValuePie", timeChart);
				response.setContentType("application/pdf");
				DateFormat df = new SimpleDateFormat("dd-MM-yy");
				String formattedDate = df.format(new Date()); 
				response.addHeader("Content-Disposition", "attachment; filename=" + user.getLogin() + formattedDate + ".pdf");
				OutputStream responseOutputStream = response.getOutputStream();
				try {
					JasperExportManager.exportReportToPdfStream(pdf.getReport(), responseOutputStream);
				} catch (JRException e) {
					throw new ServletException("Error: " + e.getMessage(), e);
				}
			} else {
				String lang = CookieManagement.getCookieLanguage(Arrays.asList(request.getCookies()));
				request.setAttribute("language", language.getLanguage(lang));
				request.setAttribute("appTitle", Info.NAME + " &bull;   Performance");
				request.getRequestDispatcher("jsp/performance.jsp").forward(request, response);
			}
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
