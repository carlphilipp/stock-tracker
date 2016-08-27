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

/**
 * This class represents a company that is followed by the user
 *
 * @author Carl-Philipp Harmant
 */
@Data
public class Follow {

	/**
	 * Company
	 **/
	private Company company;
	/**
	 * Id
	 **/
	private int id;
	/**
	 * Company Id
	 **/
	private int companyId;
	/**
	 * User id
	 **/
	private int userId;
	/**
	 * Lower limit
	 **/
	private Double lowerLimit;
	/**
	 * Higher limit
	 **/
	private Double higherLimit;

	// Not stored in DB. Calculated and cached at run time
	/**
	 * Gap lower limit
	 **/
	private Double gapLowerLimit;
	/**
	 * Gap higher limit
	 **/
	private Double gapHigherLimit;

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
}
