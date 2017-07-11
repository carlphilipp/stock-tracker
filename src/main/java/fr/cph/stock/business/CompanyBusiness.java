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

import fr.cph.stock.entities.Company;
import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.exception.YahooException;

import java.util.List;
import java.util.Optional;

public interface CompanyBusiness {

	void updateCompaniesNotRealTime();

	void deleteCompany(Company company);

	String addOrUpdateCompaniesLimitedRequest(List<String> companiesYahooIdRealTime) throws YahooException;

	Optional<Company> createManualCompany(String name, String industry, String sector, Currency currency, double quote);

	Optional<Company> addOrUpdateCompany(String ticker) throws YahooException;

	void updateCompanyManual(Integer companyId, Double newQuote);

	void cleanDB();

	boolean updateAllCompanies();
}
