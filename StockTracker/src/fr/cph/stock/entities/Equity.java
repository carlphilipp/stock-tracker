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
import java.util.Date;

import net.sf.json.JSONObject;
import fr.cph.stock.enumtype.Frequency;
import fr.cph.stock.enumtype.MarketCapitalization;
import fr.cph.stock.enumtype.Month;

public class Equity implements Comparable<Equity>{

//	private static final Logger log = Logger.getLogger(Equity.class);

	private final MathContext mathContext = MathContext.DECIMAL32;

	private Company company;

	private int id;
	private int portfolioId;
	private int companyId;
	private String namePersonal;
	private String sectorPersonal;
	private String industryPersonal;
	private String marketCapPersonal;
	private Double quantity;
	private Double unitCostPrice;
	private Double parity;
	private Double yieldPersonal;
	private Double parityPersonal;
	private Double stopLossLocal;
	private Double objectivLocal;
	private Frequency yieldFrequency;
	private Month yieldMonth;
	private Date lastUpdate;

	// calculated
	private Double plusMinusValue;
	private Double value;
	private Double originalValue;
	private Double yieldYear;
	private Double yieldUnitCostPrice;
	private Double plusMinusUnitCostPriceValue;
	private Double gapStopLossLocal;
	private Double gapObjectivLocal;
	private BigDecimal marketCapitalizationLocal;
	private MarketCapitalization marketCapitalizationType;
	private Double currentParity;
	//private Double variation;

	public Double getGapObjectivLocal() {
		if (gapObjectivLocal == null) {
			if (getObjectivLocal() != null) {
				gapObjectivLocal = (getObjectivLocal() / company.getQuote() - 1) * 100;
			}
		}
		return gapObjectivLocal;
	}

	public Double getValue() {
		if (value == null) {
			value = quantity * company.getQuote() * getParity();
			value = (new BigDecimal(value, mathContext)).doubleValue();
		}
		return value;
	}

	public Double getPlusMinusUnitCostPriceValue() {
		if (plusMinusUnitCostPriceValue == null) {
			plusMinusUnitCostPriceValue = getValue() - (unitCostPrice * getCurrentParity() * quantity);
		}
		return plusMinusUnitCostPriceValue;
	}

	public Double getYieldYear() {
		if (yieldYear == null) {
			yieldYear = getCurrentPruYield() * getValue() / 100;
		}
		return yieldYear.doubleValue();
	}

	public Double getPlusMinusValue() {
		if (plusMinusValue == null) {
			plusMinusValue = (getValue() - getOriginalValue()) / getOriginalValue() * 100;
		}
		return plusMinusValue;
	}

	public Double getYieldUnitCostPrice() {
		if (yieldUnitCostPrice == null) {
			yieldUnitCostPrice = getCurrentPruYield();
			// yieldUnitCostPrice = getCurrentYield() * company.getQuote() * getParity() /
			// getUnitCostPrice();
		}
		return yieldUnitCostPrice.doubleValue();
	}

	public Double getGapStopLossLocal() {
		if (gapStopLossLocal == null) {
			if (getStopLossLocal() != null) {
				gapStopLossLocal = (company.getQuote() / getStopLossLocal() - 1) * 100;
			}
		}
		return gapStopLossLocal;
	}

	public Double getGapObjectiv() {
		if (gapObjectivLocal == null) {
			gapObjectivLocal = (getObjectivLocal() / company.getQuote() - 1) * 100;
		}
		return gapObjectivLocal;
	}

	public void setGapObjectivLocal(Double gapObjectivLocal) {
		this.gapObjectivLocal = gapObjectivLocal;
	}

	public BigDecimal getMarketCapitalizationLocal() {
		if (marketCapitalizationLocal == null) {
			BigDecimal big = getMarketCapitalizationInBigDecimal();
			if (big == null) {
				marketCapitalizationLocal = null;
			} else {
				marketCapitalizationLocal = big.multiply(new BigDecimal(getParity()));
			}
		}
		return marketCapitalizationLocal;
	}

