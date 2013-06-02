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

import java.util.Date;

/**
 * This class represents an Index, like cac40 or s&p500
 * 
 * @author Carl-Philipp Harmant
 * 
 */
public class Index {

	/** Id **/
	private int id;
	/** Yahoo Id **/
	private String yahooId;
	/** Value **/
	private Double value;
	/** Date **/
	private Date date;
	/** Share Value **/
	private Double shareValue;

	/**
	 * Get the id
	 * 
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Set the id
	 * 
	 * @param id
	 *            the id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Get the yahoo id
	 * 
	 * @return the yahoo id
	 */
	public String getYahooId() {
		return yahooId;
	}

	/**
	 * Set the yahoo id
	 * 
	 * @param yahooId
	 *            the yahoo id
	 */
	public void setYahooId(String yahooId) {
		this.yahooId = yahooId;
	}

	/**
	 * Get the value
	 * 
	 * @return the value
	 */
	public Double getValue() {
		return value;
	}

	/**
	 * Set the value
	 * 
	 * @param value
	 *            the value
	 */
	public void setValue(Double value) {
		this.value = value;
	}

	/**
	 * Get the date
	 * 
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Set the date
	 * 
	 * @param date
	 *            the date
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * Get the share value
	 * 
	 * @return the share value
	 */
	public Double getShareValue() {
		return shareValue;
	}

	/**
	 * Set the share value
	 * 
	 * @param shareValue
	 *            the share value
	 */
	public void setShareValue(Double shareValue) {
		this.shareValue = shareValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Index value : " + shareValue;
	}

}
