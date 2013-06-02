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

package fr.cph.stock.external;

import java.util.Date;
import java.util.List;

import fr.cph.stock.entities.Company;
import fr.cph.stock.entities.CurrencyData;
import fr.cph.stock.entities.Index;
import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.exception.YahooException;

/**
 * This interface represents the access to external sources
 * 
 * @author Carl-Philipp Harmant
 * 
 */
public interface IExternalDataAccess {

	/**
	 * Get companies data
	 * 
	 * @param ids
	 *            a list of id
	 * @return a list of company
	 * @throws YahooException
	 */
	List<Company> getCompaniesData(List<String> ids) throws YahooException;

	/**
	 * Get company history data
	 * 
	 * @param id
	 *            the id
	 * @param from
	 *            date from
	 * @param to
	 *            date to
	 * @return a list of company
	 * @throws YahooException
	 */
	List<Company> getCompanyDataHistory(String id, Date from, Date to) throws YahooException;

	/**
	 * Get company info
	 * 
	 * @param company
	 *            the company
	 * @return a company
	 * @throws YahooException
	 */
	Company getCompanyInfo(Company company) throws YahooException;

	/**
	 * Get currency data
	 * 
	 * @param currency
	 *            the currency
	 * @return a list of currency data
	 * @throws YahooException
	 */
	List<CurrencyData> getCurrencyData(Currency currency) throws YahooException;

	/**
	 * Get index data
	 * 
	 * @param id
	 *            the id
	 * @return an Index
	 * @throws YahooException
	 */
	Index getIndexData(String id) throws YahooException;

	/**
	 * Get index history
	 * 
	 * @param id
	 *            the index id
	 * @param from
	 *            date from
	 * @param to
	 *            date to
	 * @return a list of index containing data
	 * @throws YahooException
	 */
	List<Index> getIndexDataHistory(String id, Date from, Date to) throws YahooException;

}
