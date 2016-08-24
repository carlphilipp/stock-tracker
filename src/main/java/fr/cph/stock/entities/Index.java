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

import java.util.Date;

/**
 * This class represents an Index, like cac40 or s&p500
 *
 * @author Carl-Philipp Harmant
 */
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
	 * Get the id
	 *
	 * @return the id
	 */
	public final int getId() {
		return id;
	}

	/**
	 * Set the id
	 *
	 * @param id the id
	 */
	public final void setId(final int id) {
		this.id = id;
	}

	/**
	 * Get the yahoo id
	 *
	 * @return the yahoo id
	 */
	public final String getYahooId() {
		return yahooId;
	}

	/**
	 * Set the yahoo id
	 *
	 * @param yahooId the yahoo id
	 */
	public final void setYahooId(final String yahooId) {
		this.yahooId = yahooId;
	}

	/**
	 * Get the value
	 *
	 * @return the value
	 */
	public final Double getValue() {
		return value;
	}

	/**
	 * Set the value
	 *
	 * @param value the value
	 */
	public final void setValue(final Double value) {
		this.value = value;
	}

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

	/**
	 * Get the share value
	 *
	 * @return the share value
	 */
	public final Double getShareValue() {
		return shareValue;
	}

	/**
	 * Set the share value
	 *
	 * @param shareValue the share value
	 */
	public final void setShareValue(final Double shareValue) {
		this.shareValue = shareValue;
	}

	@Override
	public final String toString() {
		return "Index value : " + shareValue;
	}
}
