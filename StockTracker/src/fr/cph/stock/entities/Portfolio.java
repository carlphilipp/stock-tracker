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

public class Portfolio {

//	private static final Logger log = Logger.getLogger(Portfolio.class);

	private final MathContext mathContext = MathContext.DECIMAL32;

	private int id;
	private int userId;
	private Currency currency;
	private Double liquidity;
	private List<Equity> equities;
	private List<ShareValue> shareValues;
	private List<Account> accounts;
	private Map<String, List<Index>> indexes;

	// Calculated
	private Double totalQuantity;
	private Double averageUnitCostPrice;
	private Double averageQuotePrice;
	private Double totalValue;
	private Double totalPlusMinusValue;
	private Double yieldYear;
	private Double yieldYearPerc;
	private Double totalGain;
	private Date lastCompanyUpdate;
	private Double maxShareValue;
	private Date maxShareValueDate;
	private Double currentShareValuesGain;
	private Double currentShareValuesGainPorcentage;
	private Double currentShareValuesYield;
	private Double currentShareValuesTaxes;
	private Double currentShareValuesVolume;

	private Double totalVariation;

	private Map<String, Double> chartSectorData;
	private Map<String, Double> chartCapData;
	private Map<Date, Double> chartShareValueData, chartShareValueData2, chartShareValueData3;
	private IChart timechart, timeValueChart, piechartsector, piechartcap;

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

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public List<Equity> getEquities() {
		return equities;
	}

	public void setEquities(List<Equity> equities) {
		this.equities = equities;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String toString() {
		return id + " - UserId: " + userId + " - Currency - " + currency + "\n" + equities;
	}

	public Double getLiquidity() {
		return liquidity;
	}

	public void setLiquidity(Double liquidity) {
		this.liquidity = liquidity;
	}

	public Double getTotalQuantity() {
		return totalQuantity;
	}

	public Double getAverageUnitCostPrice() {
		return averageUnitCostPrice;
	}

	public Double getAverageQuotePrice() {
		return averageQuotePrice;
	}

	public Double getTotalValue() {
		return totalValue + getLiquidity();
	}

	public Double getTotalPlusMinusValue() {
		return totalPlusMinusValue;
	}

	public Double getTotalPlusMinusValueAbsolute() {
		return Math.abs(totalPlusMinusValue);
	}

	public Double getYieldYear() {
		return yieldYear;
	}

	public Double getTotalGain() {
		return totalGain;
	}

	public Map<String, List<Index>> getIndexes() {
		return indexes;
	}

	public void setIndexes(Map<String, List<Index>> indexes) {
		this.indexes = indexes;
	}

	public Double getYieldYearPerc() {
		return yieldYearPerc;
	}

	public List<ShareValue> getShareValues() {
		return shareValues;
	}

	public void setShareValues(List<ShareValue> shareValues) {
		this.shareValues = shareValues;
	}

	public Date getLastCompanyUpdate() {
		return lastCompanyUpdate;
	}

	public void setLastCompanyUpdate(Date lastCompanyUpdate) {
		this.lastCompanyUpdate = lastCompanyUpdate;
	}

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
			totalVariation = totalValueStart == 0 ? totalValueStart : (((totalValueStart + totalGainToday) / totalValueStart) - 1) * 100;
			averageUnitCostPrice = totalUnitCostPrice / equities.size();
			averageQuotePrice = totalAverageQuotePrice / equities.size();
			totalPlusMinusValue = ((totalValue - totalOriginalValue) / totalOriginalValue) * 100;
			yieldYearPerc = yieldYear / getTotalValue() * 100;
			setLastCompanyUpdate(lastUpdate);
		}
	}

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

	public List<String> getCompaniesYahooIdRealTime() {
		List<String> res = new ArrayList<String>();
		for (Equity e : getEquities()) {
			if (e.getCompany().getRealTime()) {
				res.add(e.getCompany().getYahooId());
			}
		}
		return res;
	}

	public void addIndexes(List<Index> indexes) {
		if (indexes.size() > 0) {
			String index = indexes.get(0).getYahooId();
			this.indexes.put(index, indexes);
		}
	}

	public String getPortfolioReview() {
		StringBuilder sb = new StringBuilder();
		sb.append("<table class=\"shareValueTableDetails\">");
		for (Equity equity : getEquities()) {
			sb.append("<tr><td width=200px><b>" + equity.getCurrentName() + "</b></td><td width=180px>" + equity.getQuantity() + " * "
					+ equity.getCompany().getQuote());
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

	public IChart getPieChartSector() {
		if (piechartsector == null) {
			Map<String, Double> map = getChartSectorData();
			piechartsector = new PieChart(map);
			piechartsector.generate();
		}
		return piechartsector;
	}

	public IChart getPieChartCap() {
		if (piechartcap == null) {
			Map<String, Double> map = getChartCapData();
			piechartcap = new PieChart(map);
			piechartcap.generate();
		}
		return piechartcap;
	}

	public IChart getTimeValueChart() {
		if (timeValueChart == null && shareValues.size() > 0) {
			getChartShareValueData2();
			timeValueChart = new TimeValueChart(chartShareValueData2, chartShareValueData3, shareValues.get(0).getDate());
			timeValueChart.generate();

		}
		return timeValueChart;
	}

	public IChart getTimeChart() {
		if (timechart == null && shareValues.size() > 0) {
			Map<Date, Double> map = getChartShareValueData();
			// Modifying first element of each to make it pretty in chart
			timechart = new TimeChart(map, indexes, shareValues.get(0).getDate());
			timechart.generate();
		}
		return timechart;
	}

	public List<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}

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

	public Double getMaxShareValue() {
		generateShareValueInfo();
		return maxShareValue;
	}

	public Date getMaxShareValueDate() {
		generateShareValueInfo();
		return maxShareValueDate;
	}

	public Double getCurrentShareValuesYield() {
		generateShareValueInfo();
		return currentShareValuesYield;
	}

	public Double getCurrentShareValuesTaxes() {
		generateShareValueInfo();
		return currentShareValuesTaxes;
	}

	public Double getCurrentShareValuesVolume() {
		generateShareValueInfo();
		return currentShareValuesVolume;
	}

	public Double getCurrenShareValuesGain() {
		generateShareValueInfo();
		return currentShareValuesGain;
	}

	public Double getCurrenShareValuesGainPorcentage() {
		generateShareValueInfo();
		return currentShareValuesGainPorcentage;
	}

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

	public Double getTotalVariation() {
		return totalVariation;
	}

}
