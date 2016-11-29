package fr.cph.stock.entities;

import com.google.gson.JsonObject;
import fr.cph.stock.enumtype.Currency;
import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.*;

import static fr.cph.stock.enumtype.MarketCapitalization.LARGE_CAP;
import static fr.cph.stock.enumtype.MarketCapitalization.MEGA_CAP;
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
		assertEquals(1000d, actual.get(MEGA_CAP.getValue()), 0.1);
		assertEquals(4000d, actual.get(LARGE_CAP.getValue()), 0.1);
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
		final Index index = Index.builder().yahooId(CAC40).build();
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
		verifyHTML(actual);
	}

	@Test
	public void testGetGapByCompanies() {
		portfolio.setEquities(createEquities());
		Map<String, List<Equity>> actual = portfolio.getGapByCompanies();
		assertNotNull(actual);
		assertThat(actual.size(), is(3));
		assertTrue(actual.containsKey(LARGE_CAP.getValue()));
		assertTrue(actual.containsKey(MEGA_CAP.getValue()));
		assertTrue(actual.containsKey(UNKNOWN));
	}

	@Test
	public void testGetHTMLCapByCompanies() {
		portfolio.setEquities(createEquities());
		String actual = portfolio.getHTMLCapByCompanies();
		verifyHTML(actual);
	}

	@Test
	public void testGetJonObject() {
		portfolio.setEquities(createEquities());
		portfolio.setShareValues(new ArrayList<>());
		portfolio.setAccounts(createAccounts());
		portfolio.setLastCompanyUpdate(Calendar.getInstance().getTime());
		JsonObject actual = portfolio.getJSONObject();
		assertNotNull(actual);
	}

	private void verifyHTML(final String html) {
		assertNotNull(html);
		assertThat(html.length(), is(not(0)));
		assertThat(html, startsWith("var companies = ['"));
		assertThat(html, endsWith("'];"));
	}

	private List<Account> createAccounts() {
		Account account1 = Account.builder().id(2).name(FIDELITY).del(false).currency(Currency.USD).build();
		Account account2 = Account.builder().id(3).del(true).currency(Currency.USD).build();
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
		Company companyGoogle = Company.builder()
			.yahooId(GOOGLE)
			.quote(200d)
			.lastUpdate(new Timestamp(new Date().getTime()))
			.currency(Currency.USD)
			.realTime(true)
			.fund(false)
			.build();

		Equity google = Equity.builder()
			.quantity(5d)
			.unitCostPrice(100d)
			.parity(1d)
			.marketCapitalizationType(MEGA_CAP)
			.company(companyGoogle)
			.build();

		Company companyApple = Company.builder()
			.quote(400d)
			.realTime(true)
			.fund(false)
			.yahooId(APPLE)
			.currency(Currency.USD)
			.build();

		Equity apple = Equity.builder()
			.quantity(10d)
			.unitCostPrice(200d)
			.sectorPersonal(HIGH_TECH)
			.company(companyApple)
			.parity(1d)
			.marketCapitalizationType(LARGE_CAP)
			.build();

		Company companyFund1 = Company.builder()
			.quote(500d)
			.realTime(false)
			.fund(true)
			.yahooId("FUND1")
			.currency(Currency.USD)
			.build();

		Company companyFund2 = Company.builder()
			.quote(500d)
			.realTime(false)
			.fund(true)
			.yahooId("FUND2")
			.currency(Currency.USD)
			.build();

		Equity fund1 = Equity.builder()
			.quantity(50d)
			.unitCostPrice(300d)
			.parity(1d)
			.company(companyFund1)
			.build();

		Equity fund2 = Equity.builder()
			.quantity(1d)
			.unitCostPrice(300d)
			.parity(1d)
			.company(companyFund2)
			.build();

		return Arrays.asList(google, apple, fund1, fund2);
	}
}
