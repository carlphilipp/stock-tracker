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

package fr.cph.stock.android.entity;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.util.Log;

public class EntityBuilder {

	private static final String TAG = "EntityBuilder";

	private JSONObject json;
	private User user;
	private Portfolio portfolio;

	private NumberFormat formatCurrencyZero, formatCurrencyOne, formatCurrencyTwo;
	private NumberFormat formatLocaleZero, formatLocaleOne, formatLocaleTwo;

	public EntityBuilder(JSONObject json) {
		this.json = json;
	}

	private void buildEquities() throws JSONException {
		buildUser();
		buildPortfolio();
	}

	private void buildUser() throws JSONException {
		user = new User();
		JSONObject jsonUser = json.getJSONObject("user");

		String userIdStr = jsonUser.getString("id");
		user.setUserId(userIdStr);

		String localeStr = jsonUser.getString("locale");
		Locale locale = new Locale(localeStr);
		user.setLocale(locale);

		formatCurrencyZero = NumberFormat.getCurrencyInstance(user.getLocale());
		formatCurrencyZero.setMaximumFractionDigits(0);
		formatCurrencyZero.setMinimumFractionDigits(0);
		formatCurrencyZero.setRoundingMode(RoundingMode.HALF_DOWN);

		formatCurrencyOne = NumberFormat.getCurrencyInstance(user.getLocale());
		formatCurrencyOne.setMaximumFractionDigits(1);
		formatCurrencyOne.setMinimumFractionDigits(0);
		formatCurrencyOne.setRoundingMode(RoundingMode.HALF_DOWN);

		formatCurrencyTwo = NumberFormat.getCurrencyInstance(user.getLocale());
		formatCurrencyTwo.setMaximumFractionDigits(2);
		formatCurrencyTwo.setMinimumFractionDigits(0);
		formatCurrencyTwo.setRoundingMode(RoundingMode.HALF_DOWN);

		formatLocaleZero = NumberFormat.getInstance(user.getLocale());
		formatLocaleZero.setMaximumFractionDigits(0);
		formatLocaleZero.setMinimumFractionDigits(0);
		formatLocaleZero.setRoundingMode(RoundingMode.HALF_DOWN);

		formatLocaleOne = NumberFormat.getInstance(user.getLocale());
		formatLocaleOne.setMaximumFractionDigits(1);
		formatLocaleOne.setMinimumFractionDigits(0);
		formatLocaleOne.setRoundingMode(RoundingMode.HALF_DOWN);

		formatLocaleTwo = NumberFormat.getInstance(user.getLocale());
		formatLocaleTwo.setMaximumFractionDigits(2);
		formatLocaleTwo.setMinimumFractionDigits(0);
		formatLocaleTwo.setRoundingMode(RoundingMode.HALF_DOWN);

		String datePattern = jsonUser.getString("datePattern");
		user.setDatePattern(datePattern);

		String datePatternWithoutHourMin = jsonUser.getString("datePatternWithoutHourMin");
		user.setDatePatternWithoutHourMin(datePatternWithoutHourMin);

		JSONObject lastUpdateJSON = jsonUser.getJSONObject("lastUpdate");
		user.setLastUpdate(extractDate(lastUpdateJSON, user.getDatePattern()));
	}

