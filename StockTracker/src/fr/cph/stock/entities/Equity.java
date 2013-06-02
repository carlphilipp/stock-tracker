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

/**
 * This class represents an equity
 * 
 * @author Carl-Philipp Harmant
 * 
 */
public class Equity implements Comparable<Equity> {

	// private static final Logger log = Logger.getLogger(Equity.class);

	/** Precision for calculation **/
	private final MathContext mathContext = MathContext.DECIMAL32;

	/** Company **/
	private Company company;
	/** Id **/
	private int id;
	/** Portfolio id **/
	private int portfolioId;
	/** Company id **/
	private int companyId;
	/** Personal company name **/
	private String namePersonal;
	/** Personal sector name **/
	private String sectorPersonal;
	/** Personal industry name **/
	private String industryPersonal;
	/** Personal Market Cap **/
	private String marketCapPersonal;
	/** Quantity **/
	private Double quantity;
	/** Unit cost price **/
	private Double unitCostPrice;
	/** Parity **/
	private Double parity;
	/** Personal Yield **/
	private Double yieldPersonal;
	/** Personal parity **/
	private Double parityPersonal;
	/** Stop loss **/
	private Double stopLossLocal;
	/** Objective **/
	private Double objectivLocal;
	/** Yield Frequency **/
	private Frequency yieldFrequency;
	/** Yield month **/
	private Month yieldMonth;
	/** Last update **/
	private Date lastUpdate;

	// Not stored in DB. Calculated at run time
	/** Plus Minus value **/
	private Double plusMinusValue;
	/** Value **/
	private Double value;
	/** Original value **/
	private Double originalValue;
	/** Yield Year **/
	private Double yieldYear;
	/** Unit cost price / yield **/
	private Double yieldUnitCostPrice;
	/** Plus minus value / unit cost price **/
	private Double plusMinusUnitCostPriceValue;
	/** Gap stop loss **/
	private Double gapStopLossLocal;
	/** Gap objective **/
	private Double gapObjectivLocal;
	/** Market Capitalization **/
	private BigDecimal marketCapitalizationLocal;
	/** Market Capitalization type **/
	private MarketCapitalization marketCapitalizationType;
	/** Current parity **/
	private Double currentParity;

	/**
	 * Calculation of gap objective
	 * 
	 * @return the cap
	 */
	public Double getGapObjectivLocal() {
		if (gapObjectivLocal == null) {
			if (getObjectivLocal() != null) {
				gapObjectivLocal = (getObjectivLocal() / company.getQuote() - 1) * 100;
			}
		}
		return gapObjectivLocal;
	}

	/**
	 * Get current value
	 * 
	 * @return the value
	 */
	public Double getValue() {
		if (value == null) {
			value = quantity * company.getQuote() * getParity();
			value = (new BigDecimal(value, mathContext)).doubleValue();
		}
		return value;
	}

	/**
	 * Get plus minus value / unit cost price
	 * 
	 * @return the plus minus value
	 */
	public Double getPlusMinusUnitCostPriceValue() {
		if (plusMinusUnitCostPriceValue == null) {
			plusMinusUnitCostPriceValue = getValue() - (unitCostPrice * getCurrentParity() * quantity);
		}
		return plusMinusUnitCostPriceValue;
	}

	/**
	 * Get yield by year
	 * 
	 * @return a yield
	 */
	public Double getYieldYear() {
		if (yieldYear == null) {
			yieldYear = getCurrentPruYield() * getValue() / 100;
		}
		return yieldYear.doubleValue();
	}

	/**
	 * Get plus minus value
	 * 
	 * @return the plus minus value
	 */
	public Double getPlusMinusValue() {
		if (plusMinusValue == null) {
			plusMinusValue = (getValue() - getOriginalValue()) / getOriginalValue() * 100;
		}
		return plusMinusValue;
	}

	/**
	 * Get Yield unit cost price
	 * 
	 * @return the yield
	 */
	public Double getYieldUnitCostPrice() {
		if (yieldUnitCostPrice == null) {
			yieldUnitCostPrice = getCurrentPruYield();
		}
		return yieldUnitCostPrice.doubleValue();
	}

	/**
	 * Get gap stop loss
	 * 
	 * @return the gap
	 */
	public Double getGapStopLossLocal() {
		if (gapStopLossLocal == null) {
			if (getStopLossLocal() != null) {
				gapStopLossLocal = (company.getQuote() / getStopLossLocal() - 1) * 100;
			}
		}
		return gapStopLossLocal;
	}

	/**
	 * Get gap objective
	 * 
	 * @return the gap
	 */
	public Double getGapObjectiv() {
		if (gapObjectivLocal == null) {
			gapObjectivLocal = (getObjectivLocal() / company.getQuote() - 1) * 100;
		}
		return gapObjectivLocal;
	}

