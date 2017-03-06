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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import fr.cph.stock.entities.Company;
import fr.cph.stock.entities.CurrencyData;
import fr.cph.stock.entities.Index;
import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.enumtype.Market;
import fr.cph.stock.exception.YahooException;
import fr.cph.stock.external.ExternalDataAccess;
import fr.cph.stock.external.YahooGateway;
import fr.cph.stock.external.web.company.Quote;
import fr.cph.stock.external.web.currency.history.HistoryResult;
import fr.cph.stock.external.web.currency.xchange.Rate;
import fr.cph.stock.external.web.currency.xchange.XChangeResult;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static fr.cph.stock.util.Constants.QUOTE;

/**
 * This class connect to yahooGatewayImpl api and convert the jsonObjects to java bean of the app
 *
 * @author Carl-Philipp Harmant
 */
@Log4j2
@Singleton
public class ExternalDataAccessImpl implements ExternalDataAccess {

	private static final DateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private static final String DIAGNOSTICS = "diagnostics";
	private static final String JAVASCRIPT = "javascript";
	private static final String QUERY = "query";
	private static final String NULL = "null";
	private static final String RESULTS = "results";

	@NonNull
	private YahooGateway yahooGateway;
	@NonNull
	private Gson gson;

	@Inject
	public ExternalDataAccessImpl(final YahooGateway yahooGateway, final Gson gson) {
		this.yahooGateway = yahooGateway;
		this.gson = gson;
	}

	@Override
	public Stream<Company> getCompaniesData(final List<String> yahooIds) {
		final String requestQuotes = "select * from yahoo.finance.quotes where symbol in (" + getFormattedList(yahooIds) + ")";
		final JsonObject json = yahooGateway.getJSONObject(requestQuotes);
		final JsonArray jsonResults = getJSONArrayFromJSONObject(json);
		return StreamSupport.stream(jsonResults.spliterator(), false)
			.map(jsonElement -> gson.fromJson(jsonElement, Quote.class))
			.map(quote -> {
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
			});
	}

	@Override
	public final Stream<CurrencyData> getCurrencyData(final Currency currency) {
		return Arrays.stream(Currency.values())
			.filter(c -> c != currency)
			.flatMap(c -> {
				final StringBuilder sb = new StringBuilder();
				sb.append("\"").append(currency.getCode()).append(c.getCode()).append("\",\"").append(c.getCode()).append(currency.getCode()).append("\"");
				final String request = "select * from yahoo.finance.xchange where pair in (" + sb + ")";
				final XChangeResult baseResultDTO = (XChangeResult) yahooGateway.getObject(request, XChangeResult.class);
				final List<Rate> rates = baseResultDTO.getQuery().getResults().getRate();
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
					log.error("Could not find currency data: {}", baseResultDTO);
					return Stream.empty();
				}
			});
	}

	@Override
	public Stream<Company> getCompanyDataHistory(final String yahooId, final Date from, final Date to) {
		final String startDate = SIMPLE_DATE_FORMAT.format(from);
		final Calendar cal = Calendar.getInstance();
		final String endDate = to == null
			? SIMPLE_DATE_FORMAT.format(cal.getTime())
			: SIMPLE_DATE_FORMAT.format(to);
		final String request = "select * from yahoo.finance.historicaldata where symbol = \"" + yahooId + "\" and startDate = \"" + startDate + "\" and endDate = \"" + endDate + "\"";
		final HistoryResult historyResult = (HistoryResult) yahooGateway.getObject(request, HistoryResult.class);
		final List<fr.cph.stock.external.web.currency.history.Quote> listResult = historyResult.getQuery().getResults().getQuote();
		if (listResult != null) {
			return listResult.stream().map(quote -> Company.builder().quote(quote.getClose()).yahooId(yahooId).build());
		} else {
			return Stream.empty();
		}
	}

	@Override
	public final Index getIndexData(final String yahooId) throws YahooException {
		final String requestQuotes = "select * from yahoo.finance.quotes where symbol in ('" + yahooId + "')";
		final JsonObject json = yahooGateway.getJSONObject(requestQuotes);
		final Quote quote = gson.fromJson(json.getAsJsonObject(QUERY).getAsJsonObject(RESULTS).getAsJsonObject(QUOTE), Quote.class);
		return Index.builder()
			.yahooId(yahooId)
			.value(quote.getLastTradePriceOnly())
			.build();
	}

	/**
	 * Get a json array from a json object. Don't remember the goal of this function
	 *
	 * @param json the jsonObject
	 * @return a jsonArray
	 * @throws YahooException the yahooGatewayImpl exception
	 */
	private JsonArray getJSONArrayFromJSONObject(final JsonObject json) {
		final JsonObject jQuery = json.getAsJsonObject(QUERY);
		JsonArray quotes;
		if (jQuery != null) {
			final JsonObject jsonResults = jQuery.getAsJsonObject(RESULTS);
			if (jsonResults != null) {
				if (jsonResults.get(QUOTE).isJsonArray()) {
					quotes = jsonResults.getAsJsonArray(QUOTE);
				} else {
					final JsonObject quote = jsonResults.getAsJsonObject(QUOTE);
					if (quote == null) {
						final JsonObject error2 = json.getAsJsonObject(QUERY).getAsJsonObject(DIAGNOSTICS).getAsJsonObject(JAVASCRIPT);
						if (error2 == null) {
							throw new YahooException("Can't get the error message");
						} else {
							throw new YahooException(error2.getAsJsonObject("content").getAsString());
						}
					} else {
						quotes = new JsonArray();
						quotes.add(quote);
					}
				}
			} else {
				final JsonObject error = json.getAsJsonObject(QUERY).getAsJsonObject(DIAGNOSTICS).getAsJsonObject(JAVASCRIPT);
				if (error == null) {
					log.debug("JSONObject found: {}", json.getAsJsonObject(QUERY).getAsJsonObject(DIAGNOSTICS));
					throw new YahooException("The current table 'yahoo.finance.quotes' has probably been blocked.");
				} else {
					throw new YahooException(error.getAsJsonObject("content").getAsString());
				}
			}
		} else {
			throw new YahooException("Something went wrong. Query object null");
		}
		return quotes;
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

	/**
	 * Guess the market from the id
	 *
	 * @param yahooId the yahooGatewayImpl id
	 * @return a Market
	 */
	private Market guessMarket(final String yahooId) {
		final String suffix = yahooId.substring(yahooId.indexOf('.') + 1, yahooId.length());
		return Market.getMarketFromSuffix(suffix);
	}
}
