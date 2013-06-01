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

package fr.cph.stock.external;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;

import fr.cph.stock.entities.Company;
import fr.cph.stock.entities.CurrencyData;
import fr.cph.stock.entities.Index;
import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.enumtype.Market;
import fr.cph.stock.exception.YahooException;

public class YahooExternalDataAccess implements IExternalDataAccess {

	private static final Logger log = Logger.getLogger(YahooExternalDataAccess.class);

	@Override
	public List<Company> getCompaniesData(List<String> yahooIds) throws YahooException {
		List<Company> companies = new ArrayList<Company>();

		String requestQuotes = "select * from yahoo.finance.quotes where symbol in (" + getFormattedList(yahooIds) + ")";
		Yahoo yahoo = new Yahoo(requestQuotes);
		JSONObject json = yahoo.getJSONObject();
		JSONArray jsonResults = getJSONArrayFromJSONObject(json);
		for (int j = 0; j < jsonResults.size(); j++) {
			JSONObject jsonCompany = jsonResults.getJSONObject(j);
			Company company = new Company();
			company.setYahooId(jsonCompany.optString("symbol"));

			if (jsonCompany.optString("StockExchange").equals("null")) {
				company = getCompanyInfo(company);
				if (company.getSector() != null && company.getIndustry() != null && company.getMarketCapitalization() != null) {
					company.setRealTime(false);
					company.setMarket(guessMarket(company.getYahooId()));
					company.setCurrency(Market.getCurrency(company.getMarket()));
					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.DATE, -5);
					try {
						List<Company> temp = getCompanyDataHistory(company.getYahooId(), cal.getTime(), null);
						company.setQuote(temp.get(0).getQuote());
					} catch (YahooException e) {
						log.info(e.getMessage(), e);
					}
					companies.add(company);
				} else {
					throw new YahooException(jsonCompany.optString("symbol") + YahooException.TOCKEN_UNKNOWN);
				}
			} else {
				company.setName(WordUtils.capitalizeFully(jsonCompany.optString("Name")));
				company.setMarket(Market.valueOf(jsonCompany.optString("StockExchange").toUpperCase()));

				company.setCurrency(Market.getCurrency(company.getMarket()));
				if (!jsonCompany.optString("DividendYield").equals("null")) {
					company.setYield(Double.valueOf(jsonCompany.optString("DividendYield")));
				}
				company.setQuote(Double.valueOf(jsonCompany.optString("LastTradePriceOnly")));
				String marketCap = jsonCompany.optString("MarketCapitalization");
				if (marketCap != null && !marketCap.equals("null") && !marketCap.equals("")) {
					company.setMarketCapitalization(marketCap);
				}
				company.setYesterdayClose(jsonCompany.getDouble("PreviousClose"));
				company.setChangeInPercent(jsonCompany.getString("ChangeinPercent"));
				company.setYearLow(jsonCompany.getDouble("YearLow"));
				company.setYearHigh(jsonCompany.getDouble("YearHigh"));
				company.setRealTime(true);
				company.setFund(false);
				companies.add(company);
			}
		}
		return companies;
	}

	@Override
	public Company getCompanyInfo(Company company) throws YahooException {
		String requestStocks = "select * from yahoo.finance.stocks where symbol='" + company.getYahooId() + "'";
		Yahoo yahoo = new Yahoo(requestStocks);
		JSONObject jsonCompanyInfo = yahoo.getJSONObject();
		jsonCompanyInfo = jsonCompanyInfo.getJSONObject("query").getJSONObject("results").getJSONObject("stock");
		String sector = jsonCompanyInfo.optString("Sector");
		String industry = jsonCompanyInfo.optString("Industry");
		if (sector.equals("") && industry.equals("")) {
			sector = jsonCompanyInfo.optString("Category");
			String fundFamily = jsonCompanyInfo.optString("FundFamily");
			if (fundFamily != null) {
				company.setFund(true);
			}
			industry = fundFamily;
			String marketCap = jsonCompanyInfo.optString("NetAssets");
			if (marketCap != null && !marketCap.equals("null") && !marketCap.equals("")) {
				company.setMarketCapitalization(jsonCompanyInfo.optString("NetAssets"));
			}
		}
		if (!industry.equals("")) {
			company.setIndustry(industry);
		}
		if (!sector.equals("")) {
			company.setSector(sector);
		}
		return company;
	}

