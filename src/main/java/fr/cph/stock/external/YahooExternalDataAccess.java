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

package fr.cph.stock.external;

import fr.cph.stock.entities.Company;
import fr.cph.stock.entities.CurrencyData;
import fr.cph.stock.entities.Index;
import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.enumtype.Market;
import fr.cph.stock.exception.YahooException;
import fr.cph.stock.exception.YahooUnknownTickerException;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static fr.cph.stock.util.Constants.QUOTE;

/**
 * This class connect to yahooGateway api and convert the jsonObjects to java bean of the app
 *
 * @author Carl-Philipp Harmant
 */
public class YahooExternalDataAccess implements IExternalDataAccess {

	/**
	 * Logger
	 **/
	private static final Logger LOG = Logger.getLogger(YahooExternalDataAccess.class);
	private static final DateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	private static final String CHANGE_IN_PERCENT = "ChangeinPercent";
	private static final String CATEGORY = "Category";
	private static final String CURRENCY = "Currency";
	private static final String CLOSE = "Close";
	private static final String DATE = "Date";
	private static final String DIVIDEND_YIELD = "DividendYield";
	private static final String DIAGNOSTICS = "diagnostics";
	private static final String DESCRIPTION = "description";
	private static final String ERROR = "error";
	private static final String FUND_FAMILY = "FundFamily";
	private static final String INDUSTRY = "Industry";
	private static final String JAVASCRIPT = "javascript";
	private static final String LAST_TRADE_PRICE_ONLY = "LastTradePriceOnly";
	private static final String PREVIOUS_CLOSE = "PreviousClose";
	private static final String QUERY = "query";
	private static final String MARKET_CAPITALIZATION = "MarketCapitalization";
	private static final String NET_ASSETS = "NetAssets";
	private static final String NULL = "null";
	private static final String NAME = "Name";
	private static final String RATE_UPPERCASE = "Rate";
	private static final String RATE_LOWERCASE = "rate";
	private static final String RESULTS = "results";
	private static final String STOCK_EXCHANGE = "StockExchange";
	private static final String SYMBOL = "symbol";
	private static final String SECTOR = "Sector";
	private static final String STOCK = "stock";
	private static final String YEAR_LOW = "YearLow";
	private static final String YEAR_HIGH = "YearHigh";

	private YahooGateway yahooGateway;

	public YahooExternalDataAccess() {
		yahooGateway = YahooGateway.INSTANCE;
	}

