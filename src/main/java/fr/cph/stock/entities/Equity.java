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

package fr.cph.stock.entities;

import com.google.gson.JsonObject;
import fr.cph.stock.enumtype.Frequency;
import fr.cph.stock.enumtype.MarketCapitalization;
import fr.cph.stock.enumtype.Month;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Date;

/**
 * This class represents an equity
 *
 * @author Carl-Philipp Harmant
 */
@ToString
public class Equity implements Comparable<Equity> {

	/**
	 * Precision for calculation
	 **/
	private final MathContext mathContext = MathContext.DECIMAL32;
	private static final int PERCENT = 100;

	@Getter
	@Setter
	private Company company;
	@Getter
	@Setter
	private int id;
	@Getter
	@Setter
	private int portfolioId;
	@Getter
	@Setter
	private int companyId;
	@Getter
	@Setter
	private String namePersonal;
	@Getter
	@Setter
	private String sectorPersonal;
	@Getter
	@Setter
	private String industryPersonal;
	@Getter
	@Setter
	private String marketCapPersonal;
	@Getter
	@Setter
	private Double quantity;
	@Getter
	@Setter
	private Double unitCostPrice;
	@Getter
	@Setter
	private Double parity;
	@Getter
	@Setter
	private Double yieldPersonal;
	@Getter
	@Setter
	private Double parityPersonal;
	@Getter
	@Setter
	private Double stopLossLocal;
	@Getter
	@Setter
	private Double objectivLocal;
	@Getter
	@Setter
	private Frequency yieldFrequency;
	@Getter
	@Setter
	private Month yieldMonth;
	@Setter
	private Date lastUpdate;

	// Not stored in DB. Calculated at run time and cached
	private Double plusMinusValue;
	private Double value;
	private Double originalValue;
	/**
	 * Yield Year
	 **/
	private Double yieldYear;
	/**
	 * Unit cost price / yield
	 **/
	private Double yieldUnitCostPrice;
	/**
	 * Plus minus value / unit cost price
	 **/
	private Double plusMinusUnitCostPriceValue;
	/**
	 * Gap stop loss
	 **/
	private Double gapStopLossLocal;
	/**
	 * Gap objective
	 **/
	private Double gapObjectivLocal;
	/**
	 * Market Capitalization
	 **/
	private BigDecimal marketCapitalizationLocal;
	/**
	 * Market Capitalization type
	 **/
	private MarketCapitalization marketCapitalizationType;
	/**
	 * Current parity
	 **/
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
	final Double getPlusMinusUnitCostPriceValue() {
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
	final Double getYieldYear() {
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
	private Double getPlusMinusValue() {
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
	private Double getYieldUnitCostPrice() {
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
	 * @param gapObjectivLocal the gap objective
	 */
	public final void setGapObjectivLocal(final Double gapObjectivLocal) {
		this.gapObjectivLocal = gapObjectivLocal;
	}

	/**
	 * Get Market capitalization
	 *
	 * @return the market cap in decimal
	 */
	private BigDecimal getMarketCapitalizationLocal() {
		if (marketCapitalizationLocal == null) {
			final BigDecimal big = getMarketCapitalizationInBigDecimal();
			marketCapitalizationLocal = big == null ? null : big.multiply(new BigDecimal(getParity()));
		}
		return marketCapitalizationLocal;
	}

	/**
	 * Get market capitalization type
	 *
	 * @return the market capitalization
	 */
	final MarketCapitalization getMarketCapitalizationType() {
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
	private BigDecimal getMarketCapitalizationInBigDecimal() {
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
				} else {
					if (c == 'T') {
						str = str.replaceAll("T", "");
						int point = str.indexOf('.');
						int reverse = str.length() - point - 1;
						sb.append(str.replaceAll("\\.", ""));
						for (int i = 0; i < 9 - reverse; i++) {
							sb.append("0");
						}
					}
				}
			}
			return new BigDecimal(sb.toString());
		} else {
			return null;
		}
	}

	/**
	 * Get current sector
	 *
	 * @return the current sector
	 */
	final String getCurrentSector() {
		return getSectorPersonal() == null ? company.getSector() : getSectorPersonal();
	}

	/**
	 * Get current industry
	 *
	 * @return the current industry
	 */
	public final String getCurrentIndustry() {
		return getIndustryPersonal() == null ? company.getIndustry() : getIndustryPersonal();
	}

	/**
	 * Get current market capitalization
	 *
	 * @return the current market cap
	 */
	private String getCurrentMarketCap() {
		return getMarketCapPersonal() == null ? company.getMarketCapitalization() : getMarketCapPersonal();
	}

	/**
	 * Set market capitalization
	 *
	 * @param marketCapitalizationType the market cpitalization type
	 */
	final void setMarketCapitalizationType(final MarketCapitalization marketCapitalizationType) {
		this.marketCapitalizationType = marketCapitalizationType;
	}

	/**
	 * Get original value
	 *
	 * @return the original value
	 */
	private Double getOriginalValue() {
		if (originalValue == null) {
			originalValue = getQuantity() * getUnitCostPrice() * getCurrentParity();
		}
		return originalValue;
	}

	/**
	 * Get current pru yield
	 *
	 * @return the yield
	 */
	private Double getCurrentPruYield() {
		double res;
		if (getYieldPersonal() != null) {
			res = getUnitCostPrice() == 0 ? 0 : getYieldPersonal() / getUnitCostPrice() * PERCENT;
		} else {
			res = getUnitCostPrice() == 0 ? 0 : getCompany().getYield() / getUnitCostPrice() * PERCENT;
		}
		res = new BigDecimal(Double.toString(res), mathContext).doubleValue();
		return res;
	}

	/**
	 * Get current yield
	 *
	 * @return the current yield
	 */
	private Double getCurrentYield() {
		final Double res = getYieldPersonal() != null
			? getYieldPersonal() / getCompany().getQuote() * PERCENT
			: getCompany().getYield() / getCompany().getQuote() * PERCENT;
		return res.isInfinite() || res.isNaN()
			? 0.0
			: new BigDecimal(Double.toString(res), mathContext).doubleValue();
	}

	/**
	 * Get current parity. If personal parity is null, will return company today parity
	 *
	 * @return the current parity
	 */
	final Double getCurrentParity() {
		if (currentParity == null) {
			currentParity = getParityPersonal() != null ? getParityPersonal() : getParity();
			currentParity = new BigDecimal(currentParity, mathContext).doubleValue();
		}
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
	 * Get current name. Return company name if personnal name is null
	 *
	 * @return the current name
	 */
	final String getCurrentName() {
		return getNamePersonal() != null ? getNamePersonal() : company.getName();
	}

	/**
	 * Get a jsonObject of the equity
	 *
	 * @return a JSONObject
	 */
	final JsonObject getJSONObject() {
		final JsonObject json = new JsonObject();
		json.addProperty("name", getCurrentName());
		json.addProperty("unitCostPrice", getUnitCostPrice());
		json.addProperty("value", getValue());
		json.addProperty("plusMinusValue", getPlusMinusValue());
		json.addProperty("quantity", getQuantity());
		json.addProperty("yieldYear", getCurrentYield());
		json.addProperty("yieldUnitCostPrice", getYieldUnitCostPrice());
		json.addProperty("quote", getCompany().getQuote());
		json.addProperty("plusMinusUnitCostPriceValue", getPlusMinusUnitCostPriceValue());
		json.addProperty("variation", getCompany().getChange());
		return json;
	}

	@Override
	public final int compareTo(final Equity equity) {
		return this.getCurrentName().compareTo(equity.getCurrentName());
	}
}
