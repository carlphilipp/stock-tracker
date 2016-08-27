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

import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.enumtype.Market;
import lombok.Data;

import java.sql.Timestamp;

/**
 * This class represents a company
 *
 * @author Carl-Philipp Harmant
 */
@Data
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
	/**
	 * Is this company under read time data
	 **/
	private Boolean realTime;
	private Boolean fund;
	private Timestamp lastUpdate;
	private Double change;
	private Double gapYearLow;
	private Double gapYearHigh;
	/**
	 * Manually used by user
	 **/
	private Boolean manual;

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
	 * @return the gap year low
	 */
	public final Double getGapYearLow() {
		if (getYearLow() != null && gapYearLow == null) {
			gapYearLow = (getQuote() / getYearLow() - 1) * 100;
		}
		return gapYearLow;
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
	 * @param changeInPercent the change in percent
	 */
	public final void setChangeInPercent(final String changeInPercent) {
		if (changeInPercent != null) {
			final char c = changeInPercent.charAt(0);
			final String number = c == '+'
				? changeInPercent.substring(1, changeInPercent.length() - 1)
				: changeInPercent.substring(0, changeInPercent.length() - 1);
			setChange(Double.parseDouble(number));
		}
		this.changeInPercent = changeInPercent;
	}
}
