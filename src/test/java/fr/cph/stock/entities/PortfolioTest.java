package fr.cph.stock.entities;

import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.enumtype.MarketCapitalization;
import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.*;

import static fr.cph.stock.util.Constants.FUND;
import static fr.cph.stock.util.Constants.UNKNOWN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PortfolioTest {

	private static final String FIDELITY = "Fidelity";
	private static final String HIGH_TECH = "HighTech";
	private static final String GOOGLE = "GOOG";
	private static final String APPLE = "AAPL";
	private static final String CAC40 = "CAC40";

	private Portfolio portfolio;

	@Before
	public void setUp() {
		portfolio = new Portfolio();
		portfolio.setCurrency(Currency.USD);
	}

	@Test
	public void testGetTotalValue() {
		Double actual = portfolio.getTotalValue();
		assertEquals(0d, actual, 0.1);
	}

	@Test
	public void testGetTotalValueWithLiquidity() {
		portfolio.setLiquidity(1000d);
		Double actual = portfolio.getTotalValue();
		assertEquals(1000d, actual, 0.1);
	}

	@Test
	public void testCompute() {
		portfolio.setEquities(createEquities());
		portfolio.compute();

		assertEquals(66d, portfolio.getTotalQuantity(), 0.1);
		assertEquals(30500d, portfolio.getTotalValue(), 0.1);
		assertEquals(0d, portfolio.getYieldYear(), 0.1);
		assertEquals(12700d, portfolio.getTotalGain(), 0.1);
		assertNotNull(portfolio.getLastCompanyUpdate());
	}

	@Test
	public void testGetChartSectorData() {
		portfolio.setEquities(createEquities());
		Map<String, Double> actual = portfolio.getChartSectorData();
		assertNotNull(actual);
		assertEquals(25500d, actual.get(FUND), 0.1);
		assertEquals(4000d, actual.get(HIGH_TECH), 0.1);
		assertEquals(1000d, actual.get(UNKNOWN), 0.1);
	}

	@Test
	public void testGetChartShareValueData() {
		portfolio.setShareValues(createShareValues());
		Map<Date, Double> actual = portfolio.getChartShareValueData();
		assertNotNull(actual);
		assertThat(actual.size(), is(2));
		Iterator<Date> iterator = actual.keySet().iterator();
		assertEquals(100d, actual.get(iterator.next()), 0.1);
		assertEquals(50d, actual.get(iterator.next()), 0.1);
	}

	@Test
	public void testGetChartCapData() {
		portfolio.setEquities(createEquities());
		Map<String, Double> actual = portfolio.getChartCapData();
		assertNotNull(actual);
		assertEquals(1000d, actual.get(MarketCapitalization.MEGA_CAP.getValue()), 0.1);
		assertEquals(4000d, actual.get(MarketCapitalization.LARGE_CAP.getValue()), 0.1);
		assertEquals(25500d, actual.get(UNKNOWN), 0.1);
	}

	@Test
	public void testGetCompaniesYahooIdRealTime() {
		portfolio.setEquities(createEquities());
		List<String> actual = portfolio.getCompaniesYahooIdRealTime();
		assertNotNull(actual);
		assertThat(actual, containsInAnyOrder(GOOGLE, APPLE));
	}

	@Test
	public void testAddIndexes() {
		final List<Index> indexes = new ArrayList<>();
		final Index index = new Index();
		index.setYahooId(CAC40);
		indexes.add(index);

		portfolio.addIndexes(indexes);
		final Map<String, List<Index>> actual = portfolio.getIndexes();
		assertNotNull(actual);
		assertThat(actual.size(), is(1));
		assertNotNull(actual.get(CAC40));
	}

	@Test
	public void testGetPortfolioReview() {
		portfolio.setEquities(createEquities());
		final String actual = portfolio.getPortfolioReview();
		assertNotNull(actual);
	}

	@Test
	public void testGetAccountByName() {
		portfolio.setAccounts(createAccounts());
		Optional<Account> actual = portfolio.getAccount(FIDELITY);
		assertTrue(actual.isPresent());
	}

	@Test
	public void testGetAccountById() {
		portfolio.setAccounts(createAccounts());
		Optional<Account> actual = portfolio.getAccount(2);
		assertTrue(actual.isPresent());
	}

	@Test
	public void testGetAccountByIdEmpty() {
		Optional<Account> actual = portfolio.getAccount(2);
		assertFalse(actual.isPresent());
	}

	@Test
	public void testGetFirstAccount() {
		portfolio.setAccounts(createAccounts());
		Optional<Account> actual = portfolio.getFirstAccount();
		assertTrue(actual.isPresent());
	}

	@Test
	public void testGetSectorByCompanies() {
		portfolio.setEquities(createEquities());
		Map<String, List<Equity>> actual = portfolio.getSectorByCompanies();
		assertNotNull(actual);
		assertThat(actual.size(), is(3));
		assertTrue(actual.containsKey(FUND));
		assertTrue(actual.containsKey(UNKNOWN));
		assertTrue(actual.containsKey(HIGH_TECH));
	}

	@Test
	public void testGetHTMLSectorByCompanies() {
		portfolio.setEquities(createEquities());
		String actual = portfolio.getHTMLSectorByCompanies();
		assertNotNull(actual);
		assertThat(actual.length(), is(not(0)));
		assertThat(actual, startsWith("var companies = ['"));
		assertThat(actual, endsWith("'];"));
	}

	private List<Account> createAccounts() {
		Account account1 = new Account();
		account1.setId(2);
		account1.setName(FIDELITY);
		account1.setDel(false);
		Account account2 = new Account();
		account2.setId(3);
		account2.setDel(true);
		return Arrays.asList(account1, account2);
	}

	private List<ShareValue> createShareValues() {
		Date date = new Date();
		ShareValue shareValue1 = new ShareValue();
		shareValue1.setShareValue(5d);
		shareValue1.setDate(new Date(date.getTime() + 50000));

		ShareValue shareValue2 = new ShareValue();
		shareValue2.setShareValue(10d);
		shareValue2.setDate(date);
		return Arrays.asList(shareValue1, shareValue2);
	}

	private List<Equity> createEquities() {
		Equity google = new Equity();
		google.setQuantity(5d);
		google.setUnitCostPrice(100d);
		Company companyGoogle = new Company();
		companyGoogle.setYahooId(GOOGLE);
		companyGoogle.setQuote(200d);
		companyGoogle.setLastUpdate(new Timestamp(new Date().getTime()));
		companyGoogle.setCurrency(Currency.USD);
		google.setCompany(companyGoogle);
		companyGoogle.setRealTime(true);
		companyGoogle.setFund(false);
		google.setParity(1d);
		google.setMarketCapitalizationType(MarketCapitalization.MEGA_CAP);

		Equity apple = new Equity();
		apple.setQuantity(10d);
		apple.setUnitCostPrice(200d);
		apple.setSectorPersonal(HIGH_TECH);
		Company companyApple = new Company();
		companyApple.setQuote(400d);
		companyApple.setRealTime(true);
		companyApple.setFund(false);
		companyApple.setYahooId(APPLE);
		companyApple.setCurrency(Currency.USD);
		apple.setCompany(companyApple);
		apple.setParity(1d);
		apple.setMarketCapitalizationType(MarketCapitalization.LARGE_CAP);

		Equity fund1 = new Equity();
		fund1.setQuantity(50d);
		fund1.setUnitCostPrice(300d);
		Company companyFund1 = new Company();
		companyFund1.setQuote(500d);
		companyFund1.setRealTime(false);
		companyFund1.setFund(true);
		companyFund1.setYahooId("FUND1");
		companyFund1.setCurrency(Currency.USD);
		fund1.setCompany(companyFund1);
		fund1.setParity(1d);

		Equity fund2 = new Equity();
		fund2.setQuantity(1d);
		fund2.setUnitCostPrice(300d);
		Company companyFund2 = new Company();
		companyFund2.setQuote(500d);
		companyFund2.setRealTime(false);
		companyFund2.setFund(true);
		companyFund2.setYahooId("FUND2");
		companyFund2.setCurrency(Currency.USD);
		fund2.setCompany(companyFund2);
		fund2.setParity(1d);
		return Arrays.asList(google, apple, fund1, fund2);
	}
}
