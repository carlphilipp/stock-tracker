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

package fr.cph.stock.entities;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import fr.cph.stock.entities.chart.IChart;
import fr.cph.stock.entities.chart.PieChart;
import fr.cph.stock.entities.chart.TimeChart;
import fr.cph.stock.entities.chart.TimeValueChart;
import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.enumtype.MarketCapitalization;

/**
 * This class represents a portofolio that belongs to a user
 * 
 * @author Carl-Philipp Harmant
 * 
 */
public class Portfolio {

	// private static final Logger log = Logger.getLogger(Portfolio.class);

	/** Precision of calculation **/
	private final MathContext mathContext = MathContext.DECIMAL32;
	/** Id **/
	private int id;
	/** User Id that get this portfolio **/
	private int userId;
	/** Currency **/
	private Currency currency;
	/** Liquidity **/
	private Double liquidity;
	/** List of equities **/
	private List<Equity> equities;
	/** List of share value **/
	private List<ShareValue> shareValues;
	/** List of accounts **/
	private List<Account> accounts;
	/** Map representing the indexes **/
	private Map<String, List<Index>> indexes;

	// Calculated
	/** Total quantity **/
	private Double totalQuantity;
	/** Average unit cost price **/
	private Double averageUnitCostPrice;
	/** Average quote price **/
	private Double averageQuotePrice;
	/** Total value **/
	private Double totalValue;
	/** Total plus minus value **/
	private Double totalPlusMinusValue;
	/** Yield per year **/
	private Double yieldYear;
	/** Yield per year in percentage **/
	private Double yieldYearPerc;
	/** Total gain **/
	private Double totalGain;
	/** Last company update **/
	private Date lastCompanyUpdate;
	/** Best performance of the share value **/
	private Double maxShareValue;
	/** Date of the best performance **/
	private Date maxShareValueDate;
	/** Current share value gain **/
	private Double currentShareValuesGain;
	/** Current share value gain in percentage **/
	private Double currentShareValuesGainPorcentage;
	/** Current share value yield **/
	private Double currentShareValuesYield;
	/** Current share value taxes **/
	private Double currentShareValuesTaxes;
	/** Current share value taxes **/
	private Double currentShareValuesVolume;
	/** Total variation **/
	private Double totalVariation;
	/** Map that represents data of sector chart **/
	private Map<String, Double> chartSectorData;
	/** Map that represents data of capitalization chart **/
	private Map<String, Double> chartCapData;
	/** Maps that represents data of share value (user + cac40 + s&p500 **/
	private Map<Date, Double> chartShareValueData, chartShareValueData2, chartShareValueData3;
	/** Chart objects **/
	private IChart timechart, timeValueChart, piechartsector, piechartcap;

	/**
	 * Constructor
	 */
	public Portfolio() {
		totalQuantity = new Double(0);
		averageUnitCostPrice = new Double(0);
		averageQuotePrice = new Double(0);
		totalValue = new Double(0);
		totalPlusMinusValue = new Double(0);
		yieldYear = new Double(0);
		totalGain = new Double(0);
		yieldYearPerc = new Double(0);
		indexes = new HashMap<String, List<Index>>();
	}

	/**
	 * Get currency
	 * 
	 * @return the currency
	 */
	public Currency getCurrency() {
		return currency;
	}

	/**
	 * Set the currency
	 * 
	 * @param currency
	 *            the currency
	 */
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	/**
	 * Get equities
	 * 
	 * @return the equities
	 */
	public List<Equity> getEquities() {
		return equities;
	}

	/**
	 * Set equities
	 * 
	 * @param equities
	 *            the equities
	 */
	public void setEquities(List<Equity> equities) {
		this.equities = equities;
	}

	/**
	 * Get id
	 * 
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Set id
	 * 
	 * @param id
	 *            the id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Get user id
	 * 
	 * @return the user id
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * Set user id
	 * 
	 * @param userId
	 *            the user id
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return id + " - UserId: " + userId + " - Currency - " + currency + "\n" + equities;
	}

	/**
	 * Get liquidity
	 * 
	 * @return the liquidity
	 */
	public Double getLiquidity() {
		return liquidity;
	}

	/**
	 * Set liquidity
	 * 
	 * @param liquidity
	 *            the liquidity
	 */
	public void setLiquidity(Double liquidity) {
		this.liquidity = liquidity;
	}

