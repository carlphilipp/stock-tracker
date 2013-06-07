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

import android.os.Parcel;
import android.os.Parcelable;

public class Equity implements Parcelable {

	private String name;
	private String unitCostPrice;
	private String value;
	private String plusMinusValue;
	private boolean up;
	private boolean upVariation;
	private String quantity;
	private String yieldYear;
	private String yieldUnitCostPrice;
	private String quote;
	private String plusMinusUnitCostPriceValue;
	private String variation;

	public Equity() {

	}

	public Equity(Parcel in) {
		readFromParcel(in);
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getYieldYear() {
		return yieldYear;
	}

	public void setYieldYear(String yieldYear) {
		this.yieldYear = yieldYear;
	}

	public String getYieldUnitCostPrice() {
		return yieldUnitCostPrice;
	}

	public void setYieldUnitCostPrice(String yieldUnitCostPrice) {
		this.yieldUnitCostPrice = yieldUnitCostPrice;
	}

	public String getQuote() {
		return quote;
	}

	public void setQuote(String quote) {
		this.quote = quote;
	}

	public String getPlusMinusUnitCostPriceValue() {
		return plusMinusUnitCostPriceValue;
	}

	public void setPlusMinusUnitCostPriceValue(String plusMinusUnitCostPriceValue) {
		this.plusMinusUnitCostPriceValue = plusMinusUnitCostPriceValue;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUnitCostPrice() {
		return unitCostPrice;
	}

	public void setUnitCostPrice(String unitCostPrice) {
		this.unitCostPrice = unitCostPrice;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getPlusMinusValue() {
		return plusMinusValue;
	}

	public void setPlusMinusValue(String plusMinusValue) {
		this.plusMinusValue = plusMinusValue;
	}

	public boolean isUp() {
		return up;
	}

	public void setUp(boolean up) {
		this.up = up;
	}

	@Override
	public int describeContents() {
		return 0;
	}
	public String getVariation() {
		return variation;
	}

	public void setVariation(String variation) {
		this.variation = variation;
	}

	public boolean isUpVariation() {
		return upVariation;
	}

	public void setUpVariation(boolean upVariation) {
		this.upVariation = upVariation;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(unitCostPrice);
		dest.writeString(value);
		dest.writeString(plusMinusValue);
		dest.writeString(quantity);
		dest.writeString(yieldYear);
		dest.writeString(yieldUnitCostPrice);
		dest.writeString(quote);
		dest.writeString(plusMinusUnitCostPriceValue);
		dest.writeByte((byte) (up ? 1 : 0)); // myBoolean = in.readByte() == 1;
		dest.writeString(variation);
		dest.writeByte((byte) (upVariation ? 1 : 0));
	}

	private void readFromParcel(Parcel in) {
		name = in.readString();
		unitCostPrice = in.readString();
		value = in.readString();
		plusMinusValue = in.readString();
		quantity = in.readString();
		yieldYear = in.readString();
		yieldUnitCostPrice = in.readString();
		quote = in.readString();
		plusMinusUnitCostPriceValue = in.readString();
		up = in.readByte() == 1;
		variation = in.readString();
		upVariation = in.readByte() == 1;
	}

	public static final Parcelable.Creator<Equity> CREATOR = new Parcelable.Creator<Equity>() {
		public Equity createFromParcel(Parcel in) {
			return new Equity(in);
		}

		public Equity[] newArray(int size) {
			return new Equity[size];
		}
	};

}
