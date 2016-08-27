package fr.cph.stock.business;

import fr.cph.stock.entities.Company;
import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.exception.YahooException;

import java.util.List;

public interface CompanyBusiness {

	List<Company> addOrUpdateCompanies(List<String> tickers) throws YahooException;

	void updateCompaniesNotRealTime();

	void deleteCompany(Company company);

	String addOrUpdateCompaniesLimitedRequest(List<String> companiesYahooIdRealTime) throws YahooException;

	Company createManualCompany(String name, String industry, String sector, Currency currency, double quote);

	void updateCompanyManual(Integer companyId, Double newQuote);

	Company addOrUpdateCompany(String ticker) throws YahooException;

	void cleanDB();

	boolean updateAllCompanies();
}
