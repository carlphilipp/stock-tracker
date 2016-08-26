package fr.cph.stock.business.impl;

import fr.cph.stock.dao.CompanyDAO;
import fr.cph.stock.entities.Company;
import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.enumtype.Market;
import fr.cph.stock.exception.YahooException;
import fr.cph.stock.external.YahooExternalDataAccess;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CompanyBusinessTest {

	private static final String TICKER = "GOOG";

	@Rule
	public ExpectedException exception = ExpectedException.none();
	@Mock
	private CompanyDAO companyDAO;
	@Mock
	private YahooExternalDataAccess yahoo;

	@Spy
	@InjectMocks
	private CompanyBusinessImpl companyBusiness;

	@Test
	public void testAddCompanies() throws YahooException {
		final List<String> tickers = Collections.singletonList(TICKER);
		final Company company = new Company();
		company.setYahooId(TICKER);
		final List<Company> companies = Collections.singletonList(company);

		when(yahoo.getCompaniesData(tickers)).thenReturn(companies);
		when(companyDAO.selectWithYahooId(TICKER)).thenReturn(null).thenReturn(company);
		when(yahoo.getCompanyInfo(company)).thenReturn(company);

		final List<Company> actual = companyBusiness.addOrUpdateCompanies(tickers);

		verify(yahoo).getCompaniesData(tickers);
		verify(companyDAO, times(2)).selectWithYahooId(TICKER);
		verify(yahoo).getCompanyInfo(company);
		verify(companyDAO).insert(company);
		assertNotNull(actual);
		assertThat(actual, is(not(empty())));
		assertEquals(TICKER, actual.get(0).getYahooId());
	}

	@Test
	public void testUpdateCompanies() throws YahooException {
		final List<String> tickers = Collections.singletonList(TICKER);
		final Company company = new Company();
		company.setYahooId(TICKER);
		final List<Company> companies = Collections.singletonList(company);

		when(yahoo.getCompaniesData(tickers)).thenReturn(companies);
		when(companyDAO.selectWithYahooId(TICKER)).thenReturn(company);
		when(yahoo.getCompanyInfo(company)).thenReturn(company);

		final List<Company> actual = companyBusiness.addOrUpdateCompanies(tickers);

		verify(yahoo).getCompaniesData(tickers);
		verify(companyDAO).update(company);
		assertNotNull(actual);
		assertThat(actual, is(not(empty())));
		assertEquals(TICKER, actual.get(0).getYahooId());
	}

	@Test
	public void testUpdateCompaniesNotRealTime() throws YahooException {
		final Company company = new Company();
		company.setYahooId(TICKER);
		final List<Company> companies = Collections.singletonList(company);

		when(companyDAO.selectAllCompany(false)).thenReturn(companies);
		when(yahoo.getCompanyDataHistory(eq(TICKER), isA(Date.class), eq(null))).thenReturn(companies);

		companyBusiness.updateCompaniesNotRealTime();

		verify(companyDAO).selectAllCompany(false);
		verify(companyDAO).update(company);
	}

	@Test
	public void testDeleteCompany() throws YahooException {
		final Company company = new Company();
		company.setYahooId(TICKER);

		companyBusiness.deleteCompany(company);

		verify(companyDAO).delete(company);
	}

	@Test
	public void testAddOrUpdateCompaniesLimitedRequest() throws YahooException {
		final List<String> tickers = new ArrayList<>();
		tickers.add(TICKER);

		final String actual = companyBusiness.addOrUpdateCompaniesLimitedRequest(tickers);

		assertNotNull(actual);
		assertThat(actual.length(), is(0));
	}

	@Test
	public void testAddOrUpdateCompaniesLimitedRequestMax() throws YahooException {
		final List<String> tickers = Collections.nCopies(16, TICKER);

		final String actual = companyBusiness.addOrUpdateCompaniesLimitedRequest(tickers);

		assertNotNull(actual);
		assertThat(actual.length(), is(0));
	}

	@Test
	public void testCreateManualCompany() {
		final Company company = new Company();
		when(companyDAO.selectWithYahooId(isA(String.class))).thenReturn(company);

		final Company actual = companyBusiness.createManualCompany("Company name", "industry", "sector", Currency.EUR, 5.0);

		assertNotNull(actual);
		verify(companyDAO).insert(isA(Company.class));
		verify(companyDAO).selectWithYahooId(isA(String.class));
	}

	@Test
	public void testUpdateCompanyManual() {
		final Company company = new Company();
		when(companyDAO.select(2)).thenReturn(company);

		companyBusiness.updateCompanyManual(2, 5.0);

		verify(companyDAO).update(isA(Company.class));
	}

	@Test
	public void testAddCompany() throws YahooException {
		final List<String> tickers = Collections.singletonList(TICKER);
		final Company company = new Company();
		company.setYahooId(TICKER);
		final List<Company> companies = Collections.singletonList(company);

		when(yahoo.getCompaniesData(tickers)).thenReturn(companies);
		when(companyDAO.selectWithYahooId(TICKER)).thenReturn(null).thenReturn(company);
		when(yahoo.getCompanyInfo(company)).thenReturn(company);

		final Company actual = companyBusiness.addOrUpdateCompany(TICKER);

		verify(yahoo).getCompaniesData(tickers);
		verify(companyDAO, times(2)).selectWithYahooId(TICKER);
		verify(yahoo).getCompanyInfo(company);
		verify(companyDAO).insert(company);
		assertNotNull(actual);
		assertEquals(TICKER, actual.getYahooId());
	}

	@Test
	public void testUpdateCompany() throws YahooException {
		final List<String> tickers = Collections.singletonList(TICKER);
		final Company company = new Company();
		company.setYahooId(TICKER);
		company.setMarket(Market.PAR);
		final List<Company> companies = Collections.singletonList(company);

		when(yahoo.getCompaniesData(tickers)).thenReturn(companies);
		when(companyDAO.selectWithYahooId(TICKER)).thenReturn(company);
		when(yahoo.getCompanyInfo(company)).thenReturn(company);

		final Company actual = companyBusiness.addOrUpdateCompany(TICKER);

		verify(yahoo).getCompaniesData(tickers);
		verify(companyDAO).update(company);
		assertNotNull(actual);
		assertEquals(TICKER, actual.getYahooId());
	}

	@Test
	public void testCleanDB() {
		final List<Integer> companiesId = new ArrayList<>();
		companiesId.add(5);
		companiesId.add(6);

		when(companyDAO.selectAllUnusedCompanyIds()).thenReturn(companiesId);

		companyBusiness.cleanDB();

		verify(companyDAO).selectAllUnusedCompanyIds();
		verify(companyDAO, times(2)).delete(isA(Company.class));
	}
}