	/**
	 * Get total quantity
	 * 
	 * @return the total quantity
	 */
	public Double getTotalQuantity() {
		return totalQuantity;
	}

	/**
	 * 
	 * @return
	 */
	public Double getAverageUnitCostPrice() {
		return averageUnitCostPrice;
	}

	/**
	 * Get average quote price
	 * 
	 * @return the average quote price
	 */
	public Double getAverageQuotePrice() {
		return averageQuotePrice;
	}

	/**
	 * Get total value
	 * 
	 * @return the total value
	 */
	public Double getTotalValue() {
		return totalValue + getLiquidity();
	}

	/**
	 * Get total plus minus value
	 * 
	 * @return the total plus minus value
	 */
	public Double getTotalPlusMinusValue() {
		return totalPlusMinusValue;
	}

	/**
	 * Get total plus minus value in absolute
	 * 
	 * @return the total plus minus value in absolute
	 */
	public Double getTotalPlusMinusValueAbsolute() {
		return Math.abs(totalPlusMinusValue);
	}

	/**
	 * Get yield year
	 * 
	 * @return the yield year
	 */
	public Double getYieldYear() {
		return yieldYear;
	}

	/**
	 * Get total gain
	 * 
	 * @return the total gain
	 */
	public Double getTotalGain() {
		return totalGain;
	}

	/**
	 * Get a map containing all the data indexes
	 * 
	 * @return a map with (Index name) => List of Index
	 */
	public Map<String, List<Index>> getIndexes() {
		return indexes;
	}

	/**
	 * Set a map containing all the data indexes
	 * 
	 * @param indexes
	 *            a map with (Index name) => List of Index
	 */
	public void setIndexes(Map<String, List<Index>> indexes) {
		this.indexes = indexes;
	}

	/**
	 * Get Yield per year in percentage
	 * 
	 * @return
	 */
	public Double getYieldYearPerc() {
		return yieldYearPerc;
	}

	/**
	 * Get a list of share value
	 * 
	 * @return a list of share value
	 */
	public List<ShareValue> getShareValues() {
		return shareValues;
	}

	/**
	 * Set the share value
	 * 
	 * @param shareValues
	 *            a list of share value
	 */
	public void setShareValues(List<ShareValue> shareValues) {
		this.shareValues = shareValues;
	}

	/**
	 * Get last company update date
	 * 
	 * @return a date
	 */
	public Date getLastCompanyUpdate() {
		return lastCompanyUpdate;
	}

	/**
	 * Set last company ypdate date
	 * 
	 * @param lastCompanyUpdate
	 *            the date
	 */
	public void setLastCompanyUpdate(Date lastCompanyUpdate) {
		this.lastCompanyUpdate = lastCompanyUpdate;
	}

	/**
	 * This function generates all calculated field
	 */
	public void compute() {
		Double totalUnitCostPrice = new Double(0);
		Double totalAverageQuotePrice = new Double(0);
		Double totalOriginalValue = new Double(0);
		totalVariation = new Double(0);
		double totalValueStart = 0;
		double totalGainToday = 0;
		Date lastUpdate = null;
		if (equities != null) {
			for (Equity equity : equities) {
				totalQuantity += equity.getQuantity();
				totalUnitCostPrice += equity.getUnitCostPrice();
				totalAverageQuotePrice += equity.getCompany().getQuote() * equity.getParity();
				totalValue += equity.getValue();
				totalOriginalValue += equity.getQuantity() * equity.getUnitCostPrice() * equity.getParity();
				yieldYear += equity.getYieldYear();
				totalGain += equity.getPlusMinusUnitCostPriceValue();
				if (equity.getCompany().getRealTime()) {
					if (lastUpdate == null) {
						lastUpdate = equity.getCompany().getLastUpdate();
					} else {
						if (lastUpdate.after(equity.getCompany().getLastUpdate())) {
							lastUpdate = equity.getCompany().getLastUpdate();
						}
					}
				}
				if (equity.getCompany().getChange() != null) {
					double valueStart = equity.getValue() / ((equity.getCompany().getChange() / 100) + 1);
					totalValueStart += valueStart;
					totalGainToday += valueStart * equity.getCompany().getChange() / 100;
				}

			}
			totalVariation = totalValueStart == 0 ? totalValueStart
					: (((totalValueStart + totalGainToday) / totalValueStart) - 1) * 100;
			averageUnitCostPrice = totalUnitCostPrice / equities.size();
			averageQuotePrice = totalAverageQuotePrice / equities.size();
			totalPlusMinusValue = ((totalValue - totalOriginalValue) / totalOriginalValue) * 100;
			yieldYearPerc = yieldYear / getTotalValue() * 100;
			setLastCompanyUpdate(lastUpdate);
		}
	}

