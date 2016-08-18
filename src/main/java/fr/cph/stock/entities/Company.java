/**
 * Copyright 2013 Carl-Philipp Harmant
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

import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.enumtype.Market;

import java.sql.Timestamp;

/**
 * This class represents a company
 *
 * @author Carl-Philipp Harmant
 *
 */
public class Company {

	private int id;
	private String yahooId;
	private String name;
	private Market market;
	private Currency currency;
	private String sector;
	private String industry;
	private double quote;
	private double yield;
	private String marketCapitalization;
	private Double yesterdayClose;
	private String changeInPercent;
	private Double yearLow;
	private Double yearHigh;
	private Double minGap;
	private Double maxGap;
	/** Is this company under read time data **/
	private Boolean realTime;
	private Boolean fund;
	private Timestamp lastUpdate;
	private Double change;
	private Double gapYearLow;
	private Double gapYearHigh;
	/** Manually used by user **/
	private Boolean manual;

	/**
	 *
	 * @return the id
	 */
	public final int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id
	 */
	public final void setId(final int id) {
		this.id = id;
	}

	/**
	 * @return the yahoo id
	 */
	public final String getYahooId() {
		return yahooId;
	}

	/**
	 * @param yahooId
	 *            the yahoo id
	 */
	public final void setYahooId(final String yahooId) {
		this.yahooId = yahooId;
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name
	 */
	public final void setName(final String name) {
		this.name = name;
	}

	/**
	 * @return the market
	 */
	public final Market getMarket() {
		return market;
	}

	/**
	 * @param market
	 *            the market
	 */
	public final void setMarket(final Market market) {
		this.market = market;
	}

	/**
	 * @return the currency
	 */
	public final Currency getCurrency() {
		return currency;
	}

	/**
	 * @param currency
	 *            the currency
	 */
	public final void setCurrency(final Currency currency) {
		this.currency = currency;
	}

	/**
	 * @return the industry
	 */
	public final String getIndustry() {
		return industry;
	}

	/**
	 * @param industry
	 *            the industry
	 */
	public final void setIndustry(final String industry) {
		this.industry = industry;
	}

	/**
	 * @return the sector
	 */
	public final String getSector() {
		return sector;
	}

	/**
	 * @param sector
	 *            the sector
	 */
	public final void setSector(final String sector) {
		this.sector = sector;
	}

	/**
	 * @return the quote
	 */
	public final double getQuote() {
		return quote;
	}

	/**
	 * @param quote
	 *            the quote
	 */
	public final void setQuote(final double quote) {
		this.quote = quote;
	}

	/**
	 * @return the yield
	 */
	public final double getYield() {
		return yield;
	}

	/**
	 * @param yield
	 *            the yield
	 */
	public final void setYield(final double yield) {
		this.yield = yield;
	}

	/**
	 * @return the yesterday close
	 */
	public final Double getYesterdayClose() {
		return yesterdayClose;
	}

	/**
	 * @param yesterdayClose
	 *            the yesterday close
	 */
	public final void setYesterdayClose(final Double yesterdayClose) {
		this.yesterdayClose = yesterdayClose;
	}

	/**
	 * @return the market capitalization
	 */
	public final String getMarketCapitalization() {
		return marketCapitalization;
	}

	/**
	 * @param marketCapitalization
	 *            the market capitalization
	 */
	public final void setMarketCapitalization(final String marketCapitalization) {
		this.marketCapitalization = marketCapitalization;
	}

	/**
	 * @return the last update
	 */
	public final Timestamp getLastUpdate() {
		if (lastUpdate != null) {
			return (Timestamp) lastUpdate.clone();
		} else {
			return null;
		}
	}

	/**
	 * @param lastUpdate
	 *            the last update
	 */
	public final void setLastUpdate(final Timestamp lastUpdate) {
		this.lastUpdate = (Timestamp) lastUpdate.clone();
	}

	/**
	 * @return the year low
	 */
	public final Double getYearLow() {
		return yearLow;
	}

	/**
	 * @param yearLow
	 *            the year low
	 */
	public final void setYearLow(final Double yearLow) {
		this.yearLow = yearLow;
	}

	/**
	 * @return the min gap
	 */
	public final Double getMinGap() {
		return minGap;
	}

	/**
	 * @param minGap
	 *            the min gap
	 */
	public final void setMinGap(final Double minGap) {
		this.minGap = minGap;
	}

	/**
	 * @return the max gap
	 */
	public final Double getMaxGap() {
		return maxGap;
	}

	/**
	 * @param maxGap
	 *            the max gap
	 */
	public final void setMaxGap(final Double maxGap) {
		this.maxGap = maxGap;
	}

	/**
	 * @return the year high
	 */
	public final Double getYearHigh() {
		return yearHigh;
	}

	/**
	 * @param yearHigh
	 *            the year high
	 */
	public final void setYearHigh(final Double yearHigh) {
		this.yearHigh = yearHigh;
	}

	/**
	 * @return the change
	 */
	public final Double getChange() {
		return change;
	}

	/**
	 * @param change
	 *            the change
	 */
	public final void setChange(final double change) {
		this.change = change;
	}

	/**
	 * @return the gap year low
	 */
	public final Double getGapYearLow() {
		if (getYearLow() != null && gapYearLow == null) {
			gapYearLow = (getQuote() / getYearLow() - 1) * 100;
		}
		return gapYearLow;
	}

	/**
	 * @param gapYearLow
	 *            the gap year low
	 */
	public final void setGapYearLow(final Double gapYearLow) {
		this.gapYearLow = gapYearLow;
	}

	/**
	 * @return the gap year high
	 */
	public final Double getGapYearHigh() {
		if (getYearHigh() != null && gapYearHigh == null) {
			gapYearHigh = (getYearHigh() / getQuote() - 1) * 100;
		}
		return gapYearHigh;
	}

	/**
	 * @param gapYearHigh
	 *            the gap year high
	 */
	public final void setGapYearHigh(final Double gapYearHigh) {
		this.gapYearHigh = gapYearHigh;
	}

	/**
	 * @return if realtime
	 */
	public final Boolean getRealTime() {
		return realTime;
	}

	/**
	 * @param realTime
	 *            if realtime
	 */
	public final void setRealTime(final Boolean realTime) {
		this.realTime = realTime;
	}

	/**
	 * @return the fund
	 */
	public final Boolean getFund() {
		return fund;
	}

	/**
	 * @param fund
	 *            the fund
	 */
	public final void setFund(final Boolean fund) {
		this.fund = fund;
	}

	/**
	 * @return the change in percent
	 */
	public final String getChangeInPercent() {
		return changeInPercent;
	}

	/**
	 * @param changeInPercent
	 *            the change in percent
	 */
	public final void setChangeInPercent(final String changeInPercent) {
		if (changeInPercent != null) {
			char c = changeInPercent.charAt(0);
			String number = null;
			if (c == '+') {
				number = changeInPercent.substring(1, changeInPercent.length() - 1);
			} else {
				number = changeInPercent.substring(0, changeInPercent.length() - 1);
			}
			setChange(Double.parseDouble(number));
		}
		this.changeInPercent = changeInPercent;
	}

	@Override
	public final String toString() {
		return "Company [id=" + id + ", yahooId=" + yahooId + ", name=" + name + ", market=" + market + ", currency=" + currency
			+ ", sector=" + sector + ", industry=" + industry + ", quote=" + quote + ", yield=" + yield
			+ ", marketCapitalization=" + marketCapitalization + ", yesterdayClose=" + yesterdayClose + ", changeInPercent="
			+ changeInPercent + ", yearLow=" + yearLow + ", yearHigh=" + yearHigh + ", minGap=" + minGap + ", maxGap="
			+ maxGap + ", realTime=" + realTime + ", fund=" + fund + ", lastUpdate=" + lastUpdate + ", change=" + change
			+ ", gapYearLow=" + gapYearLow + ", gapYearHigh=" + gapYearHigh + "]";
	}

	public Boolean getManual() {
		return manual;
	}

	public void setManual(Boolean manual) {
		this.manual = manual;
	}
}