	private void buildPortfolio() throws JSONException {
		portfolio = new Portfolio();
		JSONObject jsonPortfolio = json.getJSONObject("portfolio");

		double totalValueStr = jsonPortfolio.getDouble("totalValue");
		portfolio.setTotalValue(formatCurrencyZero.format(totalValueStr));

		double totalGain = jsonPortfolio.getDouble("totalGain");
		portfolio.setTotalGain(formatCurrencyZero.format(totalGain));

		double totalPlusMinusValue = jsonPortfolio.getDouble("totalPlusMinusValue");
		portfolio.setTotalPlusMinusValue(totalPlusMinusValue > 0 ? "+" + formatLocaleOne.format(totalPlusMinusValue) + "%" : formatLocaleOne
				.format(totalPlusMinusValue) + "%");

		portfolio.setUp(totalGain > 0 ? true : false);

		double liquidity = jsonPortfolio.getDouble("liquidity");
		portfolio.setLiquidity(formatCurrencyZero.format(liquidity));

		double yieldYear = jsonPortfolio.getDouble("yieldYear");
		portfolio.setYieldYear(formatCurrencyZero.format(yieldYear));

		double yieldYearPerc = jsonPortfolio.getDouble("yieldYearPerc");
		portfolio.setYieldYearPerc(formatLocaleOne.format(yieldYearPerc) + "%");

		JSONObject lastUpdateJSON = jsonPortfolio.getJSONObject("lastUpdate");
		portfolio.setLastUpdate(extractDate(lastUpdateJSON, user.getDatePattern()));

		JSONArray arrayEquities = jsonPortfolio.getJSONArray("equities");
		portfolio.setEquities(buildEquities(arrayEquities));

		JSONArray arrayShareValues = jsonPortfolio.getJSONArray("shareValues");
		portfolio.setShareValues(buildShareValues(arrayShareValues));

		JSONArray arrayAccounts = jsonPortfolio.getJSONArray("accounts");
		portfolio.setAccounts(buildAccounts(arrayAccounts));

		JSONObject performance = jsonPortfolio.getJSONObject("performance");
		portfolio.setGainPerformance(formatCurrencyOne.format(performance.getDouble("gain")));
		portfolio.setPerformancePerformance(formatLocaleOne.format(performance.getDouble("performance")) + "%");
		portfolio.setYieldPerformance(formatCurrencyOne.format(performance.getDouble("yield")));
		portfolio.setTaxesPerformace(formatCurrencyOne.format(performance.getDouble("taxes")));

		portfolio.setChartColors(jsonPortfolio.getString("chartShareValueColors"));
		portfolio.setChartData(jsonPortfolio.getString("chartShareValueData"));
		portfolio.setChartDate(jsonPortfolio.getString("chartShareValueDate"));
		portfolio.setChartDraw(jsonPortfolio.getString("chartShareValueDraw"));

		portfolio.setChartSectorData(jsonPortfolio.getString("chartSectorData"));
		portfolio.setChartSectorTitle(jsonPortfolio.getString("chartSectorTitle"));
		portfolio.setChartSectorDraw(jsonPortfolio.getString("chartSectorDraw"));
		portfolio.setChartSectorCompanies(jsonPortfolio.getString("chartSectorCompanies"));

		portfolio.setChartCapCompanies(jsonPortfolio.getString("chartCapCompanies"));
		portfolio.setChartCapData(jsonPortfolio.getString("chartCapData"));
		portfolio.setChartCapDraw(jsonPortfolio.getString("chartCapDraw"));
		portfolio.setChartCapTitle(jsonPortfolio.getString("chartCapTitle"));

		double totalVariation = jsonPortfolio.getDouble("totalVariation");
		if (totalVariation >= 0) {
			portfolio.setTodayUp(true);
			portfolio.setTotalVariation("+" + formatLocaleTwo.format(totalVariation) + "%");
		} else {
			portfolio.setTodayUp(false);
			portfolio.setTotalVariation(formatLocaleTwo.format(totalVariation) + "%");
		}

	}

	private List<Account> buildAccounts(JSONArray array) throws JSONException {
		List<Account> accounts = new ArrayList<Account>();
		boolean find = true;
		int i = 0;
		JSONObject temp;
		while (find) {
			temp = array.optJSONObject(i);
			if (temp != null) {
				Account account = new Account();
				account.setId(temp.getString("id"));
				account.setCurrency(temp.getString("currency"));
				double liquidity = temp.getDouble("liquidity");
				account.setLiquidity(formatLocaleOne.format(liquidity));
				account.setName(temp.getString("name"));
				accounts.add(account);
				i++;
			} else {
				find = false;
			}
		}
		return accounts;
	}