	/**
	 * Get chart sector data
	 * 
	 * @return a map
	 */
	protected Map<String, Double> getChartSectorData() {
		if (chartSectorData == null) {
			Map<String, Double> data = new HashMap<String, Double>();
			for (Equity e : getEquities()) {
				if (e.getCompany().getFund()) {
					if (data.containsKey("Fund")) {
						Double d = data.get("Fund");
						d += e.getValue();
						data.put("Fund", d);
					} else {
						data.put("Fund", e.getValue());
					}
				} else {
					String sector = e.getCurrentSector();
					if (sector == null) {
						if (data.containsKey("Unknown")) {
							Double d = data.get("Unknown");
							d += e.getValue();
							data.put("Unknown", d);
						} else {
							data.put("Unknown", e.getValue());
						}
					} else {
						if (data.containsKey(sector)) {
							Double d = data.get(sector);
							d += e.getValue();
							data.put(sector, d);
						} else {
							data.put(sector, e.getValue());
						}
					}
				}
			}
			chartSectorData = new TreeMap<String, Double>();
			chartSectorData.putAll(data);
		}
		return chartSectorData;
	}

	/**
	 * Get chart share value data
	 * 
	 * @return a map
	 */
	protected Map<Date, Double> getChartShareValueData() {
		Map<Date, Double> data = new HashMap<Date, Double>();
		List<ShareValue> shareValues = getShareValues();
		int max = shareValues.size();
		double base = shareValues.get(max - 1).getShareValue();
		for (int i = max - 1; i != -1; i--) {
			ShareValue temp = shareValues.get(i);
			Double value = temp.getShareValue() * 100 / base;
			data.put(temp.getDate(), value);
		}
		chartShareValueData = new TreeMap<Date, Double>();
		chartShareValueData.putAll(data);
		return chartShareValueData;
	}

	/**
	 * Generate chart share value data
	 */
	protected void getChartShareValueData2() {
		if (chartShareValueData2 == null && chartShareValueData3 == null) {
			Map<Date, Double> data = new HashMap<Date, Double>();
			Map<Date, Double> data2 = new HashMap<Date, Double>();
			List<ShareValue> shareValues = getShareValues();
			int max = shareValues.size();
			for (int i = max - 1; i != -1; i--) {
				ShareValue temp = shareValues.get(i);
				Double value = temp.getPortfolioValue();
				Double liquidity = temp.getLiquidities();
				data.put(temp.getDate(), value);
				if (liquidity != null) {
					data2.put(temp.getDate(), liquidity);
				}
			}
			chartShareValueData2 = new TreeMap<Date, Double>();
			chartShareValueData2.putAll(data);
			chartShareValueData3 = new TreeMap<Date, Double>();
			chartShareValueData3.putAll(data2);
		}
	}

	/**
	 * Get chart capitalization data
	 * 
	 * @return a map
	 */
	protected Map<String, Double> getChartCapData() {
		if (chartCapData == null) {
			Map<String, Double> data = new HashMap<String, Double>();
			for (Equity e : getEquities()) {
				if (!e.getCompany().getFund()) {
					MarketCapitalization marketCap = e.getMarketCapitalizationType();
					if (marketCap == null) {
						if (data.containsKey("Unknown")) {
							Double d = data.get("Unknown");
							d += e.getValue();
							data.put("Unknown", d);
						} else {
							data.put("Unknown", e.getValue());
						}
					} else {
						if (data.containsKey(marketCap.getValue())) {
							Double d = data.get(marketCap.getValue());
							d += e.getValue();
							data.put(marketCap.getValue(), d);
						} else {
							data.put(marketCap.getValue(), e.getValue());
						}
					}
				} else {
					if (data.containsKey("Unknown")) {
						Double d = data.get("Unknown");
						d += e.getValue();
						data.put("Unknown", d);
					} else {
						data.put("Unknown", e.getValue());
					}
				}
			}
			chartCapData = new TreeMap<String, Double>();
			chartCapData.putAll(data);
		}
		return chartCapData;
	}

