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

package fr.cph.stock.entities;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fr.cph.stock.entities.chart.IChart;
import fr.cph.stock.entities.chart.PieChart;
import fr.cph.stock.entities.chart.TimeChart;
import fr.cph.stock.entities.chart.TimeValueChart;
import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.enumtype.MarketCapitalization;
import fr.cph.stock.exception.NotFoundException;
import fr.cph.stock.util.Constants;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * This class represents a portofolio that belongs to a user
 *
 * @author Carl-Philipp Harmant
 */
public class Portfolio {
	/**
	 * Precision of calculation
	 **/
	private final MathContext mathContext = MathContext.DECIMAL32;
	private static final int PERCENT = 100;
	@Getter
	@Setter
	private int id;
	@Getter
	@Setter
	private int userId;
	@Getter
	@Setter
	private Currency currency;
	@Getter
	@Setter
	private Double liquidity;
	@Getter
	@Setter
	private List<Equity> equities;
	@Getter
	@Setter
	private List<ShareValue> shareValues;
	@Getter
	@Setter
	private List<Account> accounts;
	@Getter
	@Setter
	private Map<String, List<Index>> indexes;

	// Calculated
	@Getter
	private Double totalQuantity;
	@Getter
	private Double averageUnitCostPrice;
	@Getter
	private Double averageQuotePrice;
	private Double totalValue;
	private Double totalPlusMinusValue;
	private Double yieldYear;
	private Double yieldYearPerc;
	private Double totalGain;
	private Date lastCompanyUpdate;
	/**
	 * Best performance of the share value
	 **/
	private Double maxShareValue;
	/**
	 * Date of the best performance
	 **/
	private Date maxShareValueDate;
	/**
	 * Current share value gain
	 **/
	private Double currentShareValuesGain;
	/**
	 * Current share value gain in percentage
	 **/
	private Double currentShareValuesGainPorcentage;
	/**
	 * Current share value yield
	 **/
	private Double currentShareValuesYield;
	/**
	 * Current share value taxes
	 **/
	private Double currentShareValuesTaxes;
	/**
	 * Current share value taxes
	 **/
	private Double currentShareValuesVolume;
	@Getter
	private Double totalVariation;
	@Getter
	private Double totalGainToday;
	/**
	 * Map that represents data of sector chart
	 **/
	private Map<String, Double> chartSectorData;
	/**
	 * Map that represents data of capitalization chart
	 **/
	private Map<String, Double> chartCapData;
	/**
	 * Maps that represents data of share value (user + cac40 + s&p500
	 **/
	private Map<Date, Double> chartShareValueData, chartShareValueData2, chartShareValueData3;
	/**
	 * Chart objects
	 **/
	private IChart timechart, timeValueChart, piechartsector, piechartcap;

	/**
	 * Constructor
	 */
	public Portfolio() {
		totalQuantity = 0d;
		averageUnitCostPrice = 0d;
		averageQuotePrice = 0d;
		totalValue = 0d;
		totalPlusMinusValue = 0d;
		yieldYear = 0d;
		totalGain = 0d;
		yieldYearPerc = 0d;
		liquidity = 0d;
		indexes = new HashMap<>();
	}

	/**
	 * Get total value
	 *
	 * @return the total value
	 */
	public final Double getTotalValue() {
		return totalValue + getLiquidity();
	}

	/**
	 * Get total plus minus value
	 *
	 * @return the total plus minus value
	 */
	public final Double getTotalPlusMinusValue() {
		return totalPlusMinusValue;
	}

	/**
	 * Get total plus minus value in absolute
	 *
	 * @return the total plus minus value in absolute
	 */
	public final Double getTotalPlusMinusValueAbsolute() {
		return Math.abs(totalPlusMinusValue);
	}

	/**
	 * Get yield year
	 *
	 * @return the yield year
	 */
	public final Double getYieldYear() {
		return yieldYear;
	}

	/**
	 * Get total gain
	 *
	 * @return the total gain
	 */
	public final Double getTotalGain() {
		return totalGain;
	}

