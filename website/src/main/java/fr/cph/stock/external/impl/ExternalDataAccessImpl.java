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

package fr.cph.stock.external.impl;

import fr.cph.stock.entities.Company;
import fr.cph.stock.entities.CurrencyData;
import fr.cph.stock.entities.Index;
import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.enumtype.Market;
import fr.cph.stock.exception.YahooException;
import fr.cph.stock.external.ExternalDataAccess;
import fr.cph.stock.external.YahooGateway;
import fr.cph.stock.external.web.company.CompaniesData;
import fr.cph.stock.external.web.company.CompanyData;
import fr.cph.stock.external.web.company.Quote;
import fr.cph.stock.external.web.currency.history.HistoryResult;
import fr.cph.stock.external.web.currency.xchange.Rate;
import fr.cph.stock.external.web.currency.xchange.XChangeResult;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

/**
 * This class connect to yahooGatewayImpl api and convert the jsonObjects to java bean of the app
 *
 * @author Carl-Philipp Harmant
 */
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Component
@Log4j2
public class ExternalDataAccessImpl implements ExternalDataAccess {

	private static final DateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private static final String NULL = "null";

	@NonNull
	private final YahooGateway yahooGateway;

	@Override
	public final Stream<Company> getCompaniesData(final List<String> yahooIds) {
		final String requestQuotes = "select * from yahoo.finance.quotes where symbol in (" + getFormattedList(yahooIds) + ")";
		if (yahooIds.size() == 1) {
			return getCompanyData(yahooIds.get(0));
		} else {
			final CompaniesData.Query.Results results = yahooGateway.getObject(requestQuotes, CompaniesData.class).getQuery().getResults();
			if (results != null) {
				return yahooGateway.getObject(requestQuotes, CompaniesData.class).getQuery().getResults().getQuote().stream().map(this::buildCompany);
			} else {
				log.error("The YQL http request worked but the response did not contain any results");
				throw new YahooException("Error while refreshing data");
			}
		}
	}

	private Stream<Company> getCompanyData(final String yahooId) {
		final String requestQuote = "select * from yahoo.finance.quotes where symbol in ('" + yahooId + "')";
		final CompanyData json = yahooGateway.getObject(requestQuote, CompanyData.class);
		final Quote quote = json.getQuery().getResults().getQuote();
		final Company company = buildCompany(quote);
		return Stream.of(company);
	}

	private Company buildCompany(final Quote quote) {
		final Company company = Company.builder()
			.yahooId(quote.getSymbol())
			.name(WordUtils.capitalizeFully(quote.getName()))
			.market(Market.getMarket(quote.getStockExchange()))
			.yield(quote.getDividendYield() == null ? 0.0 : quote.getDividendYield())
			.quote(quote.getLastTradePriceOnly() == null ? 0.0 : quote.getLastTradePriceOnly())
			.marketCapitalization(quote.getMarketCapitalization())
			.yesterdayClose(quote.getPreviousClose() == null ? 0.0 : quote.getPreviousClose())
			.changeInPercent(quote.getChangeinPercent())
			.yearLow(quote.getYearLow())
			.yearHigh(quote.getYearHigh())
			.realTime(true)
			.fund(false)
			.manual(false)
			.build();
		if (company.getMarket().equals(Market.UNKNOWN)) {
			final String currency = quote.getCurrency();
			if (StringUtils.isNotEmpty(currency) && !currency.equals(NULL)) {
				company.setCurrency(Currency.getEnum(currency));
			}
		} else {
			company.setCurrency(Market.getCurrency(company.getMarket()));
		}
		return company;
	}

	@Override
	public final Stream<CurrencyData> getCurrencyData(final Currency currency) {
		return Arrays.stream(Currency.values())
			.filter(c -> c != currency)
			.flatMap(c -> {
				final StringBuilder sb = new StringBuilder();
				sb.append("\"").append(currency.getCode()).append(c.getCode()).append("\",\"").append(c.getCode()).append(currency.getCode()).append("\"");
				final String request = "select * from yahoo.finance.xchange where pair in (" + sb + ")";
				final XChangeResult xChangeResult = yahooGateway.getObject(request, XChangeResult.class);
				if (xChangeResult.getQuery().getResults() == null) {
					log.error("The YQL http request worked but the response did not contain any results");
					throw new YahooException("Error while refreshing currencies");
				}
				final List<Rate> rates = xChangeResult.getQuery().getResults().getRate();
				if (rates != null && rates.size() == 2) {
					final CurrencyData currencyData = CurrencyData.builder()
						.currency1(currency)
						.currency2(c)
						.value(rates.get(0).getRate())
						.build();
					final CurrencyData currencyData2 = CurrencyData.builder()
						.currency1(c)
						.currency2(currency)
						.value(rates.get(1).getRate())
						.build();
					return Stream.of(currencyData, currencyData2);
				} else {
					log.error("Could not find currency data: {}", xChangeResult);
					return Stream.empty();
				}
			});
	}

	@Override
	public final Stream<Company> getCompanyDataHistory(final String yahooId, final Date from, final Date to) {
		final String startDate = SIMPLE_DATE_FORMAT.format(from);
		final Calendar cal = Calendar.getInstance();
		final String endDate = to == null
			? SIMPLE_DATE_FORMAT.format(cal.getTime())
			: SIMPLE_DATE_FORMAT.format(to);
		final String request = "select * from yahoo.finance.historicaldata where symbol = \"" + yahooId + "\" and startDate = \"" + startDate + "\" and endDate = \"" + endDate + "\"";
		final HistoryResult historyResult = yahooGateway.getObject(request, HistoryResult.class);
		final List<fr.cph.stock.external.web.currency.history.Quote> listResult = historyResult.getQuery().getResults().getQuote();
		return listResult != null
			? listResult.stream().map(quote -> Company.builder().quote(quote.getClose()).yahooId(yahooId).build())
			: Stream.empty();
	}

	@Override
	public final Index getIndexData(final String yahooId) throws YahooException {
		final String requestQuotes = "select * from yahoo.finance.quotes where symbol in ('" + yahooId + "')";
		final CompanyData companyData = yahooGateway.getObject(requestQuotes, CompanyData.class);
		return Index.builder()
			.yahooId(yahooId)
			.value(companyData.getQuery().getResults().getQuote().getLastTradePriceOnly())
			.build();
	}

	/**
	 * Format a list to json
	 *
	 * @param list a list
	 * @return a String json friendly
	 */
	private String getFormattedList(final List<String> list) {
		final StringBuilder sb = new StringBuilder();
		int i = 0;
		for (final String str : list) {
			if (i != 0) {
				sb.append(",");
			}
			sb.append("'").append(str).append("'");
			i++;
		}
		return sb.toString();
	}
}
