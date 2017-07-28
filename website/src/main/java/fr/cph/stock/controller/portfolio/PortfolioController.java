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

package fr.cph.stock.controller.portfolio;

import fr.cph.stock.config.AppProperties;
import fr.cph.stock.entities.Index;
import fr.cph.stock.entities.Portfolio;
import fr.cph.stock.entities.User;
import fr.cph.stock.entities.chart.PieChart;
import fr.cph.stock.entities.chart.TimeChart;
import fr.cph.stock.exception.NotFoundException;
import fr.cph.stock.exception.YahooException;
import fr.cph.stock.language.LanguageFactory;
import fr.cph.stock.report.PdfReport;
import fr.cph.stock.service.CompanyService;
import fr.cph.stock.service.CurrencyService;
import fr.cph.stock.service.IndexService;
import fr.cph.stock.service.UserService;
import fr.cph.stock.util.Constants;
import fr.cph.stock.util.Util;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
public class PortfolioController {

	private static final String DATE_FORMAT = "dd/MM/yyyy";

	@NonNull
	private AppProperties appProperties;
	@NonNull
	private final UserService userService;
	@NonNull
	private final CompanyService companyService;
	@NonNull
	private final CurrencyService currencyService;
	@NonNull
	private final IndexService indexService;

	@RequestMapping(value = "/portfolio", method = RequestMethod.POST)
	public ModelAndView updatePortfolio(@RequestParam(value = CURRENCY_UPDATE, required = false) final String updateCurrencies,
										@ModelAttribute final User user,
										@CookieValue(LANGUAGE) final String lang) throws IOException, ServletException {
		final ModelAndView model = new ModelAndView("forward:/" + HOME);
		String yahooError = null;
		String yahooUpdateCompanyError = null;
		try {
			final Portfolio portfolio = userService.getUserPortfolio(user.getId()).orElseThrow(() -> new NotFoundException(user.getId()));
			if (updateCurrencies != null) {
				currencyService.updateOneCurrency(portfolio.getCurrency());
			}
			yahooUpdateCompanyError = companyService.addOrUpdateCompaniesLimitedRequest(portfolio.getCompaniesYahooIdRealTime());
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
			model.addObject(UPDATE_STATUS, "<span class='cQuoteUp'>" + LanguageFactory.INSTANCE.getLanguage(lang).get("CONSTANT_UPDATED") + " !</span>");
		}
		return model;
	}

	@RequestMapping(value = "/charts", method = RequestMethod.GET)
	public ModelAndView charts(@ModelAttribute final User user, @CookieValue(LANGUAGE) final String lang) throws ServletException {
		final ModelAndView model = new ModelAndView("charts");
		try {
			final Portfolio portfolio = userService.getUserPortfolio(user.getId()).orElseThrow(() -> new NotFoundException(user.getId()));
			if (!portfolio.getShareValues().isEmpty()) {
				final Date from = portfolio.getShareValues().get(portfolio.getShareValues().size() - 1).getDate();
				final List<Index> indexes = indexService.getIndexes(Constants.CAC_40, from, null);
				final List<Index> indexes2 = indexService.getIndexes(Constants.SP_500, from, null);
				portfolio.addIndexes(indexes);
				portfolio.addIndexes(indexes2);
			}
			final String mapSector = portfolio.getHTMLSectorByCompanies();
			final String mapCap = portfolio.getHTMLCapByCompanies();
			model.addObject(PORTFOLIO, portfolio);
			model.addObject(MAP_SECTOR, mapSector);
			model.addObject(MAP_CAP, mapCap);
		} catch (final YahooException e) {
			log.error("Error: {}", e.getMessage(), e);
		}
		model.addObject(LANGUAGE, LanguageFactory.INSTANCE.getLanguage(lang));
		model.addObject(APP_TITLE, appProperties.getName() + " &bull;   Charts");
		return model;
	}

