/**
 * Copyright 2017 Carl-Philipp Harmant
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

package fr.cph.stock.business;

import fr.cph.stock.entities.Index;
import fr.cph.stock.exception.YahooException;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public interface IndexBusiness {

	/**
	 * Get index information
	 *
	 * @param yahooId the yahoo id
	 * @param from    the from date
	 * @param to      the to date
	 * @return a list of index
	 */
	List<Index> getIndexes(String yahooId, Date from, Date to);

	/**
	 * Update an index
	 *
	 * @param yahooId the yahoo id
	 * @throws YahooException the yahoo exception
	 */
	void updateIndex(String yahooId) throws YahooException;

	/**
	 * Don't remember what it does
	 *
	 * @param yahooId  the yahoo id
	 * @param timeZone the timezone
	 * @throws YahooException the yahoo exception
	 */
	void checkUpdateIndex(String yahooId, TimeZone timeZone) throws YahooException;
}
