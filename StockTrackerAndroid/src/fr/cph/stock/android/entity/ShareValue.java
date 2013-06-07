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

public class ShareValue implements Parcelable {

	private String date;
	private String account;
	private String portfolioValue;
	private String shareQuantity;
	private String shareValue;
	private String monthlyYield;
	private boolean up;
	private String commentary;

	public ShareValue() {
	}

	public ShareValue(Parcel in) {
		readFromParcel(in);
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getCommentary() {
		return commentary;
	}

	public void setCommentary(String commentary) {
		this.commentary = commentary;
	}

	public String getShareValue() {
		return shareValue;
	}

	public void setShareValue(String shareValue) {
		this.shareValue = shareValue;
	}

	public boolean isUp() {
		return up;
	}

	public void setUp(boolean up) {
		this.up = up;
	}

	public String getPortfolioValue() {
		return portfolioValue;
	}

	public void setPortfolioValue(String portfolioValue) {
		this.portfolioValue = portfolioValue;
	}

	public String getShareQuantity() {
		return shareQuantity;
	}

	public void setShareQuantity(String shareQuantity) {
		this.shareQuantity = shareQuantity;
	}

	public String getMonthlyYield() {
		return monthlyYield;
	}

	public void setMonthlyYield(String monthlyYield) {
		this.monthlyYield = monthlyYield;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(date);
		dest.writeString(account);
		dest.writeString(portfolioValue);
		dest.writeString(shareQuantity);
		dest.writeString(shareValue);
		dest.writeString(monthlyYield);
		dest.writeString(commentary);
		dest.writeByte((byte) (up ? 1 : 0)); // myBoolean = in.readByte() == 1;
	}

	private void readFromParcel(Parcel in) {
		date = in.readString();
		account = in.readString();
		portfolioValue = in.readString();
		shareQuantity = in.readString();
		shareValue = in.readString();
		monthlyYield = in.readString();
		commentary = in.readString();
		up = in.readByte() == 1;

	}

	public static final Parcelable.Creator<ShareValue> CREATOR = new Parcelable.Creator<ShareValue>() {
		public ShareValue createFromParcel(Parcel in) {
			return new ShareValue(in);
		}

		public ShareValue[] newArray(int size) {
			return new ShareValue[size];
		}
	};

}
