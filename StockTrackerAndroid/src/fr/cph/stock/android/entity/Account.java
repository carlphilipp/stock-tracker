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

public class Account implements Parcelable {

	public String id;
	public String name;
	public String currency;
	public String liquidity;

	public Account() {

	}

	public Account(Parcel in) {
		readFromParcel(in);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getLiquidity() {
		return liquidity;
	}

	public void setLiquidity(String liquidity) {
		this.liquidity = liquidity;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(name);
		dest.writeString(currency);
		dest.writeString(liquidity);
	}

	private void readFromParcel(Parcel in) {
		id = in.readString();
		name = in.readString();
		currency = in.readString();
		liquidity = in.readString();
	}

	public static final Parcelable.Creator<Account> CREATOR = new Parcelable.Creator<Account>() {
		public Account createFromParcel(Parcel in) {
			return new Account(in);
		}

		public Account[] newArray(int size) {
			return new Account[size];
		}
	};

	@Override
	public String toString() {
		return getName() + " " + getCurrency() + " " + getLiquidity();
	}

}
