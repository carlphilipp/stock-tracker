package fr.cph.stock.service.impl;

import fr.cph.stock.config.AppProperties;
import fr.cph.stock.repository.CompanyRepository;
import fr.cph.stock.entities.Company;
import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.enumtype.Market;
import fr.cph.stock.exception.YahooException;
import fr.cph.stock.external.ExternalDataAccess;
import fr.cph.stock.util.mail.MailService;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CompanyServiceTest {

	private static final String TICKER = "GOOG";

	@Rule
	public ExpectedException exception = ExpectedException.none();
	@Mock
	private CompanyRepository companyRepository;
	@Mock
	private ExternalDataAccess yahoo;
	@Mock
	private AppProperties appProperties;
	@Mock
	private MailService mailService;

	@InjectMocks
	private CompanyServiceImpl companyBusiness;

	@After
	public void tearDown() {
		reset(yahoo);
	}

	@Test
	public void testAddCompanies() throws YahooException {
		final List<String> tickers = Collections.singletonList(TICKER);
		final Company company = Company.builder().yahooId(TICKER).build();
		final Stream<Company> companies = Stream.of(company);

		when(yahoo.getCompaniesData(tickers)).thenReturn(companies);
		when(companyRepository.selectWithYahooId(TICKER)).thenReturn(Optional.empty()).thenReturn(Optional.of(company));

		companyBusiness.addOrUpdateCompanies(tickers);

		verify(yahoo).getCompaniesData(tickers);
		verify(companyRepository).selectWithYahooId(TICKER);
		verify(companyRepository).insert(company);
	}

	@Test
	public void testUpdateCompanies() throws YahooException {
		final List<String> tickers = Collections.singletonList(TICKER);
		final Company company = new Company();
		company.setYahooId(TICKER);
		final Stream<Company> companies = Stream.of(company);

		when(yahoo.getCompaniesData(tickers)).thenReturn(companies);
		when(companyRepository.selectWithYahooId(TICKER)).thenReturn(Optional.of(company));

		companyBusiness.addOrUpdateCompanies(tickers);

		verify(yahoo).getCompaniesData(tickers);
		verify(companyRepository).update(company);
	}

	@Test
	public void testUpdateCompaniesNotRealTime() throws YahooException {
		final Company company = new Company();
		company.setYahooId(TICKER);
		final List<Company> companies = Collections.singletonList(company);

		when(companyRepository.selectAllCompany(false)).thenReturn(companies);
		when(yahoo.getCompanyDataHistory(eq(TICKER), isA(Date.class), eq(null))).thenReturn(companies.stream());

		companyBusiness.updateCompaniesNotRealTime();

		verify(companyRepository).selectAllCompany(false);
		verify(companyRepository).update(company);
	}

	@Test
	public void testDeleteCompany() throws YahooException {
		final Company company = new Company();
		company.setYahooId(TICKER);

		companyBusiness.deleteCompany(company);

		verify(companyRepository).delete(company);
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
		when(companyRepository.selectWithYahooId(isA(String.class))).thenReturn(Optional.of(company));

		final Optional<Company> actual = companyBusiness.createManualCompany("Company name", "industry", "sector", Currency.EUR, 5.0);

		assertTrue(actual.isPresent());
		verify(companyRepository).insert(isA(Company.class));
		verify(companyRepository).selectWithYahooId(isA(String.class));
	}

	@Test
	public void testUpdateCompanyManual() {
		final Company company = new Company();
		when(companyRepository.select(2)).thenReturn(Optional.of(company));

		companyBusiness.updateCompanyManual(2, 5.0);

		verify(companyRepository).update(isA(Company.class));
	}

	@Test
	public void testAddCompany() throws YahooException {
		final List<String> tickers = Collections.singletonList(TICKER);
		final Company company = new Company();
		company.setYahooId(TICKER);
		final Stream<Company> companies = Stream.of(company);

		when(yahoo.getCompaniesData(tickers)).thenReturn(companies);
		when(companyRepository.selectWithYahooId(TICKER)).thenReturn(Optional.empty()).thenReturn(Optional.of(company));

		final Optional<Company> actual = companyBusiness.addOrUpdateCompany(TICKER);

		verify(yahoo).getCompaniesData(tickers);
		verify(companyRepository, times(2)).selectWithYahooId(TICKER);
		verify(companyRepository).insert(company);
		assertTrue(actual.isPresent());
		assertEquals(TICKER, actual.orElseThrow(AssertionError::new).getYahooId());
	}

	@Test
	public void testUpdateCompany() throws YahooException {
		final List<String> tickers = Collections.singletonList(TICKER);
		final Company company = new Company();
		company.setYahooId(TICKER);
		company.setMarket(Market.PAR);
		final Stream<Company> companies = Stream.of(company);

		when(yahoo.getCompaniesData(tickers)).thenReturn(companies);
		when(companyRepository.selectWithYahooId(TICKER)).thenReturn(Optional.of(company));

		final Optional<Company> actual = companyBusiness.addOrUpdateCompany(TICKER);

		verify(yahoo).getCompaniesData(tickers);
		verify(companyRepository).update(company);
		assertTrue(actual.isPresent());
		assertEquals(TICKER, actual.orElseThrow(AssertionError::new).getYahooId());
	}

	@Test
	public void testCleanDB() {
		final List<Integer> companiesId = new ArrayList<>();
		companiesId.add(5);
		companiesId.add(6);

		when(companyRepository.selectAllUnusedCompanyIds()).thenReturn(companiesId);

		companyBusiness.cleanDB();

		verify(companyRepository).selectAllUnusedCompanyIds();
		verify(companyRepository, times(2)).delete(isA(Company.class));
	}

	@Test
	public void testUpdateAllCompanies() {
		final Company company = new Company();
		company.setYahooId(TICKER);
		company.setRealTime(true);
		company.setMarket(Market.PAR);
		final List<Company> companies = Collections.singletonList(company);

		when(companyRepository.selectAllCompany(true)).thenReturn(companies);

		boolean actual = companyBusiness.updateAllCompanies();

		assertTrue(actual);
		verify(companyRepository).selectAllCompany(true);
	}

	@Test
	public void testUpdateAllCompaniesMore() {
		final Company company = new Company();
		company.setYahooId(TICKER);
		company.setRealTime(true);
		company.setMarket(Market.PAR);
		final List<Company> companies = Collections.nCopies(16, company);

		when(companyRepository.selectAllCompany(true)).thenReturn(companies);

		boolean actual = companyBusiness.updateAllCompanies();

		assertTrue(actual);
		verify(companyRepository).selectAllCompany(true);
	}
}
