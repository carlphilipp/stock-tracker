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

import java.util.Date;

/**
 * This class represents currency data. It will get 2 currencies and get the value of the first currency depending on the second
 *
 * @author Carl-Philipp Harmant
 *
 */
public class CurrencyData {

	/** id **/
	private int id;
	/** First currency **/
	private Currency currency1;
	/** Second currency **/
	private Currency currency2;
	/** Value of the first currency compare to the second **/
	private Double value;
	/** Last update of the value **/
	private Date lastUpdate;

	/**
	 * Getter
	 *
	 * @return the id
	 */
	public final int getId() {
		return id;
	}

	/**
	 * Setter
	 *
	 * @param id
	 *            the id
	 */
	public final void setId(final int id) {
		this.id = id;
	}

	/**
	 * Getter
	 *
	 * @return the value
	 */
	public final Double getValue() {
		return value;
	}

	/**
	 * Setter
	 *
	 * @param value
	 *            the value
	 */
	public final void setValue(final Double value) {
		this.value = value;
	}

	/**
	 * Getter
	 *
	 * @return the last update
	 */
	public final Date getLastUpdate() {
		if (lastUpdate != null) {
			return (Date) lastUpdate.clone();
		} else {
			return null;
		}
	}

	/**
	 * Setter
	 *
	 * @param lastUpdate
	 *            the last update
	 */
	public final void setLastUpdate(final Date lastUpdate) {
		this.lastUpdate = (Date) lastUpdate.clone();
	}

	/**
	 * Getter
	 *
	 * @return the first currency
	 */
	public final Currency getCurrency1() {
		return currency1;
	}

	/**
	 * Setter
	 *
	 * @param currency1
	 *            the first currency
	 */
	public final void setCurrency1(final Currency currency1) {
		this.currency1 = currency1;
	}

	/**
	 * Getter
	 *
	 * @return the second currency
	 */
	public final Currency getCurrency2() {
		return currency2;
	}

	/**
	 * Setter
	 *
	 * @param currency2
	 *            the 2nd currency
	 */
	public final void setCurrency2(final Currency currency2) {
		this.currency2 = currency2;
	}

	/**
	 * To String
	 *
	 * @return a string
	 */
	@Override
	public final String toString() {
		return currency1 + " " + currency2 + " " + lastUpdate;
	}
}
