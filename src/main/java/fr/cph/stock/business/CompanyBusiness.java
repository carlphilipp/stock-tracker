package fr.cph.stock.business;

import fr.cph.stock.entities.Company;
import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.exception.YahooException;

import java.util.List;

public interface CompanyBusiness {

	List<Company> addOrUpdateCompanies(final List<String> tickers) throws YahooException;

	void updateCompaniesNotRealTime();

	void deleteCompany(final Company company);

	String addOrUpdateCompaniesLimitedRequest(final List<String> companiesYahooIdRealTime) throws YahooException;

	Company createManualCompany(final String name, final String industry, final String sector, final Currency currency, final double quote);

	void updateCompanyManual(final Integer companyId, final Double newQuote);

	Company addOrUpdateCompany(final String ticker) throws YahooException;

	void cleanDB();

	boolean updateAllCompanies();
}