	/**
	 * Get Yield per year in percentage
	 *
	 * @return a double
	 */
	public final Double getYieldYearPerc() {
		return yieldYearPerc;
	}

	/**
	 * Get last company update date
	 *
	 * @return a date
	 */
	public final Date getLastCompanyUpdate() {
		if (lastCompanyUpdate != null) {
			return (Date) lastCompanyUpdate.clone();
		} else {
			return null;
		}
	}

	/**
	 * Set last company ypdate date
	 *
	 * @param lastCompanyUpdate the date
	 */
	public final void setLastCompanyUpdate(final Date lastCompanyUpdate) {
		if (lastCompanyUpdate != null) {
			this.lastCompanyUpdate = (Date) lastCompanyUpdate.clone();
		}
	}

	/**
	 * This function generates all calculated field
	 */
	public final void compute() {
		Double totalUnitCostPrice = 0d;
		Double totalAverageQuotePrice = 0d;
		Double totalOriginalValue = 0d;
		totalVariation = 0d;
		double totalValueStart = 0;
		totalGainToday = 0d;
		Date lastUpdate = null;
		if (equities != null) {
			for (final Equity equity : equities) {
				totalQuantity += equity.getQuantity();
				totalUnitCostPrice += equity.getUnitCostPrice();
				totalAverageQuotePrice += equity.getCompany().getQuote() * equity.getParity();
				totalValue += equity.getValue();
				totalOriginalValue += equity.getQuantity() * equity.getUnitCostPrice() * equity.getCurrentParity();
				yieldYear += equity.getYieldYear();
				totalGain += equity.getPlusMinusUnitCostPriceValue();
				if (equity.getCompany().getRealTime()) {
					if (lastUpdate == null) {
						lastUpdate = equity.getCompany().getLastUpdate();
					} else {
						if (equity.getCompany().getLastUpdate() != null && lastUpdate.after(equity.getCompany().getLastUpdate())) {
							lastUpdate = equity.getCompany().getLastUpdate();
						}
					}
				}
				if (equity.getCompany().getChange() != null) {
					double valueStart = equity.getValue() / (equity.getCompany().getChange() / PERCENT + 1);
					totalValueStart += valueStart;
					totalGainToday += valueStart * equity.getCompany().getChange() / PERCENT;
				}

			}
			totalVariation = totalValueStart == 0 ? totalValueStart : ((totalValueStart + totalGainToday) / totalValueStart - 1) * PERCENT;
			averageUnitCostPrice = totalUnitCostPrice / equities.size();
			averageQuotePrice = totalAverageQuotePrice / equities.size();
			totalPlusMinusValue = ((totalValue - totalOriginalValue) / totalOriginalValue) * PERCENT;
			yieldYearPerc = yieldYear / getTotalValue() * PERCENT;
			setLastCompanyUpdate(lastUpdate);
		}
	}

	public final Double getTotalGainTodayAbsolute() {
		return Math.abs(totalGainToday);
	}

	/**
	 * Get chart sector data
	 *
	 * @return a map
	 */
	protected final Map<String, Double> getChartSectorData() {
		if (chartSectorData == null) {
			final Map<String, Double> data = new HashMap<>();
			for (final Equity equity : getEquities()) {
				if (equity.getCompany().getFund()) {
					addEquityValueToMap(data, Constants.FUND, equity);
				} else {
					final String sector = equity.getCurrentSector();
					if (sector == null) {
						addEquityValueToMap(data, Constants.UNKNOWN, equity);
					} else {
						addEquityValueToMap(data, sector, equity);
					}
				}
			}
			chartSectorData = new TreeMap<>();
			chartSectorData.putAll(data);
		}
		return chartSectorData;
	}

	private void addEquityValueToMap(final Map<String, Double> data, final String key, final Equity equity) {
		if (data.containsKey(key)) {
			data.put(key, data.get(key) + equity.getValue());
		} else {
			data.put(key, equity.getValue());
		}
	}