//	@Override
//	public Company getCompanyData(String id) throws YahooException {
//		String requestQuotes = "select * from yahoo.finance.quotes where symbol in ('" + id + "')";
//		String requestStocks = "select * from yahoo.finance.stocks where symbol='" + id + "'";
//		Yahoo yahoo = new Yahoo(requestQuotes);
//		Company company = null;
//
//		JSONObject json = yahoo.getJSONObject();
//		yahoo = new Yahoo(requestStocks);
//		JSONObject json2 = yahoo.getJSONObject();
//
//		company = new Company();
//		JSONObject jsonn = null;
//		try {
//			jsonn = json.getJSONObject("query").getJSONObject("results").getJSONObject("quote");
//		} catch (JSONException e) {
//			throw new YahooException(YahooException.ERROR, e);
//		}
//		company.setYahooId(jsonn.optString("symbol"));
//		company.setName(WordUtils.capitalizeFully(jsonn.optString("Name")));
//		if (jsonn.optString("StockExchange").equals("null")) {
//			throw new YahooException(id + YahooException.TOCKEN_UNKNOWN);
//		}
//		company.setMarket(Market.valueOf(jsonn.optString("StockExchange").toUpperCase()));
//
//		company.setCurrency(Market.getCurrency(company.getMarket()));
//		if (!jsonn.optString("DividendYield").equals("null")) {
//			company.setYield(Double.valueOf(jsonn.optString("DividendYield")));
//		}
//		company.setQuote(Double.valueOf(jsonn.optString("LastTradePriceOnly")));
//		String marketCap = jsonn.optString("MarketCapitalization");
//		if (marketCap != null && !marketCap.equals("null")) {
//			company.setMarketCapitalization(marketCap);
//		}
//
//		jsonn = json2.getJSONObject("query").getJSONObject("results").getJSONObject("stock");
//		company.setSector(jsonn.optString("Sector"));
//		company.setIndustry(jsonn.optString("Industry"));
//
//		return company;
//	}

	@Override
	public List<CurrencyData> getCurrencyData(Currency currency) throws YahooException {
		Currency[] currencies = Currency.values();
		List<CurrencyData> currenciesData = new ArrayList<CurrencyData>();
		for (Currency c : currencies) {
			if (c != currency) {
				StringBuilder sb = new StringBuilder();
				sb.append("\"" + currency.getCode() + c.getCode() + "\",\"" + c.getCode() + currency.getCode() + "\"");
				String request = "select * from yahoo.finance.xchange where pair in (" + sb + ")";
				Yahoo yahoo = new Yahoo(request);
				JSONObject json = yahoo.getJSONObject();
				JSONObject jsonn = json.optJSONObject("query");
				if (jsonn != null) {
					JSONObject jsonnn = jsonn.optJSONObject("results");
					if (jsonnn != null) {
						JSONArray jsonArray = jsonnn.optJSONArray("rate");
						if (jsonArray != null) {
							// 1st part
							JSONObject response1 = jsonArray.getJSONObject(0);
							CurrencyData currencyData = new CurrencyData();
							currencyData.setCurrency1(currency);
							currencyData.setCurrency2(c);
							currencyData.setValue(response1.getDouble("Rate"));
							currenciesData.add(currencyData);

							// 2nd part
							JSONObject response2 = jsonArray.getJSONObject(1);
							CurrencyData currencyData2 = new CurrencyData();
							currencyData2.setCurrency1(c);
							currencyData2.setCurrency2(currency);
							currencyData2.setValue(response2.getDouble("Rate"));
							currenciesData.add(currencyData2);
						}
					}
				} else {
					jsonn = json.optJSONObject("error");
					if (jsonn == null) {
						log.error("query null: " + json);
					} else {
						String description = jsonn.getString("description");
						if (description == null) {
							log.warn("error description null: " + jsonn);
						} else {
							log.warn("error description: " + description);
						}
					}
				}
			}
		}
		return currenciesData;
	}

	@Override
	public List<Index> getIndexDataHistory(String yahooId, Date from, Date to) throws YahooException {
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String startDate = formatter.format(from);
		Calendar cal = Calendar.getInstance();
		String endDate;
		if (to == null) {
			endDate = formatter.format(cal.getTime());
		} else {
			endDate = formatter.format(to);
		}
		List<Index> indexes = new ArrayList<Index>();
		String request = "select * from yahoo.finance.historicaldata where symbol = \"" + yahooId + "\" and startDate = \"" + startDate
				+ "\" and endDate = \"" + endDate + "\"";
		Yahoo yahoo = new Yahoo(request);
		JSONObject json = yahoo.getJSONObject();
		JSONArray jsonResults = getJSONArrayFromJSONObject(json);
		for (int j = 0; j < jsonResults.size(); j++) {
			JSONObject jsonIndex = jsonResults.getJSONObject(j);
			Index index = new Index();
			index.setValue(jsonIndex.getDouble("Close"));

			Date date;
			try {
				date = formatter.parse(jsonIndex.optString("Date"));
			} catch (ParseException e) {
				throw new YahooException(e.getMessage(), e);
			}
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			index.setDate(calendar.getTime());
			index.setYahooId(yahooId);
			indexes.add(index);
		}
		return indexes;
	}

	@Override
	public List<Company> getCompanyDataHistory(String yahooId, Date from, Date to) throws YahooException {
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String startDate = formatter.format(from);
		Calendar cal = Calendar.getInstance();
		String endDate;
		if (to == null) {
			endDate = formatter.format(cal.getTime());
		} else {
			endDate = formatter.format(to);
		}
		List<Company> companies = new ArrayList<Company>();
		String request = "select * from yahoo.finance.historicaldata where symbol = \"" + yahooId + "\" and startDate = \"" + startDate
				+ "\" and endDate = \"" + endDate + "\"";
		Yahoo yahoo = new Yahoo(request);
		JSONObject json = yahoo.getJSONObject();
		JSONArray jsonResults = getJSONArrayFromJSONObject(json);
		for (int j = 0; j < jsonResults.size(); j++) {
			JSONObject jsonIndex = jsonResults.getJSONObject(j);
			Company company = new Company();
			double close;
			try {
				close = jsonIndex.getDouble("Close");
				company.setQuote(close);
				company.setYahooId(yahooId);
				companies.add(company);
			} catch (JSONException e) {
				log.warn("Error while trying to get double (Close) from json object: " + jsonIndex);
			}
		}
		return companies;
	}

	@Override
	public Index getIndexData(String yahooId) throws YahooException {
		String requestQuotes = "select * from yahoo.finance.quotes where symbol in ('" + yahooId + "')";
		Yahoo yahoo = new Yahoo(requestQuotes);
		Index index = new Index();
		JSONObject json = yahoo.getJSONObject();

		JSONObject jsonn = null;
		try {
			jsonn = json.getJSONObject("query").getJSONObject("results").getJSONObject("quote");
		} catch (JSONException e) {
			throw new YahooException(YahooException.ERROR, e);
		}
		index.setYahooId(yahooId);
		index.setValue(Double.valueOf(jsonn.optString("LastTradePriceOnly")));

		return index;
	}

	protected JSONArray getJSONArrayFromJSONObject(JSONObject json) throws YahooException {
		JSONObject jQuery = json.optJSONObject("query");
		JSONArray quotes = null;
		if (jQuery != null) {
			JSONObject jsonResults = jQuery.optJSONObject("results");
			if (jsonResults != null) {
				quotes = jsonResults.optJSONArray("quote");
				if (quotes == null) {
					JSONObject quote = jsonResults.getJSONObject("quote");
					if (quote == null) {
						JSONObject error2 = json.getJSONObject("query").getJSONObject("diagnostics")
								.optJSONObject("javascript");
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
				JSONObject error = json.optJSONObject("query").optJSONObject("diagnostics").optJSONObject("javascript");
				if (error == null) {
					log.debug("JSONObject found: " + json.optJSONObject("query").optJSONObject("diagnostics"));
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

	protected String getFormattedList(List<String> list) {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (String str : list) {
			if (i != 0) {
				sb.append(",");
			}
			sb.append("'" + str + "'");
			i++;
		}
		return sb.toString();
	}

	public Market guessMarket(String yahooId) {
		String suffix = yahooId.substring(yahooId.indexOf('.') + 1, yahooId.length());
		return Market.getMarketFromSuffix(suffix);
	}
}
