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

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Portfolio implements Parcelable {

	private String totalValue;
	private String totalGain;
	private String totalPlusMinusValue;
	private boolean up;
	private boolean todayUp;
	private String liquidity;
	private String yieldYear;
	private String yieldYearPerc;
	private String lastUpdate;

	private String gainPerformance;
	private String performancePerformance;
	private String yieldPerformance;
	private String taxesPerformace;

	private String chartColors;
	private String chartData;
	private String chartDate;
	private String chartDraw;
	
	private String chartSectorData;
	private String chartSectorTitle;
	private String chartSectorDraw;
	private String chartSectorCompanies;
	
	private String chartCapData;
	private String chartCapTitle;
	private String chartCapDraw;
	private String chartCapCompanies;
	
	private String totalVariation;

	private List<Equity> equities;
	private List<ShareValue> shareValues;
	private List<Account> accounts;

	public Portfolio() {

	}

	public Portfolio(Parcel in) {
		readFromParcel(in);
	}

	public boolean isUp() {
		return up;
	}

	public void setUp(boolean up) {
		this.up = up;
	}

	public String getTotalValue() {
		return totalValue;
	}

	public void setTotalValue(String totalValue) {
		this.totalValue = totalValue;
	}

	public String getTotalGain() {
		return totalGain;
	}

	public void setTotalGain(String totalGain) {
		this.totalGain = totalGain;
	}

	public String getTotalPlusMinusValue() {
		return totalPlusMinusValue;
	}

	public void setTotalPlusMinusValue(String totalPlusMinusValue) {
		this.totalPlusMinusValue = totalPlusMinusValue;
	}

	public String getLiquidity() {
		return liquidity;
	}

	public void setLiquidity(String liquidity) {
		this.liquidity = liquidity;
	}

	public String getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public List<Equity> getEquities() {
		return equities;
	}

	public void setEquities(List<Equity> equities) {
		this.equities = equities;
	}

	public String getYieldYear() {
		return yieldYear;
	}

	public void setYieldYear(String yielYear) {
		this.yieldYear = yielYear;
	}

	public String getYieldYearPerc() {
		return yieldYearPerc;
	}

	public void setYieldYearPerc(String yieldYearPerc) {
		this.yieldYearPerc = yieldYearPerc;
	}

	public List<ShareValue> getShareValues() {
		return shareValues;
	}

	public void setShareValues(List<ShareValue> shareValues) {
		this.shareValues = shareValues;
	}

	public List<Account> getAccounts() {
		return accounts;
	}

	public String getGainPerformance() {
		return gainPerformance;
	}

	public void setGainPerformance(String gainPerformance) {
		this.gainPerformance = gainPerformance;
	}

	public String getPerformancePerformance() {
		return performancePerformance;
	}

	public void setPerformancePerformance(String performancePerformance) {
		this.performancePerformance = performancePerformance;
	}

	public String getYieldPerformance() {
		return yieldPerformance;
	}

	public void setYieldPerformance(String yieldPerformance) {
		this.yieldPerformance = yieldPerformance;
	}

	public String getTaxesPerformace() {
		return taxesPerformace;
	}

	public void setTaxesPerformace(String taxesPerformace) {
		this.taxesPerformace = taxesPerformace;
	}

	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}

	public String getChartColors() {
		return chartColors;
	}

	public void setChartColors(String chartColors) {
		this.chartColors = chartColors;
	}

	public String getChartData() {
		return chartData;
	}

	public void setChartData(String chartData) {
		this.chartData = chartData;
	}

	public String getChartDate() {
		return chartDate;
	}

	public void setChartDate(String chartDate) {
		this.chartDate = chartDate;
	}

	public String getChartDraw() {
		return chartDraw;
	}

	public void setChartDraw(String chartDraw) {
		this.chartDraw = chartDraw;
	}

	public String getChartSectorData() {
		return chartSectorData;
	}

	public void setChartSectorData(String chartSectorData) {
		this.chartSectorData = chartSectorData;
	}

	public String getChartSectorTitle() {
		return chartSectorTitle;
	}

	public void setChartSectorTitle(String chartSectorTitle) {
		this.chartSectorTitle = chartSectorTitle;
	}

	public String getChartSectorDraw() {
		return chartSectorDraw;
	}

	public void setChartSectorDraw(String chartSectorDraw) {
		this.chartSectorDraw = chartSectorDraw;
	}

	public String getChartSectorCompanies() {
		return chartSectorCompanies;
	}

	public void setChartSectorCompanies(String chartSectorCompanies) {
		this.chartSectorCompanies = chartSectorCompanies;
	}

	public String getChartCapData() {
		return chartCapData;
	}

	public void setChartCapData(String chartCapData) {
		this.chartCapData = chartCapData;
	}

	public String getChartCapTitle() {
		return chartCapTitle;
	}

	public void setChartCapTitle(String chartCapTitle) {
		this.chartCapTitle = chartCapTitle;
	}

	public String getChartCapDraw() {
		return chartCapDraw;
	}

	public void setChartCapDraw(String chartCapDraw) {
		this.chartCapDraw = chartCapDraw;
	}

	public String getChartCapCompanies() {
		return chartCapCompanies;
	}

	public void setChartCapCompanies(String chartCapCompanies) {
		this.chartCapCompanies = chartCapCompanies;
	}

	public String getTotalVariation() {
		return totalVariation;
	}

	public void setTotalVariation(String totalVariation) {
		this.totalVariation = totalVariation;
	}

	public boolean isTodayUp() {
		return todayUp;
	}

	public void setTodayUp(boolean todayUp) {
		this.todayUp = todayUp;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(totalValue);
		dest.writeString(totalGain);
		dest.writeString(totalPlusMinusValue);
		dest.writeString(liquidity);
		dest.writeByte((byte) (up ? 1 : 0)); // myBoolean = in.readByte() == 1;
		dest.writeByte((byte) (todayUp ? 1 : 0));
		dest.writeString(yieldYear);
		dest.writeString(yieldYearPerc);
		dest.writeString(lastUpdate);

		dest.writeString(gainPerformance);
		dest.writeString(performancePerformance);
		dest.writeString(yieldPerformance);
		dest.writeString(taxesPerformace);

		dest.writeString(chartColors);
		dest.writeString(chartData);
		dest.writeString(chartDate);
		dest.writeString(chartDraw);
		
		dest.writeString(chartSectorData);
		dest.writeString(chartSectorTitle);
		dest.writeString(chartSectorDraw);
		dest.writeString(chartSectorCompanies);
		
		dest.writeString(chartCapData);
		dest.writeString(chartCapTitle);
		dest.writeString(chartCapDraw);
		dest.writeString(chartCapCompanies);
		
		dest.writeTypedList(equities);
		dest.writeTypedList(shareValues);
		dest.writeTypedList(accounts);
		
		dest.writeString(totalVariation);
	}

	private void readFromParcel(Parcel in) {
		totalValue = in.readString();
		totalGain = in.readString();
		totalPlusMinusValue = in.readString();
		liquidity = in.readString();
		up = in.readByte() == 1;
		todayUp = in.readByte() == 1;
		yieldYear = in.readString();
		yieldYearPerc = in.readString();
		lastUpdate = in.readString();
		gainPerformance = in.readString();
		performancePerformance = in.readString();
		yieldPerformance = in.readString();
		taxesPerformace = in.readString();
		chartColors = in.readString();
		chartData = in.readString();
		chartDate = in.readString();
		chartDraw = in.readString();
		chartSectorData = in.readString();
		chartSectorTitle = in.readString();
		chartSectorDraw = in.readString();
		chartSectorCompanies = in.readString();
		chartCapData = in.readString();
		chartCapTitle = in.readString();
		chartCapDraw = in.readString();
		chartCapCompanies = in.readString();
		equities = new ArrayList<Equity>();
		in.readTypedList(equities, Equity.CREATOR);
		shareValues = new ArrayList<ShareValue>();
		in.readTypedList(shareValues, ShareValue.CREATOR);
		accounts = new ArrayList<Account>();
		in.readTypedList(accounts, Account.CREATOR);
		totalVariation = in.readString();
	}

	public static final Parcelable.Creator<Portfolio> CREATOR = new Parcelable.Creator<Portfolio>() {
		public Portfolio createFromParcel(Parcel in) {
			return new Portfolio(in);
		}

		public Portfolio[] newArray(int size) {
			return new Portfolio[size];
		}
	};

}