	/**
	 * Set objective
	 * 
	 * @param gapObjectivLocal
	 *            the gap objective
	 */
	public void setGapObjectivLocal(Double gapObjectivLocal) {
		this.gapObjectivLocal = gapObjectivLocal;
	}

	/**
	 * Get Market capitalization
	 * 
	 * @return the market cap in decimal
	 */
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

	/**
	 * Get market capitalization type
	 * 
	 * @return the market capitalization
	 */
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

	/**
	 * Get Market capitalization
	 * 
	 * @return the market cap in decimal
	 */
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

	/**
	 * Get company
	 * 
	 * @return the company
	 */
	public Company getCompany() {
		return company;
	}

	/**
	 * Set company
	 * 
	 * @param company
	 *            the company
	 */
	public void setCompany(Company company) {
		this.company = company;
	}

	/**
	 * Get quantity
	 * 
	 * @return the quantity
	 */
	public Double getQuantity() {
		return quantity;
	}

	/**
	 * Set quantity
	 * 
	 * @param quantity
	 *            the quantity
	 */
	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	/**
	 * Get unit cost price
	 * 
	 * @return the unit cost price
	 */
	public Double getUnitCostPrice() {
		return unitCostPrice;
	}

	/**
	 * Set unit cost price
	 * 
	 * @param unitCostPrice
	 *            the unit cost price
	 */
	public void setUnitCostPrice(Double unitCostPrice) {
		this.unitCostPrice = unitCostPrice;
	}

	/**
	 * Get parity
	 * 
	 * @return the parity
	 */
	public double getParity() {
		return parity;
	}

	/**
	 * Set parity
	 * 
	 * @param parity
	 *            the parity
	 */
	public void setParity(Double parity) {
		this.parity = parity;
	}

	/**
	 * Get yield personal
	 * 
	 * @return the yield personal
	 */
	public Double getYieldPersonal() {
		return yieldPersonal;
	}

	/**
	 * Set yield personal
	 * 
	 * @param yieldPersonal
	 *            the yield personal
	 */
	public void setYieldPersonal(Double yieldPersonal) {
		this.yieldPersonal = yieldPersonal;
	}

	/**
	 * Get stop loss
	 * 
	 * @return the stop loss
	 */
	public Double getStopLossLocal() {
		return stopLossLocal;
	}

	/**
	 * Set stop loss
	 * 
	 * @param stopLossLocal
	 *            the stop loss
	 */
	public void setStopLossLocal(Double stopLossLocal) {
		this.stopLossLocal = stopLossLocal;
	}

	/**
	 * Get objective
	 * 
	 * @return the objective
	 */
	public Double getObjectivLocal() {
		return objectivLocal;
	}

	/**
	 * Set objective
	 * 
	 * @param objectivLocal
	 *            the objective
	 */
	public void setObjectivLocal(Double objectivLocal) {
		this.objectivLocal = objectivLocal;
	}

	/**
	 * Get yield frequency
	 * 
	 * @return the frequency
	 */
	public Frequency getYieldFrequency() {
		return yieldFrequency;
	}

	/**
	 * Set yield frequency
	 * 
	 * @param yieldFrequency
	 *            the frequency
	 */
	public void setYieldFrequency(Frequency yieldFrequency) {
		this.yieldFrequency = yieldFrequency;
	}

	/**
	 * Get the yield month
	 * 
	 * @return the month
	 */
	public Month getYieldMonth() {
		return yieldMonth;
	}

	/**
	 * Set yield per month
	 * 
	 * @param yieldMonth
	 *            the yield per month
	 */
	public void setYieldMonth(Month yieldMonth) {
		this.yieldMonth = yieldMonth;
	}