	@RequestMapping(value = "/performance", method = RequestMethod.GET)
	public ModelAndView performance(@RequestParam(value = FROM, required = false) @DateTimeFormat(pattern = DATE_FORMAT) final Date fromDate,
									@RequestParam(value = TO, required = false) @DateTimeFormat(pattern = DATE_FORMAT) final Date toDate,
									@ModelAttribute final User user,
									@CookieValue(LANGUAGE) final String lang) throws ServletException, ParseException {
		final ModelAndView model = new ModelAndView("performance");
		try {
			final Portfolio portfolio = userService.getUserPortfolio(user.getId(), fromDate, toDate).orElseThrow(() -> new NotFoundException(user.getId()));
			if (portfolio.getShareValues().size() != 0) {
				Date from = portfolio.getShareValues().get(portfolio.getShareValues().size() - 1).getDate();
				// Reset time to 17:00PM to get also the cac40 into the day selected (or it would not select it
				from = Util.resetHourMinSecMill(from);
				// Put 17:00PM to the first sharevalue, to make it nice in graphic
				portfolio.getShareValues().get(portfolio.getShareValues().size() - 1).setDate(from);

				// FIXME that should be done already into get user portfolio. To verify.
				final List<Index> indexesCAC40 = indexService.getIndexes(Constants.CAC_40, from, toDate);
				final List<Index> indexesSP500 = indexService.getIndexes(Constants.SP_500, from, toDate);
				portfolio.addIndexes(indexesCAC40);
				portfolio.addIndexes(indexesSP500);
				portfolio.compute();

				Date fro = from;
				if (indexesCAC40.size() > 0) {
					final Date date = indexesCAC40.get(0).getDate();
					if (date.before(fro)) {
						fro = date;
					}
				}
				if (indexesSP500.size() > 0) {
					final Date date2 = indexesSP500.get(0).getDate();
					if (date2.before(fro)) {
						fro = date2;
					}
				}
				Date t = portfolio.getShareValues().get(0).getDate();
				if (indexesCAC40.size() > 1) {
					final Date date3 = indexesCAC40.get(indexesCAC40.size() - 1).getDate();
					if (date3.after(t)) {
						t = date3;
					}
				}
				if (indexesSP500.size() > 1) {
					final Date date = indexesSP500.get(indexesSP500.size() - 1).getDate();
					if (date.after(t)) {
						t = date;
					}
				}
				model.addObject(FROM_UNDERSCORE, fro);
				model.addObject(TO_UNDERSCORE, t);
			}
			model.addObject(PORTFOLIO, portfolio);
		} catch (final YahooException e) {
			log.error("Error: {}", e.getMessage(), e);
		}
		model.addObject(LANGUAGE, LanguageFactory.INSTANCE.getLanguage(lang));
		model.addObject(APP_TITLE, appProperties.getName() + " &bull;   Performance");
		return model;
	}

	@RequestMapping(value = "/currencies", method = RequestMethod.GET)
	public ModelAndView currencies(
		@ModelAttribute final User user,
		@CookieValue(LANGUAGE) final String lang) throws ServletException, ParseException {
		final ModelAndView model = new ModelAndView("currencies");
		final Portfolio portfolio = userService.getUserPortfolio(user.getId()).orElseThrow(() -> new NotFoundException(user.getId()));
		add(model, portfolio, lang);
		return model;
	}

	@RequestMapping(value = "/currencies", method = RequestMethod.POST)
	public ModelAndView refreshCurrencies(
		@ModelAttribute final User user,
		@CookieValue(LANGUAGE) final String lang) throws ServletException, ParseException {
		final ModelAndView model = new ModelAndView("currencies");
		final Portfolio portfolio = userService.getUserPortfolio(user.getId()).orElseThrow(() -> new NotFoundException(user.getId()));
		try {
			currencyService.updateOneCurrency(portfolio.getCurrency());
			model.addObject(MESSAGE, "Done !");
		} catch (final YahooException e) {
			model.addObject(ERROR, e.getMessage());
		}
		add(model, portfolio, lang);
		return model;
	}

	public void add(final ModelAndView model, final Portfolio portfolio, final String lang) {
		final Object[][] tab = currencyService.getAllCurrencyData(portfolio.getCurrency());
		model.addObject(PORTFOLIO, portfolio);
		model.addObject(TAB, tab);
		model.addObject(LANGUAGE, LanguageFactory.INSTANCE.getLanguage(lang));
		model.addObject(APP_TITLE, appProperties.getName() + " &bull;   Currencies");
	}

	// FIXME: PDF generated does not work
	@RequestMapping(value = "/pdf", method = RequestMethod.GET, produces = MediaType.APPLICATION_PDF_VALUE)
	public void pdf(final HttpServletResponse response,
					@RequestParam(value = FROM, required = false) @DateTimeFormat(pattern = DATE_FORMAT) final Date fromDate,
					@RequestParam(value = TO, required = false) @DateTimeFormat(pattern = DATE_FORMAT) final Date toDate,
					@ModelAttribute final User user,
					@CookieValue(LANGUAGE) final String lang) throws ServletException, ParseException, IOException {
		final Portfolio portfolio = userService.getUserPortfolio(user.getId()).orElseThrow(() -> new NotFoundException(user.getId()));
		final Image sectorChart = PdfReport.createPieChart((PieChart) portfolio.getPieChartSector(), "Sector Chart");
		final Image capChart = PdfReport.createPieChart((PieChart) portfolio.getPieChartCap(), "Cap Chart");
		final Image timeChart = PdfReport.createTimeChart((TimeChart) portfolio.getTimeChart(), "Share value");
		final PdfReport pdf = new PdfReport(appProperties.getReport().getIreport());
		pdf.addParam(PORTFOLIO, portfolio);
		pdf.addParam(EQUITIES, portfolio.getEquities());
		pdf.addParam(USER, user);
		pdf.addParam(SECTOR_PIE, sectorChart);
		pdf.addParam(CAP_PIE, capChart);
		pdf.addParam(SHARE_VALUE_PIE, timeChart);
		final DateFormat df = new SimpleDateFormat("dd-MM-yy");
		final String formattedDate = df.format(new Date());
		response.addHeader("Content-Disposition", "attachment; filename=" + user.getLogin() + formattedDate + ".pdf");
		try (final OutputStream responseOutputStream = response.getOutputStream()) {
			JasperExportManager.exportReportToPdfStream(pdf.getReport(), responseOutputStream);
		} catch (final JRException e) {
			throw new ServletException("Error: " + e.getMessage(), e);
		}
	}
}