	public MarketCapitalization getMarketCapitalizationType() {
		if (marketCapitalizationType == null) {
			BigDecimal cap = getMarketCapitalizationLocal();
			if (cap != null) {
				if (cap.compareTo(new BigDecimal(200000000.0)) == 1) {
					marketCapitalizationType = MarketCapitalization.MEGA_CAP;
				} else if (cap.compareTo(new BigDecimal(10000000.0)) == 1) {
					marketCapitalizationType = MarketCapitalization.LARGE_CAP;
				} else if (cap.compareTo(new BigDecimal(2000000.0)) == 1) {
					marketCapitalizationType = MarketCapitalization.MID_CAP;
				} else if (cap.compareTo(new BigDecimal(250.0)) == 1) {
					marketCapitalizationType = MarketCapitalization.SMALL_CAP;
				} else if (cap.compareTo(new BigDecimal(50.0)) == 1) {
					marketCapitalizationType = MarketCapitalization.MICRO_CAP;
				} else {
					marketCapitalizationType = MarketCapitalization.NANO_CAP;
				}
			} else {
				marketCapitalizationType = MarketCapitalization.UNKNOWN;
			}
		}
		return marketCapitalizationType;
	}

	public BigDecimal getMarketCapitalizationInBigDecimal() {
		if (getCurrentMarketCap() != null) {
			String str = new String(getCurrentMarketCap());
			StringBuilder sb = new StringBuilder();
			char c = str.charAt(str.length() - 1);
			if (c == 'B') {
				str = str.replaceAll("B", "");
				int point = str.indexOf('.');
				int reverse = str.length() - point - 1;
				sb.append(str.replaceAll("\\.", ""));
				for (int i = 0; i < 6 - reverse; i++) {
					sb.append("0");
				}
			} else {
				if (c == 'M') {
					str = str.replaceAll("M", "");
					int point = str.indexOf('.');
					int reverse = str.length() - point - 1;
					sb.append(str.replaceAll("\\.", ""));
					for (int i = 0; i < 3 - reverse; i++) {
						sb.append("0");
					}
				}
			}
			return new BigDecimal(sb.toString());
		} else {
			return null;
		}

	}

	// Getter & Setter
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public Double getUnitCostPrice() {
		return unitCostPrice;
	}

	public void setUnitCostPrice(Double unitCostPrice) {
		this.unitCostPrice = unitCostPrice;
	}

	public double getParity() {
		return parity;
	}

	public void setParity(Double parity) {
		this.parity = parity;
	}

	public Double getYieldPersonal() {
		return yieldPersonal;
	}

	public void setYieldPersonal(Double yieldPersonal) {
		this.yieldPersonal = yieldPersonal;
	}

	public Double getStopLossLocal() {
		return stopLossLocal;
	}

	public void setStopLossLocal(Double stopLossLocal) {
		this.stopLossLocal = stopLossLocal;
	}

	public Double getObjectivLocal() {
		return objectivLocal;
	}

	public void setObjectivLocal(Double objectivLocal) {
		this.objectivLocal = objectivLocal;
	}

	public Frequency getYieldFrequency() {
		return yieldFrequency;
	}

	public void setYieldFrequency(Frequency yieldFrequency) {
		this.yieldFrequency = yieldFrequency;
	}

	public Month getYieldMonth() {
		return yieldMonth;
	}

	public void setYieldMonth(Month yieldMonth) {
		this.yieldMonth = yieldMonth;
	}