	/**
	 * Get chart share value data
	 *
	 * @return a map
	 */
	protected final Map<Date, Double> getChartShareValueData() {
		Map<Date, Double> data = new HashMap<>();
		List<ShareValue> shareValuess = getShareValues();
		int max = shareValuess.size();
		double base = shareValuess.get(max - 1).getShareValue();
		for (int i = max - 1; i != -1; i--) {
			ShareValue temp = shareValuess.get(i);
			Double value = temp.getShareValue() * PERCENT / base;
			data.put(temp.getDate(), value);
		}
		chartShareValueData = new TreeMap<>();
		chartShareValueData.putAll(data);
		return chartShareValueData;
	}

	/**
	 * Generate chart share value data
	 */
	protected final void getChartShareValueData2() {
		if (chartShareValueData2 == null && chartShareValueData3 == null) {
			Map<Date, Double> data = new HashMap<>();
			Map<Date, Double> data2 = new HashMap<>();
			List<ShareValue> shareValuess = getShareValues();
			int max = shareValuess.size();
			for (int i = max - 1; i != -1; i--) {
				ShareValue temp = shareValuess.get(i);
				Double value = temp.getPortfolioValue();
				Double liqui = temp.getLiquidities();
				data.put(temp.getDate(), value);
				if (liqui != null) {
					data2.put(temp.getDate(), liqui);
				}
			}
			chartShareValueData2 = new TreeMap<>();
			chartShareValueData2.putAll(data);
			chartShareValueData3 = new TreeMap<>();
			chartShareValueData3.putAll(data2);
		}
	}

	/**
	 * Get chart capitalization data
	 *
	 * @return a map
	 */
	protected final Map<String, Double> getChartCapData() {
		if (chartCapData == null) {
			Map<String, Double> data = new HashMap<>();
			for (Equity equity : getEquities()) {
				if (!equity.getCompany().getFund()) {
					MarketCapitalization marketCap = equity.getMarketCapitalizationType();
					if (marketCap == null) {
						addEquityValueToMap(data, Constants.UNKNOWN, equity);
					} else {
						addEquityValueToMap(data, marketCap.getValue(), equity);
					}
				} else {
					addEquityValueToMap(data, Constants.UNKNOWN, equity);
				}
			}
			chartCapData = new TreeMap<>();
			chartCapData.putAll(data);
		}
		return chartCapData;
	}

	/**
	 * Get a list of yahoo id representing equities that do not have real time data
	 *
	 * @return a list of yahoo id
	 */
	public final List<String> getCompaniesYahooIdRealTime() {
		return getEquities().stream()
			.filter(equity -> equity.getCompany().getRealTime())
			.map(equity -> equity.getCompany().getYahooId())
			.collect(Collectors.toList());
	}

	/**
	 * Add indexes
	 *
	 * @param indexes a list of indexes
	 */
	public final void addIndexes(final List<Index> indexes) {
		if (indexes.size() > 0) {
			String index = indexes.get(0).getYahooId();
			this.indexes.put(index, indexes);
		}
	}

	/**
	 * Get portfolio review. An horrible function.
	 *
	 * @return a string
	 */
	public final String getPortfolioReview() {
		final StringBuilder sb = new StringBuilder();
		sb.append("<table class=\"shareValueTableDetails\">");
		for (final Equity equity : getEquities()) {
			sb.append("<tr><td width=200px><b>")
				.append(equity.getCurrentName())
				.append("</b></td><td width=180px>")
				.append(equity.getQuantity())
				.append(" * ")
				.append(equity.getCompany().getQuote());
			if (equity.getCompany().getCurrency() != getCurrency()) {
				sb.append(" * ").append(getCurrency().getParity(equity.getCompany().getCurrency()));
			}
			sb.append("</td><td>").append(equity.getValue()).append(" (").append(getCurrency().getCode()).append(")</td></tr>");
		}
		sb.append("<tr><td colspan=3><b>Liquidity:</b> ").append(getLiquidity()).append(" (").append(getCurrency().getCode()).append(")</td></tr>");
		sb.append("<tr><td colspan=3><b>Total:</b> ").append(new BigDecimal(getTotalValue(), mathContext).doubleValue()).append(" (").append(getCurrency().getCode()).append(")</td></tr>");
		sb.append("</table>");
		return sb.toString();
	}

