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

package fr.cph.stock.external;

import fr.cph.stock.entities.Company;
import fr.cph.stock.entities.CurrencyData;
import fr.cph.stock.entities.Index;
import fr.cph.stock.enumtype.Currency;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

/**
 * This interface represents the access to external sources
 *
 * @author Carl-Philipp Harmant
 */
public interface ExternalDataAccess {

	/**
	 * Get companies data
	 *
	 * @param ids a list of id
	 * @return a list of company
	 */
	Stream<Company> getCompaniesData(List<String> ids);

	/**
	 * Get company history data
	 *
	 * @param id   the id
	 * @param from date from
	 * @param to   date to
	 * @return a list of company
	 */
	Stream<Company> getCompanyDataHistory(String id, Date from, Date to);

	/**
	 * Get currency data
	 *
	 * @param currency the currency
	 * @return a list of currency data
	 */
	Stream<CurrencyData> getCurrencyData(Currency currency);

	/**
	 * Get index data
	 *
	 * @param id the id
	 * @return an Index
	 */
	Index getIndexData(String id);
}
