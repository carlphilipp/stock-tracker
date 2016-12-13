/**
 * Copyright 2016 Carl-Philipp Harmant
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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import fr.cph.stock.entities.Company;
import fr.cph.stock.entities.CurrencyData;
import fr.cph.stock.entities.Index;
import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.enumtype.Market;
import fr.cph.stock.exception.YahooException;
import fr.cph.stock.exception.YahooUnknownTickerException;
import fr.cph.stock.external.ExternalDataAccess;
import fr.cph.stock.external.YahooGateway;
import fr.cph.stock.external.web.company.Quote;
import fr.cph.stock.external.web.currency.history.HistoryResult;
import fr.cph.stock.external.web.currency.xchange.Rate;
import fr.cph.stock.external.web.currency.xchange.XChangeResult;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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

	private static final String CATEGORY = "Category";
	private static final String CLOSE = "Close";
	private static final String DIAGNOSTICS = "diagnostics";
	private static final String FUND_FAMILY = "FundFamily";
	private static final String INDUSTRY = "Industry";
	private static final String JAVASCRIPT = "javascript";
	private static final String LAST_TRADE_PRICE_ONLY = "LastTradePriceOnly";
	private static final String QUERY = "query";
	private static final String NET_ASSETS = "NetAssets";
	private static final String NULL = "null";
	private static final String RESULTS = "results";
	private static final String SECTOR = "Sector";
	private static final String STOCK = "stock";

	@NonNull
	private YahooGateway yahooGateway;
	@NonNull
	private Gson gson;

	@Inject
	public ExternalDataAccessImpl(final YahooGateway yahooGateway, final Gson gson) {
		this.yahooGateway = yahooGateway;
		this.gson = gson;
	}

/*	public static void main(String[] args) thr	ows YahooException {
		Gson gson = new Gson();
		ExternalDataAccessImpl externalDataAccess = new ExternalDataAccessImpl(new YahooGatewayImpl(gson), gson);
		Company company = Company.builder().yahooId("APPL").build();
		//externalDataAccess.getCompanyInfo(company);
		final Calendar from = Calendar.getInstance();
		from.add(Calendar.DATE, -7);
		List<Company> companies = externalDataAccess.getCompanyDataHistory("GOOG", from.getTime(), null);
		System.out.println(companies);
	}*/

	@Override
	public List<Company> getCompaniesData(final List<String> yahooIds) throws YahooException {
		final List<Company> companies = new ArrayList<>();

		final String requestQuotes = "select * from yahoo.finance.quotes where symbol in (" + getFormattedList(yahooIds) + ")";
		final JsonObject json = yahooGateway.getJSONObject(requestQuotes);
		final JsonArray jsonResults = getJSONArrayFromJSONObject(json);
		for (int j = 0; j < jsonResults.size(); j++) {
			final Quote quote = gson.fromJson(jsonResults.get(j), Quote.class);
			if (quote.getStockExchange() == null) {
				Company company = Company.builder().yahooId(quote.getSymbol()).manual(false).build();
				company = getCompanyInfo(company);
				if (company.getSector() != null && company.getIndustry() != null && company.getMarketCapitalization() != null) {
					company.setRealTime(false);
					company.setMarket(guessMarket(company.getYahooId()));
					company.setCurrency(Market.getCurrency(company.getMarket()));
					final Calendar cal = Calendar.getInstance();
					cal.add(Calendar.DATE, -5);
					try {
						final List<Company> temp = getCompanyDataHistory(company.getYahooId(), cal.getTime(), null);
						company.setQuote(temp.get(0).getQuote());
					} catch (final YahooException e) {
						log.error(e.getMessage(), e);
					}
					companies.add(company);
				} else {
					throw new YahooUnknownTickerException(quote.getSymbol() + YahooUnknownTickerException.TOKEN_UNKNOWN);
				}
			} else {
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
				companies.add(company);
			}
		}
		return companies;
	}

	// TODO see if this method is really useful, it seems that the response of the request contains no relevant data
	@Override
	public Company getCompanyInfo(final Company company) throws YahooException {
		final String requestStocks = "select * from yahoo.finance.stocks where symbol='" + company.getYahooId() + "'";
		JsonObject jsonCompanyInfo = yahooGateway.getJSONObject(requestStocks);
		try {
			jsonCompanyInfo = jsonCompanyInfo.getAsJsonObject(QUERY).getAsJsonObject(RESULTS).getAsJsonObject(STOCK);
		} catch (final Exception e) {
			throw new YahooException("Error while getting info from json : " + company.getYahooId() + " " + jsonCompanyInfo, e);
		}
		String sector = null;
		String industry = null;
		JsonElement sectorJsonElement = jsonCompanyInfo.get(SECTOR);
		JsonElement industryJsonElement = jsonCompanyInfo.get(INDUSTRY);
		if ((sectorJsonElement == null || sectorJsonElement.isJsonNull()) && (industryJsonElement == null || industryJsonElement.isJsonNull())) {
			if (jsonCompanyInfo.get(CATEGORY) != null) {
				sector = jsonCompanyInfo.get(CATEGORY).getAsString();
			}
			if (jsonCompanyInfo.get(FUND_FAMILY) != null) {
				final String fundFamily = jsonCompanyInfo.get(FUND_FAMILY).getAsString();
				company.setFund(true);
				industry = fundFamily;
			}
			final JsonElement marketCap = jsonCompanyInfo.get(NET_ASSETS);
			if (marketCap != null && StringUtils.isNotEmpty(marketCap.getAsString()) && !marketCap.getAsString().equals(NULL)) {
				company.setMarketCapitalization(marketCap.getAsString());
			}
		}
		if (StringUtils.isNotEmpty(industry)) {
			company.setIndustry(industry);
		}
		if (StringUtils.isNotEmpty(sector)) {
			company.setSector(sector);
		}
		return company;
	}

	@Override
	public final List<CurrencyData> getCurrencyData(final Currency currency) throws YahooException {
		final Currency[] currencies = Currency.values();
		final List<CurrencyData> currenciesData = new ArrayList<>();
		for (final Currency c : currencies) {
			if (c != currency) {
				final StringBuilder sb = new StringBuilder();
				sb.append("\"").append(currency.getCode()).append(c.getCode()).append("\",\"").append(c.getCode()).append(currency.getCode()).append("\"");
				final String request = "select * from yahoo.finance.xchange where pair in (" + sb + ")";
				final XChangeResult baseResultDTO = (XChangeResult) yahooGateway.getObject(request, XChangeResult.class);
				if (baseResultDTO.getQuery().getResults().getRate() != null && baseResultDTO.getQuery().getResults().getRate().size() == 2) {
					final List<Rate> rates = baseResultDTO.getQuery().getResults().getRate();
					final CurrencyData currencyData = CurrencyData.builder()
						.currency1(currency)
						.currency2(c)
						.value(rates.get(0).getRate())
						.build();
					currenciesData.add(currencyData);
					final CurrencyData currencyData2 = CurrencyData.builder()
						.currency1(c)
						.currency2(currency)
						.value(rates.get(1).getRate())
						.build();
					currenciesData.add(currencyData2);
				} else {
					log.error("Could not find currency data: {}", baseResultDTO);
				}
			}
		}
		return currenciesData;
	}

	@Override
	public List<Company> getCompanyDataHistory(final String yahooId, final Date from, final Date to) throws YahooException {
		final String startDate = SIMPLE_DATE_FORMAT.format(from);
		final Calendar cal = Calendar.getInstance();
		final String endDate = to == null
			? SIMPLE_DATE_FORMAT.format(cal.getTime())
			: SIMPLE_DATE_FORMAT.format(to);
		final String request = "select * from yahoo.finance.historicaldata where symbol = \"" + yahooId + "\" and startDate = \"" + startDate + "\" and endDate = \"" + endDate + "\"";
		final HistoryResult historyResult = (HistoryResult) yahooGateway.getObject(request, HistoryResult.class);
		final List<fr.cph.stock.external.web.currency.history.Quote> listResult = historyResult.getQuery().getResults().getQuote();
		if (listResult != null) {
			return listResult.stream()
				.map(quote -> Company.builder().quote(quote.getClose()).yahooId(yahooId).build())
				.collect(Collectors.toList());
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public final Index getIndexData(final String yahooId) throws YahooException {
		final String requestQuotes = "select * from yahoo.finance.quotes where symbol in ('" + yahooId + "')";

		final JsonObject json = yahooGateway.getJSONObject(requestQuotes);

		JsonObject jsonn;
		try {
			jsonn = json.getAsJsonObject(QUERY).getAsJsonObject(RESULTS).getAsJsonObject(QUOTE);
		} catch (final Exception e) {
			throw new YahooException(YahooException.ERROR, e);
		}

		return Index.builder()
			.yahooId(yahooId)
			.value(jsonn.get(LAST_TRADE_PRICE_ONLY).getAsDouble())
			.build();
	}

	/**
	 * Get a json array from a json object. Don't remember the goal of this function
	 *
	 * @param json the jsonObject
	 * @return a jsonArray
	 * @throws YahooException the yahooGatewayImpl exception
	 */
	private JsonArray getJSONArrayFromJSONObject(final JsonObject json) throws YahooException {
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