	private List<ShareValue> buildShareValues(JSONArray array) throws JSONException {
		List<ShareValue> shareValues = new ArrayList<ShareValue>();
		boolean find = true;
		int i = 0;
		JSONObject temp;
		while (find) {
			temp = array.optJSONObject(i);
			if (temp != null) {
				ShareValue sv = new ShareValue();
				sv.setAccount(temp.getString("account"));
				sv.setCommentary(temp.optString("commentary"));

				JSONObject dateSON = temp.getJSONObject("date");
				sv.setDate(extractDate(dateSON, user.getDatePatternWithoutHourMin()));

				double share = temp.getDouble("shareValue");
				sv.setShareValue(formatLocaleOne.format(share));
				if (share > 100) {
					sv.setUp(true);
				} else {
					sv.setUp(false);
				}

				sv.setPortfolioValue(formatCurrencyOne.format(temp.getDouble("portfolioValue")));
				sv.setShareQuantity(formatLocaleOne.format(temp.getDouble("shareQuantity")));
				sv.setMonthlyYield(formatCurrencyOne.format(temp.getDouble("monthlyYield")));

				shareValues.add(sv);
				i++;
			} else {
				find = false;
			}
		}
		return shareValues;
	}

	private List<Equity> buildEquities(JSONArray array) throws JSONException {
		List<Equity> equities = new ArrayList<Equity>();
		boolean find = true;
		int i = 0;
		JSONObject temp;

		while (find) {
			temp = array.optJSONObject(i);
			if (temp != null) {
				Equity e = new Equity();
				e.setName(temp.getString("name"));

				double unitCostPrice = temp.getDouble("unitCostPrice");
				e.setUnitCostPrice(formatLocaleTwo.format(unitCostPrice));

				e.setValue(formatLocaleZero.format(temp.getDouble("value")));
				double plusMinusValue = temp.getDouble("plusMinusValue");
				String plusMinusValueStr = formatLocaleOne.format(plusMinusValue) + "%";
				if (plusMinusValue > 0) {
					e.setPlusMinusValue("+" + plusMinusValueStr);
				} else {
					e.setPlusMinusValue(plusMinusValueStr);
				}
				e.setUp(plusMinusValue > 0 ? true : false);

				double quantity = temp.getDouble("quantity");
				e.setQuantity(formatLocaleOne.format(quantity));

				double yieldYear = temp.getDouble("yieldYear");
				e.setYieldYear(formatLocaleOne.format(yieldYear) + "%");

				double yieldUnitCostPrice = temp.getDouble("yieldUnitCostPrice");
				e.setYieldUnitCostPrice(formatLocaleOne.format(yieldUnitCostPrice) + "%");

				double quote = temp.getDouble("quote");
				e.setQuote(formatLocaleTwo.format(quote));

				double plusMinusUnitCostPriceValue = temp.getDouble("plusMinusUnitCostPriceValue");
				if (plusMinusValue > 0) {
					e.setPlusMinusUnitCostPriceValue("+" + formatLocaleZero.format(plusMinusUnitCostPriceValue));
				} else {
					e.setPlusMinusUnitCostPriceValue(formatLocaleZero.format(plusMinusUnitCostPriceValue));
				}
				Double variation = temp.optDouble("variation");
				if (!variation.isNaN()) {
					if (variation >= 0) {
						e.setUpVariation(true);
						e.setVariation("+" + formatLocaleTwo.format(variation) + "%");
					} else {
						e.setUpVariation(false);
						e.setVariation(formatLocaleTwo.format(variation) + "%");
					}
				}else{
					e.setUpVariation(true);
					e.setVariation("+0%");
				}
				equities.add(e);
				i++;
			} else {
				find = false;
			}
		}
		return equities;
	}

	@SuppressLint("SimpleDateFormat")
	private String extractDate(JSONObject jsonDate, String pattern) throws JSONException {
		BigInteger time = new BigInteger(jsonDate.getString("time"));
		Date date = new Date();
		date.setTime(time.longValue());
		DateFormat formatter = new SimpleDateFormat(pattern);
		String formattedDate = formatter.format(date);
		return formattedDate;
	}

	public User getUser() {
		if (user == null) {
			try {
				buildEquities();
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage(), e);
				portfolio = null;
				user = null;
			}
		}
		return user;
	}

	public Portfolio getPortfolio() {
		if (portfolio == null) {
			try {
				buildEquities();
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage(), e);
				portfolio = null;
				user = null;
			}
		}
		return portfolio;
	}

}