	/**
	 * Get pie chart sector
	 *
	 * @return the chart
	 */
	public final IChart getPieChartSector() {
		if (piechartsector == null) {
			final Map<String, Double> map = getChartSectorData();
			piechartsector = new PieChart(map);
			piechartsector.generate();
		}
		return piechartsector;
	}

	/**
	 * Get pie chart capitalization
	 *
	 * @return the chart
	 */
	public final IChart getPieChartCap() {
		if (piechartcap == null) {
			final Map<String, Double> map = getChartCapData();
			piechartcap = new PieChart(map);
			piechartcap.generate();
		}
		return piechartcap;
	}

	/**
	 * Get time value chart
	 *
	 * @return the chart
	 */
	public final IChart getTimeValueChart() {
		if (timeValueChart == null && shareValues.size() > 0) {
			getChartShareValueData2();
			timeValueChart = new TimeValueChart(chartShareValueData2, chartShareValueData3, shareValues.get(0).getDate());
			timeValueChart.generate();
		}
		return timeValueChart;
	}

	/**
	 * Get time chart
	 *
	 * @return the chart
	 */
	public final IChart getTimeChart() {
		if (timechart == null && shareValues.size() > 0) {
			Map<Date, Double> map = getChartShareValueData();
			// Modifying first element of each to make it pretty in chart
			timechart = new TimeChart(map, indexes, shareValues.get(0).getDate());
			timechart.generate();
		}
		return timechart;
	}

	/**
	 * Get one account with its name
	 *
	 * @param name the account name
	 * @return the account
	 */
	public final Optional<Account> getAccount(final String name) {
		return accounts == null
			? Optional.empty()
			: accounts.stream().filter(account -> account.getName().equals(name)).findFirst();
	}

	/**
	 * Get one account with its id
	 *
	 * @param id the id
	 * @return the account
	 */
	public final Optional<Account> getAccount(final int id) {
		return accounts == null
			? Optional.empty()
			: accounts.stream().filter(account -> account.getId() == id).findFirst();
	}

	/**
	 * Get the first account created
	 *
	 * @return the account
	 */
	public final Optional<Account> getFirstAccount() {
		return accounts == null
			? Optional.empty()
			: accounts.stream().filter(account -> !account.getDel()).findFirst();
	}

	/**
	 * Get a string containing the list of sector
	 *
	 * @return a string
	 */
	public final String getHTMLSectorByCompanies() {
		final Map<String, List<Equity>> map = getSectorByCompanies();
		return extractHTMLfromMap(map);
	}

	protected Map<String, List<Equity>> getSectorByCompanies() {
		final Map<String, List<Equity>> map = new TreeMap<>();
		List<Equity> companies;
		for (final Equity equity : getEquities()) {
			String sector = equity.getCurrentSector() == null ? Constants.UNKNOWN : equity.getCurrentSector();
			sector = equity.getCompany().getFund() ? Constants.FUND : sector;
			equity.getCompany().setSector(sector);

			companies = map.getOrDefault(equity.getCurrentSector(), new ArrayList<>());
			companies.add(equity);
			map.put(equity.getCurrentSector(), companies);
		}
		return map;
	}

	private String extractHTMLfromMap(final Map<String, List<Equity>> map) {
		final StringBuilder res = new StringBuilder("var companies = [");
		boolean addComma = false;
		for (final Entry<String, List<Equity>> entry : map.entrySet()) {
			if (addComma) {
				res.append(",");
			}
			addComma = true;
			res.append("'");
			entry.getValue().forEach(equity -> res.append(" - ").append(equity.getCurrentName()).append("<br>"));
			res.append("'");
		}
		res.append("];");
		return res.toString();
	}

	/**
	 * Get a string containing the list of capitalization
	 *
	 * @return a string
	 */
	public final String getHTMLCapByCompanies() {
		final Map<String, List<Equity>> map = getGapByCompanies();
		return extractHTMLfromMap(map);
	}

