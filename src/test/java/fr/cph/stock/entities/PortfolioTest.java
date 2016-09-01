package fr.cph.stock.entities;

import fr.cph.stock.enumtype.MarketCapitalization;
import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.*;

import static fr.cph.stock.util.Constants.FUND;
import static fr.cph.stock.util.Constants.UNKNOWN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PortfolioTest {

	private Portfolio portfolio;

	@Before
	public void setUp() {
		portfolio = new Portfolio();
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
		assertEquals(4000d, actual.get("HighTech"), 0.1);
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

	private List<ShareValue> createShareValues() {
		Date date = new Date();
		List<ShareValue> shareValues = new ArrayList<>();

		ShareValue shareValue1 = new ShareValue();
		shareValue1.setShareValue(5d);
		shareValue1.setDate(new Date(date.getTime() + 50000));
		shareValues.add(shareValue1);

		ShareValue shareValue2 = new ShareValue();
		shareValue2.setShareValue(10d);
		shareValue2.setDate(date);
		shareValues.add(shareValue2);
		return shareValues;
	}

	private List<Equity> createEquities() {
		Equity google = new Equity();
		google.setQuantity(5d);
		google.setUnitCostPrice(100d);
		Company companyGoogle = new Company();
		companyGoogle.setQuote(200d);
		companyGoogle.setLastUpdate(new Timestamp(new Date().getTime()));
		google.setCompany(companyGoogle);
		companyGoogle.setRealTime(true);
		companyGoogle.setFund(false);
		google.setParity(1d);
		google.setMarketCapitalizationType(MarketCapitalization.MEGA_CAP);

		Equity apple = new Equity();
		apple.setQuantity(10d);
		apple.setUnitCostPrice(200d);
		apple.setSectorPersonal("HighTech");
		Company companyApple = new Company();
		companyApple.setQuote(400d);
		companyApple.setRealTime(true);
		companyApple.setFund(false);
		apple.setCompany(companyApple);
		apple.setParity(1d);
		apple.setMarketCapitalizationType(MarketCapitalization.LARGE_CAP);

		Equity fund1 = new Equity();
		fund1.setQuantity(50d);
		fund1.setUnitCostPrice(300d);
		Company companyFund1 = new Company();
		companyFund1.setQuote(500d);
		companyFund1.setRealTime(true);
		companyFund1.setFund(true);
		fund1.setCompany(companyFund1);
		fund1.setParity(1d);

		Equity fund2 = new Equity();
		fund2.setQuantity(1d);
		fund2.setUnitCostPrice(300d);
		Company companyFund2 = new Company();
		companyFund2.setQuote(500d);
		companyFund2.setRealTime(true);
		companyFund2.setFund(true);
		fund2.setCompany(companyFund2);
		fund2.setParity(1d);
		return Arrays.asList(google, apple, fund1, fund2);
	}
}
