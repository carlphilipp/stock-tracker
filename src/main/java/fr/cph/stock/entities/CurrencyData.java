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
import lombok.Data;

import java.util.Date;

/**
 * This class represents currency data. It will get 2 currencies and get the value of the first currency depending on the second
 *
 * @author Carl-Philipp Harmant
 */
@Data
public class CurrencyData {

	/**
	 * id
	 **/
	private int id;
	/**
	 * First currency
	 **/
	private Currency currency1;
	/**
	 * Second currency
	 **/
	private Currency currency2;
	/**
	 * Value of the first currency compare to the second
	 **/
	private Double value;
	/**
	 * Last update of the value
	 **/
	private Date lastUpdate;


	public final Date getLastUpdate() {
		return lastUpdate != null ? (Date) lastUpdate.clone() : null;
	}

	public final void setLastUpdate(final Date lastUpdate) {
		this.lastUpdate = (Date) lastUpdate.clone();
	}

	@Override
	public final String toString() {
		return currency1 + " " + currency2 + " " + lastUpdate;
	}
}