	/**
	 * Set id
	 * 
	 * @param id
	 *            the id
	 */
	public void setid(int id) {
		this.id = id;
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
	 * Get company id
	 * 
	 * @return the company id
	 */
	public int getCompanyId() {
		return companyId;
	}

	/**
	 * Set company id
	 * 
	 * @param companyId
	 *            the id
	 */
	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	/**
	 * Get pertofolio id
	 * 
	 * @return the portfolio id
	 */
	public int getPortfolioId() {
		return portfolioId;
	}

	/**
	 * Set portfolio id
	 * 
	 * @param portfolioId
	 *            the id
	 */
	public void setPortfolioId(int portfolioId) {
		this.portfolioId = portfolioId;
	}

	/**
	 * Get sector personal
	 * 
	 * @return the personal sector
	 */
	public String getSectorPersonal() {
		return sectorPersonal;
	}

	/**
	 * Get current sector
	 * 
	 * @return the current sector
	 */
	public String getCurrentSector() {
		String sector;
		if (getSectorPersonal() == null) {
			sector = company.getSector();
		} else {
			sector = getSectorPersonal();
		}
		return sector;
	}

	/**
	 * Set personal sector
	 * 
	 * @param sectorPersonal
	 *            the personal sector
	 */
	public void setSectorPersonal(String sectorPersonal) {
		this.sectorPersonal = sectorPersonal;
	}

	/**
	 * Get personal industry
	 * 
	 * @return the industry
	 */
	public String getIndustryPersonal() {
		return industryPersonal;
	}

	/**
	 * Get current industry
	 * 
	 * @return the current industry
	 */
	public String getCurrentIndustry() {
		String industry;
		if (getIndustryPersonal() == null) {
			industry = company.getIndustry();
		} else {
			industry = getIndustryPersonal();
		}
		return industry;
	}

	/**
	 * Set personal industry
	 * 
	 * @param industryPersonal
	 *            the industry
	 */
	public void setIndustryPersonal(String industryPersonal) {
		this.industryPersonal = industryPersonal;
	}

	/**
	 * Get market cap personal
	 * 
	 * @return the market cap
	 */
	public String getMarketCapPersonal() {
		return marketCapPersonal;
	}

	/**
	 * Get current market capitalization
	 * 
	 * @return the current market cap
	 */
	public String getCurrentMarketCap() {
		String marketCap;
		if (getMarketCapPersonal() == null) {
			marketCap = company.getMarketCapitalization();
		} else {
			marketCap = getMarketCapPersonal();
		}
		return marketCap;
	}

	/**
	 * Set market capitalization personal
	 * 
	 * @param marketCapPersonal
	 *            the market cap personnal
	 */
	public void setMarketCapPersonal(String marketCapPersonal) {
		this.marketCapPersonal = marketCapPersonal;
	}

	/**
	 * Set market capitalization
	 * 
	 * @param marketCapitalizationType
	 *            the market cpitalization type
	 */
	public void setMarketCapitalizationType(MarketCapitalization marketCapitalizationType) {
		this.marketCapitalizationType = marketCapitalizationType;
	}

	/**
	 * Get original value
	 * 
	 * @return the original value
	 */
	public Double getOriginalValue() {
		if (originalValue == null) {
			originalValue = getQuantity() * getUnitCostPrice() * getCurrentParity();
		}
		return originalValue;
	}

	/**
	 * Set original value
	 * 
	 * @param originalValue
	 *            the original value
	 */
	public void setOriginalValue(Double originalValue) {
		this.originalValue = originalValue;
	}

	/**
	 * Get current pru yield
	 * 
	 * @return the yield
	 */
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

	/**
	 * Get current yield
	 * 
	 * @return the current yield
	 */
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

	/**
	 * Get personal parity
	 * 
	 * @return the parity
	 */
	public Double getParityPersonal() {
		return parityPersonal;
	}

	/**
	 * Set personal parity
	 * 
	 * @param parityPersonal
	 *            the parity
	 */
	public void setParityPersonal(Double parityPersonal) {
		this.parityPersonal = parityPersonal;
	}

	/**
	 * Get current parity. If personal parity is null, will return company today parity
	 * 
	 * @return the current parity
	 */
	public Double getCurrentParity() {
		if (getParityPersonal() != null) {
			currentParity = getParityPersonal();
		} else {
			currentParity = getParity();
		}
		currentParity = (new BigDecimal(currentParity, mathContext)).doubleValue();
		return currentParity;
	}

	/**
	 * Get last update
	 * 
	 * @return the date
	 */
	public Date getLastUpdate() {
		return lastUpdate;
	}

	/**
	 * Set last update
	 * 
	 * @param lastUpdate
	 *            a date
	 */
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	/**
	 * Get personal name
	 * 
	 * @return the personal name
	 */
	public String getNamePersonal() {
		return namePersonal;
	}

	/**
	 * Set personal name
	 * 
	 * @param namePersonal
	 *            the name
	 */
	public void setNamePersonal(String namePersonal) {
		this.namePersonal = namePersonal;
	}

	/**
	 * Get current name. Return company name if personnal name is null
	 * 
	 * @return the current name
	 */
	public String getCurrentName() {
		String temp = null;
		if (getNamePersonal() != null) {
			temp = getNamePersonal();
		} else {
			temp = company.getName();
		}
		return temp;
	}

	/**
	 * Get a jsonObject of the equity
	 * 
	 * @return a JSONObject
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Equity " + id + " - portfolioId: " + portfolioId + "\n");
		sb.append("Quantity: " + quantity + " -  Unit Cost Price: " + unitCostPrice + "\n");
		sb.append(company);
		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Equity equity) {
		return this.getCurrentName().compareTo(equity.getCurrentName());
	}

}