	protected Map<String, List<Equity>> getGapByCompanies() {
		final Map<String, List<Equity>> map = new TreeMap<>();
		List<Equity> companies;
		for (final Equity equity : getEquities()) {
			if (equity.getMarketCapitalizationType().getValue() == null || equity.getCompany().getFund()) {
				equity.setMarketCapitalizationType(MarketCapitalization.UNKNOWN);
			}
			companies = map.getOrDefault(equity.getMarketCapitalizationType().getValue(), new ArrayList<>());
			companies.add(equity);
			map.put(equity.getMarketCapitalizationType().getValue(), companies);
		}
		return map;
	}

	/**
	 * This function generates the share value info (because it muse be calculated each time)
	 */
	private void generateShareValueInfo() {
		List<ShareValue> shareValuess = getShareValues();
		if (shareValuess.size() != 0 && maxShareValue == null && maxShareValueDate == null && currentShareValuesYield == null
			&& currentShareValuesTaxes == null && currentShareValuesVolume == null && currentShareValuesGain == null
			&& currentShareValuesGainPorcentage == null) {
			ShareValue lastShareValue = shareValuess.get(0);
			ShareValue firstShareValue = shareValuess.get(shareValuess.size() - 1);
			double liquidityMov = 0;
			currentShareValuesYield = 0d;
			currentShareValuesTaxes = 0d;
			currentShareValuesVolume = 0d;

			double max = shareValuess.get(0).getShareValue();
			Date date = shareValuess.get(0).getDate();
			for (int i = 0; i <= shareValuess.size() - 1; i++) {
				ShareValue sv = shareValuess.get(i);
				double current = sv.getShareValue();
				final Account account = getAccount(sv.getAccount().getName()).orElseThrow(() -> new NotFoundException(sv.getAccount().getName()));
				if (current > max) {
					max = current;
					date = sv.getDate();
				}
				if (sv.getLiquidityMovement() != null && i != shareValuess.size() - 1) {
					liquidityMov += sv.getLiquidityMovement() * account.getParity();
				}
				if (sv.getYield() != null) {
					currentShareValuesYield += sv.getYield() * account.getParity();
				}
				if (sv.getTaxe() != null) {
					currentShareValuesTaxes += sv.getTaxe() * account.getParity();
				}
				if (sv.getBuy() != null) {
					currentShareValuesVolume += sv.getBuy() * account.getParity();
				}
				if (sv.getSell() != null) {
					currentShareValuesVolume += sv.getBuy() * account.getParity();
				}
			}
			// log.info("Liquidity Movement: " + liquidityMov);
			double depart = liquidityMov + firstShareValue.getPortfolioValue();
			double fin = lastShareValue.getPortfolioValue();
			// log.info("Valeur portefeuille départ: " + depart +
			// " ( Liquidity Movement + valeur de départ soit " + liquidityMov + " + "
			// + firstShareValue.getPortfolioValue() + ")");
			// log.info("Valeur portefeuille arrivée: " + fin);
			// log.info("Dividendes recues: " + currentShareValuesYield +
			// " (déja incluse dans le portefeuille d'arrivée");
			// log.info("Gain (arrivée - départ): " + (fin - depart));
			// log.info("Gain en pourcentage (arrivée - départ) / départ * 100: " + (fin -
			// depart)
			// / depart * 100);

			currentShareValuesGain = fin - depart;

			double last = lastShareValue.getShareValue();
			double first = firstShareValue.getShareValue();
			currentShareValuesGainPorcentage = last * PERCENT / first - PERCENT;
			maxShareValue = max;
			maxShareValueDate = date;
		}
	}

	/**
	 * Get max share value
	 *
	 * @return a double
	 */
	public final Double getMaxShareValue() {
		generateShareValueInfo();
		return maxShareValue;
	}

	/**
	 * Get max share value date
	 *
	 * @return a double
	 */
	public final Date getMaxShareValueDate() {
		generateShareValueInfo();
		return maxShareValueDate != null
			? (Date) maxShareValueDate.clone()
			: null;
	}