	public void setid(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	public int getPortfolioId() {
		return portfolioId;
	}

	public void setPortfolioId(int portfolioId) {
		this.portfolioId = portfolioId;
	}

	public String getSectorPersonal() {
		return sectorPersonal;
	}

	public String getCurrentSector() {
		String sector;
		if (getSectorPersonal() == null) {
			sector = company.getSector();
		} else {
			sector = getSectorPersonal();
		}
		return sector;
	}

	public void setSectorPersonal(String sectorPersonal) {
		this.sectorPersonal = sectorPersonal;
	}

	public String getIndustryPersonal() {
		return industryPersonal;
	}

	public String getCurrentIndustry() {
		String industry;
		if (getIndustryPersonal() == null) {
			industry = company.getIndustry();
		} else {
			industry = getIndustryPersonal();
		}
		return industry;
	}

	public void setIndustryPersonal(String industryPersonal) {
		this.industryPersonal = industryPersonal;
	}

	public String getMarketCapPersonal() {
		return marketCapPersonal;
	}

	public String getCurrentMarketCap() {
		String marketCap;
		if (getMarketCapPersonal() == null) {
			marketCap = company.getMarketCapitalization();
		} else {
			marketCap = getMarketCapPersonal();
		}
		return marketCap;
	}

	public void setMarketCapPersonal(String marketCapPersonal) {
		this.marketCapPersonal = marketCapPersonal;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Equity " + id + " - portfolioId: " + portfolioId + "\n");
		sb.append("Quantity: " + quantity + " -  Unit Cost Price: " + unitCostPrice + "\n");
		sb.append(company);
		return sb.toString();
	}

	public void setMarketCapitalizationType(MarketCapitalization marketCapitalizationType) {
		this.marketCapitalizationType = marketCapitalizationType;
	}

	public Double getOriginalValue() {
		if (originalValue == null) {
			originalValue = getQuantity() * getUnitCostPrice() * getCurrentParity();
		}
		return originalValue;
	}

	public void setOriginalValue(Double originalValue) {
		this.originalValue = originalValue;
	}

	public Double getCurrentPruYield() {
		double res;
		if (getYieldPersonal() != null) {
			if (getUnitCostPrice() == 0) {
				res = 0;
			} else {
				res = getYieldPersonal() / getUnitCostPrice() * 100;
			}
		} else {
			if (getUnitCostPrice() == 0) {
				res = 0;
			} else {
				res = getCompany().getYield() / getUnitCostPrice() * 100;
			}
		}
		res = (new BigDecimal(res, mathContext)).doubleValue();
		return res;
	}

	public Double getCurrentYield() {
		double res;
		if (getYieldPersonal() != null) {
			res = getYieldPersonal() / getCompany().getQuote() * 100;
		} else {
			res = getCompany().getYield() / getCompany().getQuote() * 100;
		}
		res = (new BigDecimal(res, mathContext)).doubleValue();
		return res;
	}

	public Double getParityPersonal() {
		return parityPersonal;
	}

	public void setParityPersonal(Double parityPersonal) {
		this.parityPersonal = parityPersonal;
	}

	public Double getCurrentParity() {
		if (getParityPersonal() != null) {
			currentParity = getParityPersonal();
		} else {
			currentParity = getParity();
		}
		currentParity = (new BigDecimal(currentParity, mathContext)).doubleValue();
		return currentParity;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public String getNamePersonal() {
		return namePersonal;
	}

	public void setNamePersonal(String namePersonal) {
		this.namePersonal = namePersonal;
	}

	public String getCurrentName() {
		String temp = null;
		if (getNamePersonal() != null) {
			temp = getNamePersonal();
		} else {
			temp = company.getName();
		}
		return temp;
	}

	public JSONObject getJSONObject() {
		JSONObject json = new JSONObject();
		json.put("name", getCurrentName());
		json.put("unitCostPrice", getUnitCostPrice());
		json.put("value", getValue());
		json.put("plusMinusValue", getPlusMinusValue());

		json.put("quantity", getQuantity());
		json.put("yieldYear", getCurrentYield());
		json.put("yieldUnitCostPrice", getYieldUnitCostPrice());
		json.put("quote", getCompany().getQuote());
		json.put("plusMinusUnitCostPriceValue", getPlusMinusUnitCostPriceValue());
		json.put("variation", getCompany().getChange());
		return json;
	}
	
	@Override
	public int compareTo(Equity equity) {
		return this.getCurrentName().compareTo(equity.getCurrentName());
	}

}