	// TODO sounds like we need GSON or Jackson here.
	@Override
	public final List<Company> getCompaniesData(final List<String> yahooIds) throws YahooException {
		final List<Company> companies = new ArrayList<>();

		final String requestQuotes = "select * from yahoo.finance.quotes where symbol in (" + getFormattedList(yahooIds) + ")";
		final JSONObject json = yahooGateway.getJSONObject(requestQuotes);
		final JSONArray jsonResults = getJSONArrayFromJSONObject(json);
		for (int j = 0; j < jsonResults.size(); j++) {
			final JSONObject jsonCompany = jsonResults.getJSONObject(j);
			Company company = new Company();
			company.setYahooId(jsonCompany.optString(SYMBOL));
			company.setManual(false);
			if (jsonCompany.optString(STOCK_EXCHANGE).equals(NULL)) {
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
						LOG.error(e.getMessage(), e);
					}
					companies.add(company);
				} else {
					throw new YahooUnknownTickerException(jsonCompany.optString(SYMBOL) + YahooUnknownTickerException.TOKEN_UNKNOWN);
				}
			} else {
				company.setName(WordUtils.capitalizeFully(jsonCompany.optString(NAME)));
				final String stockExchange = jsonCompany.optString(STOCK_EXCHANGE).toUpperCase();
				company.setMarket(Market.getMarket(stockExchange));

				if (company.getMarket().equals(Market.UNKNOWN)) {
					final String currency = jsonCompany.optString(CURRENCY);
					if (StringUtils.isNotEmpty(currency) && !currency.equals(NULL)) {
						company.setCurrency(Currency.getEnum(currency));
					}
				} else {
					company.setCurrency(Market.getCurrency(company.getMarket()));
				}

				if (!jsonCompany.optString(DIVIDEND_YIELD).equals(NULL)) {
					company.setYield(Double.valueOf(jsonCompany.optString(DIVIDEND_YIELD)));
				}
				final String lastTrade = jsonCompany.optString(LAST_TRADE_PRICE_ONLY);
				if (StringUtils.isNotEmpty(lastTrade) && !lastTrade.equals(NULL)) {
					company.setQuote(Double.valueOf(lastTrade));
				}
				final String marketCap = jsonCompany.optString(MARKET_CAPITALIZATION);
				if (StringUtils.isNotEmpty(marketCap) && !marketCap.equals(NULL)) {
					company.setMarketCapitalization(marketCap);
				}
				final Double previousClose = jsonCompany.optDouble(PREVIOUS_CLOSE);
				if (!previousClose.isNaN()) {
					company.setYesterdayClose(previousClose);
				} else {
					company.setYesterdayClose(0.0);
				}
				company.setChangeInPercent(jsonCompany.optString(CHANGE_IN_PERCENT));
				final Double yearLow = jsonCompany.optDouble(YEAR_LOW);
				if (!yearLow.isNaN()) {
					company.setYearLow(yearLow);
				}
				final Double yearHigh = jsonCompany.optDouble(YEAR_HIGH);
				if (!yearHigh.isNaN()) {
					company.setYearHigh(yearHigh);
				}
				company.setRealTime(true);
				company.setFund(false);
				companies.add(company);
			}
		}
		return companies;
	}

	@Override
	public final Company getCompanyInfo(final Company company) throws YahooException {
		final String requestStocks = "select * from yahoo.finance.stocks where symbol='" + company.getYahooId() + "'";
		JSONObject jsonCompanyInfo = yahooGateway.getJSONObject(requestStocks);
		try {
			jsonCompanyInfo = jsonCompanyInfo.getJSONObject(QUERY).getJSONObject(RESULTS).getJSONObject(STOCK);
		} catch (JSONException e) {
			throw new YahooException("Error while getting info from json : " + company.getYahooId() + " " + jsonCompanyInfo, e);
		}
		String sector = jsonCompanyInfo.optString(SECTOR);
		String industry = jsonCompanyInfo.optString(INDUSTRY);
		if (StringUtils.isEmpty(sector) && StringUtils.isEmpty(industry)) {
			sector = jsonCompanyInfo.optString(CATEGORY);
			final String fundFamily = jsonCompanyInfo.optString(FUND_FAMILY);
			if (fundFamily != null) {
				company.setFund(true);
			}
			industry = fundFamily;
			final String marketCap = jsonCompanyInfo.optString(NET_ASSETS);
			if (StringUtils.isNotEmpty(marketCap) && !marketCap.equals(NULL)) {
				company.setMarketCapitalization(jsonCompanyInfo.optString(NET_ASSETS));
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
				final JSONObject json = yahooGateway.getJSONObject(request);
				JSONObject jsonn = json.optJSONObject(QUERY);
				if (jsonn != null) {
					final JSONObject jsonnn = jsonn.optJSONObject(RESULTS);
					if (jsonnn != null) {
						final JSONArray jsonArray = jsonnn.optJSONArray(RATE_LOWERCASE);
						if (jsonArray != null) {
							// 1st part
							final JSONObject response1 = jsonArray.getJSONObject(0);
							CurrencyData currencyData = new CurrencyData();
							currencyData.setCurrency1(currency);
							currencyData.setCurrency2(c);
							currencyData.setValue(response1.getDouble(RATE_UPPERCASE));
							currenciesData.add(currencyData);

							// 2nd part
							final JSONObject response2 = jsonArray.getJSONObject(1);
							CurrencyData currencyData2 = new CurrencyData();
							currencyData2.setCurrency1(c);
							currencyData2.setCurrency2(currency);
							currencyData2.setValue(response2.getDouble(RATE_UPPERCASE));
							currenciesData.add(currencyData2);
						}
					}
				} else {
					jsonn = json.optJSONObject(ERROR);
					if (jsonn == null) {
						LOG.error("query null: " + json);
					} else {
						final String description = jsonn.getString(DESCRIPTION);
						if (description == null) {
							LOG.warn("error description null: " + jsonn);
						} else {
							LOG.warn("error description: " + description);
						}
					}
				}
			}
		}
		return currenciesData;
	}

	@Override
	public final List<Index> getIndexDataHistory(final String yahooId, final Date from, final Date to) throws YahooException {
		final String startDate = SIMPLE_DATE_FORMAT.format(from);
		final Calendar cal = Calendar.getInstance();
		final String endDate = to == null
			? SIMPLE_DATE_FORMAT.format(cal.getTime())
			: SIMPLE_DATE_FORMAT.format(to);
		final List<Index> indexes = new ArrayList<>();
		final String request = "select * from yahoo.finance.historicaldata where symbol = \"" + yahooId + "\" and startDate = \"" + startDate + "\" and endDate = \"" + endDate + "\"";
		final JSONObject json = yahooGateway.getJSONObject(request);
		final JSONArray jsonResults = getJSONArrayFromJSONObject(json);
		for (int j = 0; j < jsonResults.size(); j++) {
			final JSONObject jsonIndex = jsonResults.getJSONObject(j);
			final Index index = new Index();
			index.setValue(jsonIndex.getDouble(CLOSE));

			Date date;
			try {
				date = SIMPLE_DATE_FORMAT.parse(jsonIndex.optString(DATE));
			} catch (final ParseException e) {
				throw new YahooException(e.getMessage(), e);
			}
			final Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			index.setDate(calendar.getTime());
			index.setYahooId(yahooId);
			indexes.add(index);
		}
		return indexes;
	}

	@Override
	public final List<Company> getCompanyDataHistory(final String yahooId, final Date from, final Date to) throws YahooException {
		final String startDate = SIMPLE_DATE_FORMAT.format(from);
		final Calendar cal = Calendar.getInstance();
		final String endDate = to == null
			? SIMPLE_DATE_FORMAT.format(cal.getTime())
			: SIMPLE_DATE_FORMAT.format(to);
		final List<Company> companies = new ArrayList<>();
		final String request = "select * from yahoo.finance.historicaldata where symbol = \"" + yahooId + "\" and startDate = \"" + startDate + "\" and endDate = \"" + endDate + "\"";
		final JSONObject json = yahooGateway.getJSONObject(request);
		final JSONArray jsonResults = getJSONArrayFromJSONObject(json);
		for (int j = 0; j < jsonResults.size(); j++) {
			final JSONObject jsonIndex = jsonResults.getJSONObject(j);
			final Company company = new Company();
			try {
				final double close = jsonIndex.getDouble(CLOSE);
				company.setQuote(close);
				company.setYahooId(yahooId);
				companies.add(company);
			} catch (final JSONException e) {
				LOG.warn("Error while trying to get double (Close) from json object: " + jsonIndex);
			}
		}
		return companies;
	}

	@Override
	public final Index getIndexData(final String yahooId) throws YahooException {
		final String requestQuotes = "select * from yahoo.finance.quotes where symbol in ('" + yahooId + "')";
		final Index index = new Index();
		final JSONObject json = yahooGateway.getJSONObject(requestQuotes);

		JSONObject jsonn;
		try {
			jsonn = json.getJSONObject(QUERY).getJSONObject(RESULTS).getJSONObject(QUOTE);
		} catch (final JSONException e) {
			throw new YahooException(YahooException.ERROR, e);
		}
		index.setYahooId(yahooId);
		index.setValue(Double.valueOf(jsonn.optString(LAST_TRADE_PRICE_ONLY)));

		return index;
	}

	/**
	 * Get a json array from a json object. Don't remember the goal of this function
	 *
	 * @param json the jsonObject
	 * @return a jsonArray
	 * @throws YahooException the yahooGateway exception
	 */
	private JSONArray getJSONArrayFromJSONObject(final JSONObject json) throws YahooException {
		final JSONObject jQuery = json.optJSONObject(QUERY);
		JSONArray quotes;
		if (jQuery != null) {
			final JSONObject jsonResults = jQuery.optJSONObject(RESULTS);
			if (jsonResults != null) {
				quotes = jsonResults.optJSONArray(QUOTE);
				if (quotes == null) {
					final JSONObject quote = jsonResults.getJSONObject(QUOTE);
					if (quote == null) {
						final JSONObject error2 = json.getJSONObject(QUERY).getJSONObject(DIAGNOSTICS).optJSONObject(JAVASCRIPT);
						if (error2 == null) {
							throw new YahooException("Can't get the error message");
						} else {
							throw new YahooException(error2.optString("content"));
						}

					} else {
						quotes = new JSONArray();
						quotes.add(quote);
					}
				}
			} else {
				final JSONObject error = json.optJSONObject(QUERY).optJSONObject(DIAGNOSTICS).optJSONObject(JAVASCRIPT);
				if (error == null) {
					LOG.debug("JSONObject found: " + json.optJSONObject(QUERY).optJSONObject(DIAGNOSTICS));
					throw new YahooException("The current table 'yahoo.finance.quotes' has probably been blocked.");
				} else {
					throw new YahooException(error.optString("content"));
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
	 * @param yahooId the yahooGateway id
	 * @return a Market
	 */
	private Market guessMarket(final String yahooId) {
		final String suffix = yahooId.substring(yahooId.indexOf('.') + 1, yahooId.length());
		return Market.getMarketFromSuffix(suffix);
	}
}