	/**
	 * Get a list of yahoo id representing equities that do not have real time data
	 * 
	 * @return a list of yahoo id
	 */
	public List<String> getCompaniesYahooIdRealTime() {
		List<String> res = new ArrayList<String>();
		for (Equity e : getEquities()) {
			if (e.getCompany().getRealTime()) {
				res.add(e.getCompany().getYahooId());
			}
		}
		return res;
	}

	/**
	 * Add indexes
	 * 
	 * @param indexes
	 *            a list of indexes
	 */
	public void addIndexes(List<Index> indexes) {
		if (indexes.size() > 0) {
			String index = indexes.get(0).getYahooId();
			this.indexes.put(index, indexes);
		}
	}

	/**
	 * Get portfolio review. An horrible function.
	 * 
	 * @return
	 */
	public String getPortfolioReview() {
		StringBuilder sb = new StringBuilder();
		sb.append("<table class=\"shareValueTableDetails\">");
		for (Equity equity : getEquities()) {
			sb.append("<tr><td width=200px><b>" + equity.getCurrentName() + "</b></td><td width=180px>" + equity.getQuantity()
					+ " * " + equity.getCompany().getQuote());
			if (equity.getCompany().getCurrency() != getCurrency()) {
				sb.append(" * " + getCurrency().getParity(equity.getCompany().getCurrency()));
			}
			sb.append("</td><td>" + equity.getValue() + " (" + getCurrency().getCode() + ")</td></tr>");
		}
		sb.append("<tr><td colspan=3><b>Liquidity:</b> " + getLiquidity() + " (" + getCurrency().getCode() + ")</td></tr>");
		sb.append("<tr><td colspan=3><b>Total:</b> " + new BigDecimal(getTotalValue(), mathContext).doubleValue() + " ("
				+ getCurrency().getCode() + ")</td></tr>");
		sb.append("</table>");
		return sb.toString();
	}