	/**
	 * Get current share value yield
	 *
	 * @return a double
	 */
	public final Double getCurrentShareValuesYield() {
		generateShareValueInfo();
		return currentShareValuesYield;
	}

	/**
	 * Get current share value taxes
	 *
	 * @return a double
	 */
	public final Double getCurrentShareValuesTaxes() {
		generateShareValueInfo();
		return currentShareValuesTaxes;
	}

	/**
	 * Get current share values volume
	 *
	 * @return a double
	 */
	public final Double getCurrentShareValuesVolume() {
		generateShareValueInfo();
		return currentShareValuesVolume;
	}

	/**
	 * Get current share values gain
	 *
	 * @return a double
	 */
	public final Double getCurrenShareValuesGain() {
		generateShareValueInfo();
		return currentShareValuesGain;
	}

	/**
	 * Get current share values gain in Percentage
	 *
	 * @return a double
	 */
	public final Double getCurrenShareValuesGainPorcentage() {
		generateShareValueInfo();
		return currentShareValuesGainPorcentage;
	}

	/**
	 * Get a view of the object in json
	 *
	 * @return a JSONObject
	 */
	public final JsonObject getJSONObject() {
		JsonObject json = new JsonObject();
		json.addProperty("id", getId());
		json.addProperty("userId", getUserId());
		json.add(Constants.CURRENCY, getCurrency().getJSONObject());
		json.addProperty(Constants.LIQUIDITY, getLiquidity());
		json.addProperty("lastUpdate", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(getLastCompanyUpdate()));
		json.addProperty("totalValue", getTotalValue());
		json.addProperty("totalGain", getTotalGain());
		json.addProperty("totalPlusMinusValue", getTotalPlusMinusValue());
		json.addProperty("yieldYear", getYieldYear());
		json.addProperty("yieldYearPerc", getYieldYearPerc());
		JsonArray equitiess = new JsonArray();
		for (Equity e : getEquities()) {
			equitiess.add(e.getJSONObject());
		}
		json.add("equities", equitiess);
		JsonArray shareValuess = new JsonArray();
		int i = 0;
		while (i < this.shareValues.size() && i < 11) {
			shareValuess.add(this.shareValues.get(i).getJSONObject());
			i++;
		}
		json.add("shareValues", shareValuess);
		JsonArray accs = new JsonArray();
		for (final Account acc : this.accounts) {
			accs.add(acc.getJSONObject());
		}
		json.add("accounts", accs);
		JsonObject jsonPerf = new JsonObject();
		jsonPerf.addProperty("gain", currentShareValuesGain);
		jsonPerf.addProperty("performance", currentShareValuesGainPorcentage);
		jsonPerf.addProperty("yield", currentShareValuesYield);
		jsonPerf.addProperty("taxes", currentShareValuesTaxes);
		json.add("performance", jsonPerf);

		if (getTimeChart() != null) {
			TimeChart timeChart = (TimeChart) getTimeChart();
			json.addProperty("chartShareValueColors", timeChart.getColors());
			json.addProperty("chartShareValueData", timeChart.getData());
			Date date = timeChart.getDate();
			String formatDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(date);
			json.addProperty("chartShareValueDate", formatDate);
			json.addProperty("chartShareValueDraw", timeChart.getDraw());
		}

		PieChart pieChart = (PieChart) getPieChartSector();
		json.addProperty("chartSectorData", pieChart.getData());
		json.addProperty("chartSectorTitle", pieChart.getTitle());
		json.addProperty("chartSectorDraw", pieChart.getDraw());
		json.addProperty("chartSectorCompanies", getHTMLSectorByCompanies());

		pieChart = (PieChart) getPieChartCap();
		json.addProperty("chartCapData", pieChart.getData());
		json.addProperty("chartCapTitle", pieChart.getTitle());
		json.addProperty("chartCapDraw", pieChart.getDraw());
		json.addProperty("chartCapCompanies", getHTMLCapByCompanies());

		json.addProperty("totalVariation", totalVariation);

		return json;
	}
}
