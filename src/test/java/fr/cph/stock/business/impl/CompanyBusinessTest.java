package fr.cph.stock.business.impl;

import fr.cph.stock.dao.CompanyDAO;
import fr.cph.stock.entities.Company;
import fr.cph.stock.exception.YahooException;
import fr.cph.stock.external.YahooExternalDataAccess;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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
}