	/**
	 * Get pie chart sector
	 * 
	 * @return
	 */
	public IChart getPieChartSector() {
		if (piechartsector == null) {
			Map<String, Double> map = getChartSectorData();
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
	public IChart getPieChartCap() {
		if (piechartcap == null) {
			Map<String, Double> map = getChartCapData();
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
	public IChart getTimeValueChart() {
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
	public IChart getTimeChart() {
		if (timechart == null && shareValues.size() > 0) {
			Map<Date, Double> map = getChartShareValueData();
			// Modifying first element of each to make it pretty in chart
			timechart = new TimeChart(map, indexes, shareValues.get(0).getDate());
			timechart.generate();
		}
		return timechart;
	}

	/**
	 * Get List of accounts
	 * 
	 * @return the list of accounts
	 */
	public List<Account> getAccounts() {
		return accounts;
	}

	/**
	 * Set list of accounts
	 * 
	 * @param accounts
	 *            the list of accounts
	 */
	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}

	/**
	 * Get one account with its name
	 * 
	 * @param name
	 *            the account name
	 * @return the account
	 */
	public Account getAccount(String name) {
		Account res = null;
		for (Account account : getAccounts()) {
			if (account.getName().equals(name)) {
				res = account;
				break;
			}
		}
		return res;
	}

	/**
	 * Get one account with its id
	 * 
	 * @param id
	 * @return the account
	 */
	public Account getAccount(int id) {
		Account res = null;
		for (Account account : getAccounts()) {
			if (account.getId() == id) {
				res = account;
				break;
			}
		}
		return res;
	}

	/**
	 * Get the first account created
	 * 
	 * @return the account
	 */
	public Account getFirstAccount() {
		Account res = null;
		for (Account account : getAccounts()) {
			if (!account.getDel()) {
				res = account;
				break;
			}
		}
		return res;
	}

	/**
	 * Get a string containing the list of sector
	 * 
	 * @return a string
	 */
	public String getSectorCompanies() {
		StringBuilder res = new StringBuilder();
		Map<String, List<Equity>> map = new HashMap<String, List<Equity>>();
		Company company = null;
		List<Equity> companies = null;
		for (Equity e : getEquities()) {
			company = e.getCompany();
			if (e.getCurrentSector() == null) {
				company.setSector("Unknown");
			}
			if (company.getFund()) {
				company.setSector("Fund");
			}
			if (!map.containsKey(e.getCurrentSector())) {
				companies = new ArrayList<Equity>();
			} else {
				companies = map.get(e.getCurrentSector());
			}
			companies.add(e);
			map.put(e.getCurrentSector(), companies);
		}
		TreeMap<String, List<Equity>> treeMap = new TreeMap<String, List<Equity>>();
		treeMap.putAll(map);
		res.append("var companies = [");
		int i = 0;
		for (Entry<String, List<Equity>> entry : treeMap.entrySet()) {
			if (i != 0) {
				res.append(",");
			}
			List<Equity> list = entry.getValue();
			// res.append("'<ul>");
			res.append("'");
			for (Equity e : list) {
				// res.append("<li>" + e.getCurrentName() + "</li>");
				res.append(" - " + e.getCurrentName() + "<br>");
			}
			// res.append("</ul>'");
			res.append("'");
			i++;
		}
		res.append("];");
		return res.toString();
	}

	/**
	 * Get a string containing the list of capitalization
	 * 
	 * @return a string
	 */
	public String getCapCompanies() {
		StringBuilder res = new StringBuilder();
		Map<String, List<Equity>> map = new HashMap<String, List<Equity>>();
		List<Equity> companies = null;
		for (Equity e : getEquities()) {
			if (e.getMarketCapitalizationType().getValue() == null || e.getCompany().getFund()) {
				e.setMarketCapitalizationType(MarketCapitalization.UNKNOWN);
			}
			if (!map.containsKey(e.getMarketCapitalizationType().getValue())) {
				companies = new ArrayList<Equity>();
			} else {
				companies = map.get(e.getMarketCapitalizationType().getValue());
			}
			companies.add(e);
			map.put(e.getMarketCapitalizationType().getValue(), companies);
		}
		TreeMap<String, List<Equity>> treeMap = new TreeMap<String, List<Equity>>();
		treeMap.putAll(map);
		res.append("var companies = [");
		int i = 0;
		for (Entry<String, List<Equity>> entry : treeMap.entrySet()) {
			if (i != 0) {
				res.append(",");
			}
			List<Equity> list = entry.getValue();
			res.append("'");
			for (Equity e : list) {
				res.append(" - " + e.getCurrentName() + "<br>");
			}
			res.append("'");
			i++;
		}
		res.append("];");
		return res.toString();
	}

	/**
	 * This function generates the share value info (because it muse be calculated each time)
	 */
	private void generateShareValueInfo() {
		List<ShareValue> shareValues = getShareValues();
		if (shareValues.size() != 0 && maxShareValue == null && maxShareValueDate == null && currentShareValuesYield == null
				&& currentShareValuesTaxes == null && currentShareValuesVolume == null && currentShareValuesGain == null
				&& currentShareValuesGainPorcentage == null) {
			ShareValue lastShareValue = shareValues.get(0);
			ShareValue firstShareValue = shareValues.get(shareValues.size() - 1);
			double liquidityMov = 0;
			currentShareValuesYield = new Double(0);
			currentShareValuesTaxes = new Double(0);
			currentShareValuesVolume = new Double(0);

			double max = shareValues.get(0).getShareValue();
			Date date = shareValues.get(0).getDate();
			for (int i = 0; i <= shareValues.size() - 1; i++) {
				ShareValue sv = shareValues.get(i);
				double current = sv.getShareValue();
				Account account = getAccount(sv.getAccount().getName());
				if (current > max) {
					max = current;
					date = sv.getDate();
				}
				if (sv.getLiquidityMovement() != null && i != shareValues.size() - 1) {
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
			// log.info("Gain en pourcentage (arrivée - départ) / départ * 100: " + (fin - depart)
			// / depart * 100);

			currentShareValuesGain = fin - depart;

			double last = lastShareValue.getShareValue();
			double first = firstShareValue.getShareValue();
			currentShareValuesGainPorcentage = last * 100 / first - 100;
			maxShareValue = max;
			maxShareValueDate = date;
		}
	}

	/**
	 * Get max share value
	 * 
	 * @return a double
	 */
	public Double getMaxShareValue() {
		generateShareValueInfo();
		return maxShareValue;
	}

	/**
	 * Get max share value date
	 * 
	 * @return a double
	 */
	public Date getMaxShareValueDate() {
		generateShareValueInfo();
		return maxShareValueDate;
	}

	/**
	 * Get current share value yield
	 * 
	 * @return a double
	 */
	public Double getCurrentShareValuesYield() {
		generateShareValueInfo();
		return currentShareValuesYield;
	}

	/**
	 * Get current share value taxes
	 * 
	 * @return a double
	 */
	public Double getCurrentShareValuesTaxes() {
		generateShareValueInfo();
		return currentShareValuesTaxes;
	}

	/**
	 * Get current share values volume
	 * 
	 * @return a double
	 */
	public Double getCurrentShareValuesVolume() {
		generateShareValueInfo();
		return currentShareValuesVolume;
	}

	/**
	 * Get current share values gain
	 * 
	 * @return a double
	 */
	public Double getCurrenShareValuesGain() {
		generateShareValueInfo();
		return currentShareValuesGain;
	}

	/**
	 * Get current share values gain in Percentage
	 * 
	 * @return a double
	 */
	public Double getCurrenShareValuesGainPorcentage() {
		generateShareValueInfo();
		return currentShareValuesGainPorcentage;
	}

	/**
	 * Get a view of the object in json
	 * 
	 * @return a JSONObject
	 */
	public JSONObject getJSONObject() {
		JSONObject json = new JSONObject();
		json.put("id", getId());
		json.put("userId", getUserId());
		json.put("currency", getCurrency().getJSONObject());
		json.put("liquidity", getLiquidity());
		json.put("lastUpdate", getLastCompanyUpdate());
		json.put("totalValue", getTotalValue());
		json.put("totalGain", getTotalGain());
		json.put("totalPlusMinusValue", getTotalPlusMinusValue());
		json.put("yieldYear", getYieldYear());
		json.put("yieldYearPerc", getYieldYearPerc());
		JSONArray equities = new JSONArray();
		for (Equity e : getEquities()) {
			equities.add(e.getJSONObject());
		}
		json.put("equities", equities);
		JSONArray shareValues = new JSONArray();
		for (int i = 0; i < 20; i++) {
			shareValues.add(this.shareValues.get(i).getJSONObject());
		}
		json.put("shareValues", shareValues);
		JSONArray accounts = new JSONArray();
		for (Account acc : this.accounts) {
			accounts.add(acc.getJSONObject());
		}
		json.put("accounts", accounts);
		JSONObject jsonPerf = new JSONObject();
		jsonPerf.accumulate("gain", currentShareValuesGain);
		jsonPerf.accumulate("performance", currentShareValuesGainPorcentage);
		jsonPerf.accumulate("yield", currentShareValuesYield);
		jsonPerf.accumulate("taxes", currentShareValuesTaxes);
		json.put("performance", jsonPerf);

		json.put("chartShareValueColors", ((TimeChart) getTimeChart()).getColors());
		json.put("chartShareValueData", ((TimeChart) getTimeChart()).getData());
		Date date = ((TimeChart) getTimeChart()).getDate();
		String formatDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(date);
		json.put("chartShareValueDate", formatDate);
		json.put("chartShareValueDraw", ((TimeChart) getTimeChart()).getDraw());

		PieChart pieChart = (PieChart) getPieChartSector();
		json.put("chartSectorData", pieChart.getData());
		json.put("chartSectorTitle", pieChart.getTitle());
		json.put("chartSectorDraw", pieChart.getDraw());
		json.put("chartSectorCompanies", getSectorCompanies());

		pieChart = (PieChart) getPieChartCap();
		json.put("chartCapData", pieChart.getData());
		json.put("chartCapTitle", pieChart.getTitle());
		json.put("chartCapDraw", pieChart.getDraw());
		json.put("chartCapCompanies", getCapCompanies());

		json.put("totalVariation", totalVariation);

		return json;
	}

	/**
	 * Get total variation
	 * 
	 * @return the total variation
	 */
	public Double getTotalVariation() {
		return totalVariation;
	}

}
