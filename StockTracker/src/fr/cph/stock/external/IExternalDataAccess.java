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

public interface IExternalDataAccess {

//	Company getCompanyData(String id) throws  YahooException;

	List<CurrencyData> getCurrencyData(Currency currency) throws YahooException;

	List<Company> getCompaniesData(List<String> yahooIds) throws YahooException;
	
	Company getCompanyInfo(Company company) throws YahooException;

	Index getIndexData(String yahooId) throws YahooException;

	List<Index> getIndexDataHistory(String yahooId, Date from, Date to) throws YahooException;

	List<Company> getCompanyDataHistory(String yahooId, Date from, Date to) throws YahooException;
	
}
