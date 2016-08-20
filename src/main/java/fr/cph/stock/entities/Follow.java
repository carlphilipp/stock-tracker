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

/**
 * This class represents a company that is followed by the user
 *
 * @author Carl-Philipp Harmant
 *
 */
public class Follow {

	/** Company **/
	private Company company;
	/** Id **/
	private int id;
	/** Company Id **/
	private int companyId;
	/** User id **/
	private int userId;
	/** Lower limit **/
	private Double lowerLimit;
	/** Higher limit **/
	private Double higherLimit;

	// Not stored in DB. Calculated at run time
	/** Gap lower limit **/
	private Double gapLowerLimit;
	/** Gap higher limit **/
	private Double gapHigherLimit;

	/**
	 * Get the company the is followed
	 *
	 * @return the company
	 */
	public final Company getCompany() {
		return company;
	}

	/**
	 * Set company
	 *
	 * @param company
	 *            the company
	 */
	public final void setCompany(final Company company) {
		this.company = company;
	}

	/**
	 * Get Id
	 *
	 * @return the id
	 */
	public final int getId() {
		return id;
	}

	/**
	 * Set id
	 *
	 * @param id
	 *            the id
	 */
	public final void setId(final int id) {
		this.id = id;
	}

	/**
	 * Get company id
	 *
	 * @return the company id
	 */
	public final int getCompanyId() {
		return companyId;
	}

	/**
	 * Set company id
	 *
	 * @param companyId
	 *            the company id
	 */
	public final void setCompanyId(final int companyId) {
		this.companyId = companyId;
	}

	/**
	 * Get user id
	 *
	 * @return the user id
	 */
	public final int getUserId() {
		return userId;
	}

	/**
	 * Set user id
	 *
	 * @param userId
	 *            the user id
	 */
	public final void setUserId(final int userId) {
		this.userId = userId;
	}

	/**
	 * Get lower limit
	 *
	 * @return the lower limit
	 */
	public final Double getLowerLimit() {
		return lowerLimit;
	}

	/**
	 * Set Lower limit
	 *
	 * @param lowerLimit
	 *            the lower limit
	 */
	public final void setLowerLimit(final Double lowerLimit) {
		this.lowerLimit = lowerLimit;
	}

	/**
	 * Get higher limit
	 *
	 * @return the higher limit
	 */
	public final Double getHigherLimit() {
		return higherLimit;
	}

	/**
	 * Set higher limit
	 *
	 * @param higherLimit
	 *            the higher limit
	 */
	public final void setHigherLimit(final Double higherLimit) {
		this.higherLimit = higherLimit;
	}

	/**
	 * Get gap lower limit
	 *
	 * @return the gap lower limit
	 */
	public final Double getGapLowerLimit() {
		if (gapLowerLimit == null) {
			gapLowerLimit = (company.getQuote() / getLowerLimit() - 1) * 100;
		}
		return gapLowerLimit;
	}

	/**
	 * Set Gap lower limit
	 *
	 * @param gapLowerLimit
	 *            the gap lower limit
	 */
	public final void setGapLowerLimit(final Double gapLowerLimit) {
		this.gapLowerLimit = gapLowerLimit;
	}

	/**
	 * The gap higher limit
	 *
	 * @return the gap higher limit
	 */
	public final Double getGapHigherLimit() {
		if (gapHigherLimit == null) {
			gapHigherLimit = (getHigherLimit() / company.getQuote() - 1) * 100;
		}
		return gapHigherLimit;
	}

	/**
	 * Set gap higher limit
	 *
	 * @param gapHigherLimit
	 *            the gap higher limit
	 */
	public final void setGapHigherLimit(final Double gapHigherLimit) {
		this.gapHigherLimit = gapHigherLimit;
	}

	/**
	 * To String
	 *
	 * @return a string
	 */
	@Override
	public final String toString() {
		return "id: " + id + " - companyId " + companyId + " - userId " + userId + " - lowerLimit " + lowerLimit
			+ " - higherLimit " + higherLimit;
	}
}
