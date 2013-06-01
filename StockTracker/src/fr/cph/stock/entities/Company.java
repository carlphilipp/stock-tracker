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

import java.sql.Timestamp;

import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.enumtype.Market;

public class Company {

//	private static final Logger log = Logger.getLogger(Company.class);

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
	private Boolean realTime;
	private Boolean fund;
	private Timestamp lastUpdate;

	// calculated
	private Double change;
	private Double gapYearLow;
	private Double gapYearHigh;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getYahooId() {
		return yahooId;
	}

	public void setYahooId(String yahooId) {
		this.yahooId = yahooId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Market getMarket() {
		return market;
	}

	public void setMarket(Market market) {
		this.market = market;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public String getSector() {
		return sector;
	}

	public void setSector(String sector) {
		this.sector = sector;
	}

	public double getQuote() {
		return quote;
	}

	public void setQuote(double quote) {
		this.quote = quote;
	}

	public double getYield() {
		return yield;
	}

	public void setYield(double yield) {
		this.yield = yield;
	}

	public Double getYesterdayClose() {
		return yesterdayClose;
	}

	public void setYesterdayClose(Double yesterdayClose) {
		this.yesterdayClose = yesterdayClose;
	}

	public String toString() {
		return "Company : " + id + " - " + yahooId + " - " + name + " - " + market + " - " + currency + " - " + sector + " - " + industry
				+ " - " + quote + " - " + yield + " - " + marketCapitalization;
	}

	public String getMarketCapitalization() {
		return marketCapitalization;
	}

	public void setMarketCapitalization(String marketCapitalization) {
		this.marketCapitalization = marketCapitalization;
	}

	public Timestamp getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Timestamp lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public Double getYearLow() {
		return yearLow;
	}

	public void setYearLow(Double yearLow) {
		this.yearLow = yearLow;
	}

	public Double getMinGap() {
		return minGap;
	}

	public void setMinGap(Double minGap) {
		this.minGap = minGap;
	}

	public Double getMaxGap() {
		return maxGap;
	}

	public void setMaxGap(Double maxGap) {
		this.maxGap = maxGap;
	}

	public Double getYearHigh() {
		return yearHigh;
	}

	public void setYearHigh(Double yearHigh) {
		this.yearHigh = yearHigh;
	}

	public Double getChange() {
		return change;
	}

	public void setChange(double change) {
		this.change = change;
	}

	public Double getGapYearLow() {
		if (getYearLow() != null) {
			if (gapYearLow == null) {
				gapYearLow = ((getQuote() / getYearLow()) - 1) * 100;
			}
		}
		return gapYearLow;
	}

	public void setGapYearLow(Double gapYearLow) {
		this.gapYearLow = gapYearLow;
	}

	public Double getGapYearHigh() {
		if (getYearHigh() != null) {
			if (gapYearHigh == null) {
				gapYearHigh = ((getYearHigh() / getQuote()) - 1) * 100;
			}
		}
		return gapYearHigh;
	}

	public void setGapYearHigh(Double gapYearHigh) {
		this.gapYearHigh = gapYearHigh;
	}

	public Boolean getRealTime() {
		return realTime;
	}

	public void setRealTime(Boolean realTime) {
		this.realTime = realTime;
	}

	public Boolean getFund() {
		return fund;
	}

	public void setFund(Boolean fund) {
		this.fund = fund;
	}

	public String getChangeInPercent() {
		return changeInPercent;
	}

	public void setChangeInPercent(String changeInPercent) {
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

}
