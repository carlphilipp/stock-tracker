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

	/** Precision for calculation **/
	private final MathContext mathContext = MathContext.DECIMAL32;
	/** **/
	private static final int PERCENT = 100;
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
	public final Double getGapObjectivLocal() {
		if (gapObjectivLocal == null && getObjectivLocal() != null) {
			gapObjectivLocal = (getObjectivLocal() / company.getQuote() - 1) * PERCENT;
		}
		return gapObjectivLocal;
	}

	/**
	 * Get current value
	 * 
	 * @return the value
	 */
	public final Double getValue() {
		if (value == null) {
			value = quantity * company.getQuote() * getParity();
			value = new BigDecimal(value, mathContext).doubleValue();
		}
		return value;
	}

	/**
	 * Get plus minus value / unit cost price
	 * 
	 * @return the plus minus value
	 */
	public final Double getPlusMinusUnitCostPriceValue() {
		if (plusMinusUnitCostPriceValue == null) {
			plusMinusUnitCostPriceValue = getValue() - unitCostPrice * getCurrentParity() * quantity;
		}
		return plusMinusUnitCostPriceValue;
	}

	/**
	 * Get yield by year
	 * 
	 * @return a yield
	 */
	public final Double getYieldYear() {
		if (yieldYear == null) {
			yieldYear = getCurrentPruYield() * getValue() / PERCENT;
		}
		return yieldYear;
	}

	/**
	 * Get plus minus value
	 * 
	 * @return the plus minus value
	 */
	public final Double getPlusMinusValue() {
		if (plusMinusValue == null) {
			if (getUnitCostPrice() == 0) {
				plusMinusValue = 0.0;
			} else {
				plusMinusValue = (getValue() - getOriginalValue()) / getOriginalValue() * PERCENT;
			}
		}
		return plusMinusValue;
	}

	/**
	 * Get Yield unit cost price
	 * 
	 * @return the yield
	 */
	public final Double getYieldUnitCostPrice() {
		if (yieldUnitCostPrice == null) {
			yieldUnitCostPrice = getCurrentPruYield();
		}
		return yieldUnitCostPrice;
	}

	/**
	 * Get gap stop loss
	 * 
	 * @return the gap
	 */
	public final Double getGapStopLossLocal() {
		if (gapStopLossLocal == null && getStopLossLocal() != null) {
			gapStopLossLocal = (company.getQuote() / getStopLossLocal() - 1) * PERCENT;
		}
		return gapStopLossLocal;
	}

	/**
	 * Get gap objective
	 * 
	 * @return the gap
	 */
	public final Double getGapObjectiv() {
		if (gapObjectivLocal == null) {
			gapObjectivLocal = (getObjectivLocal() / company.getQuote() - 1) * PERCENT;
		}
		return gapObjectivLocal;
	}

	/**
	 * Set objective
	 * 
	 * @param gapObjectivLocal
	 *            the gap objective
	 */
	public final void setGapObjectivLocal(final Double gapObjectivLocal) {
		this.gapObjectivLocal = gapObjectivLocal;
	}

	/**
	 * Get Market capitalization
	 * 
	 * @return the market cap in decimal
	 */
	public final BigDecimal getMarketCapitalizationLocal() {
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
	public final MarketCapitalization getMarketCapitalizationType() {
		if (marketCapitalizationType == null) {
			BigDecimal cap = getMarketCapitalizationLocal();
			if (cap != null) {
				if (cap.compareTo(new BigDecimal("200000000")) == 1) {
					marketCapitalizationType = MarketCapitalization.MEGA_CAP;
				} else if (cap.compareTo(new BigDecimal("10000000")) == 1) {
					marketCapitalizationType = MarketCapitalization.LARGE_CAP;
				} else if (cap.compareTo(new BigDecimal("2000000")) == 1) {
					marketCapitalizationType = MarketCapitalization.MID_CAP;
				} else if (cap.compareTo(new BigDecimal("250")) == 1) {
					marketCapitalizationType = MarketCapitalization.SMALL_CAP;
				} else if (cap.compareTo(new BigDecimal("50")) == 1) {
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
	public final BigDecimal getMarketCapitalizationInBigDecimal() {
		if (getCurrentMarketCap() != null) {
			String str = getCurrentMarketCap().intern();
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
	public final Company getCompany() {
		return company;
	}

	/**
	 * Set company
	 * 
	 * @param company
	 *            the company
	 */
	public final void setCompany(final Company company) {
		this.company = company;
	}

	/**
	 * Get quantity
	 * 
	 * @return the quantity
	 */
	public final Double getQuantity() {
		return quantity;
	}

	/**
	 * Set quantity
	 * 
	 * @param quantity
	 *            the quantity
	 */
	public final void setQuantity(final Double quantity) {
		this.quantity = quantity;
	}

	/**
	 * Get unit cost price
	 * 
	 * @return the unit cost price
	 */
	public final Double getUnitCostPrice() {
		return unitCostPrice;
	}

	/**
	 * Set unit cost price
	 * 
	 * @param unitCostPrice
	 *            the unit cost price
	 */
	public final void setUnitCostPrice(final Double unitCostPrice) {
		this.unitCostPrice = unitCostPrice;
	}

	/**
	 * Get parity
	 * 
	 * @return the parity
	 */
	public final double getParity() {
		return parity;
	}

	/**
	 * Set parity
	 * 
	 * @param parity
	 *            the parity
	 */
	public final void setParity(final Double parity) {
		this.parity = parity;
	}

	/**
	 * Get yield personal
	 * 
	 * @return the yield personal
	 */
	public final Double getYieldPersonal() {
		return yieldPersonal;
	}

	/**
	 * Set yield personal
	 * 
	 * @param yieldPersonal
	 *            the yield personal
	 */
	public final void setYieldPersonal(final Double yieldPersonal) {
		this.yieldPersonal = yieldPersonal;
	}

	/**
	 * Get stop loss
	 * 
	 * @return the stop loss
	 */
	public final Double getStopLossLocal() {
		return stopLossLocal;
	}

	/**
	 * Set stop loss
	 * 
	 * @param stopLossLocal
	 *            the stop loss
	 */
	public final void setStopLossLocal(final Double stopLossLocal) {
		this.stopLossLocal = stopLossLocal;
	}

	/**
	 * Get objective
	 * 
	 * @return the objective
	 */
	public final Double getObjectivLocal() {
		return objectivLocal;
	}

	/**
	 * Set objective
	 * 
	 * @param objectivLocal
	 *            the objective
	 */
	public final void setObjectivLocal(final Double objectivLocal) {
		this.objectivLocal = objectivLocal;
	}

	/**
	 * Get yield frequency
	 * 
	 * @return the frequency
	 */
	public final Frequency getYieldFrequency() {
		return yieldFrequency;
	}

	/**
	 * Set yield frequency
	 * 
	 * @param yieldFrequency
	 *            the frequency
	 */
	public final void setYieldFrequency(final Frequency yieldFrequency) {
		this.yieldFrequency = yieldFrequency;
	}

	/**
	 * Get the yield month
	 * 
	 * @return the month
	 */
	public final Month getYieldMonth() {
		return yieldMonth;
	}

	/**
	 * Set yield per month
	 * 
	 * @param yieldMonth
	 *            the yield per month
	 */
	public final void setYieldMonth(final Month yieldMonth) {
		this.yieldMonth = yieldMonth;
	}

	/**
	 * Set id
	 * 
	 * @param id
	 *            the id
	 */
	public final void setid(final int id) {
		this.id = id;
	}

	/**
	 * Get id
	 * 
	 * @return the id
	 */
	public final int getId() {
		return id;
	}

	/**
	 * Get company id
	 * 
	 * @return the company id
	 */
	public final int getCompanyId() {
		return companyId;
	}

	/**
	 * Set company id
	 * 
	 * @param companyId
	 *            the id
	 */
	public final void setCompanyId(final int companyId) {
		this.companyId = companyId;
	}

	/**
	 * Get pertofolio id
	 * 
	 * @return the portfolio id
	 */
	public final int getPortfolioId() {
		return portfolioId;
	}

	/**
	 * Set portfolio id
	 * 
	 * @param portfolioId
	 *            the id
	 */
	public final void setPortfolioId(final int portfolioId) {
		this.portfolioId = portfolioId;
	}

	/**
	 * Get sector personal
	 * 
	 * @return the personal sector
	 */
	public final String getSectorPersonal() {
		return sectorPersonal;
	}

	/**
	 * Get current sector
	 * 
	 * @return the current sector
	 */
	public final String getCurrentSector() {
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
	public final void setSectorPersonal(final String sectorPersonal) {
		this.sectorPersonal = sectorPersonal;
	}

	/**
	 * Get personal industry
	 * 
	 * @return the industry
	 */
	public final String getIndustryPersonal() {
		return industryPersonal;
	}

	/**
	 * Get current industry
	 * 
	 * @return the current industry
	 */
	public final String getCurrentIndustry() {
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
	public final void setIndustryPersonal(final String industryPersonal) {
		this.industryPersonal = industryPersonal;
	}

	/**
	 * Get market cap personal
	 * 
	 * @return the market cap
	 */
	public final String getMarketCapPersonal() {
		return marketCapPersonal;
	}

	/**
	 * Get current market capitalization
	 * 
	 * @return the current market cap
	 */
	public final String getCurrentMarketCap() {
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
	public final void setMarketCapPersonal(final String marketCapPersonal) {
		this.marketCapPersonal = marketCapPersonal;
	}

	/**
	 * Set market capitalization
	 * 
	 * @param marketCapitalizationType
	 *            the market cpitalization type
	 */
	public final void setMarketCapitalizationType(final MarketCapitalization marketCapitalizationType) {
		this.marketCapitalizationType = marketCapitalizationType;
	}

	/**
	 * Get original value
	 * 
	 * @return the original value
	 */
	public final Double getOriginalValue() {
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
	public final void setOriginalValue(final Double originalValue) {
		this.originalValue = originalValue;
	}

	/**
	 * Get current pru yield
	 * 
	 * @return the yield
	 */
	public final Double getCurrentPruYield() {
		double res;
		if (getYieldPersonal() != null) {
			if (getUnitCostPrice() == 0) {
				res = 0;
			} else {
				res = getYieldPersonal() / getUnitCostPrice() * PERCENT;
			}
		} else {
			if (getUnitCostPrice() == 0) {
				res = 0;
			} else {
				res = getCompany().getYield() / getUnitCostPrice() * PERCENT;
			}
		}
		res = new BigDecimal(res, mathContext).doubleValue();
		return res;
	}

	/**
	 * Get current yield
	 * 
	 * @return the current yield
	 */
	public final Double getCurrentYield() {
		double res;
		if (getYieldPersonal() != null) {
			res = getYieldPersonal() / getCompany().getQuote() * PERCENT;
		} else {
			res = getCompany().getYield() / getCompany().getQuote() * PERCENT;
		}
		res = new BigDecimal(res, mathContext).doubleValue();
		return res;
	}

	/**
	 * Get personal parity
	 * 
	 * @return the parity
	 */
	public final Double getParityPersonal() {
/*		if (parityPersonal == null) {
			return 1.0;
		} else {*/
			return parityPersonal;
		//}
	}

	/**
	 * Set personal parity
	 * 
	 * @param parityPersonal
	 *            the parity
	 */
	public final void setParityPersonal(final Double parityPersonal) {
		this.parityPersonal = parityPersonal;
	}

	/**
	 * Get current parity. If personal parity is null, will return company today parity
	 * 
	 * @return the current parity
	 */
	public final Double getCurrentParity() {
		if (getParityPersonal() != null) {
			currentParity = getParityPersonal();
		} else {
			currentParity = getParity();
		}
		currentParity = new BigDecimal(currentParity, mathContext).doubleValue();
		return currentParity;
	}

	/**
	 * Get last update
	 * 
	 * @return the date
	 */
	public final Date getLastUpdate() {
		return (Date) lastUpdate.clone();
	}

	/**
	 * Set last update
	 * 
	 * @param lastUpdate
	 *            a date
	 */
	public final void setLastUpdate(final Date lastUpdate) {
		this.lastUpdate = (Date) lastUpdate.clone();
	}

	/**
	 * Get personal name
	 * 
	 * @return the personal name
	 */
	public final String getNamePersonal() {
		return namePersonal;
	}

	/**
	 * Set personal name
	 * 
	 * @param namePersonal
	 *            the name
	 */
	public final void setNamePersonal(final String namePersonal) {
		this.namePersonal = namePersonal;
	}

	/**
	 * Get current name. Return company name if personnal name is null
	 * 
	 * @return the current name
	 */
	public final String getCurrentName() {
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
	public final JSONObject getJSONObject() {
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
	public final String toString() {
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
	public int compareTo(final Equity equity) {
		return this.getCurrentName().compareTo(equity.getCurrentName());
	}

}