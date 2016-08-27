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

import lombok.Data;

import java.util.Date;

/**
 * This class represents an Index, like cac40 or s&p500
 *
 * @author Carl-Philipp Harmant
 */
@Data
public class Index {

	/**
	 * Id
	 **/
	private int id;
	/**
	 * Yahoo Id
	 **/
	private String yahooId;
	/**
	 * Value
	 **/
	private Double value;
	/**
	 * Date
	 **/
	private Date date;
	/**
	 * Share Value
	 **/
	private Double shareValue;

	/**
	 * Get the date
	 *
	 * @return the date
	 */
	public final Date getDate() {
		return date != null ? (Date) date.clone() : null;
	}

	/**
	 * Set the date
	 *
	 * @param date the date
	 */
	public final void setDate(final Date date) {
		this.date = (Date) date.clone();
	}
}
