package fr.cph.stock.entities;

import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static fr.cph.stock.util.Constants.FUND;
import static fr.cph.stock.util.Constants.UNKNOWN;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class PortfolioTest {

	private Portfolio portfolio;

	@Before
	public void setUp() {
		portfolio = new Portfolio();
	}

	@Test
	public void testGetTotalValue() {
		Double actual = portfolio.getTotalValue();
		assertEquals(0d, actual);
	}

	@Test
	public void testGetTotalValueWithLiquidity() {
		portfolio.setLiquidity(1000d);
		Double actual = portfolio.getTotalValue();
		assertEquals(1000d, actual);
	}

	@Test
	public void testCompute() {
		portfolio.setEquities(createEquities());
		portfolio.compute();

		assertEquals(66d, portfolio.getTotalQuantity());
		assertEquals(30500d, portfolio.getTotalValue());
		assertEquals(0d, portfolio.getYieldYear());
		assertEquals(12700d, portfolio.getTotalGain());
		assertNotNull(portfolio.getLastCompanyUpdate());
	}

	@Test
	public void testGetChartSectorData() {
		portfolio.setEquities(createEquities());
		Map<String, Double> actual = portfolio.getChartSectorData();
		assertNotNull(actual);
		assertEquals(25500d , actual.get(FUND));
		assertEquals(4000d , actual.get("HighTech"));
		assertEquals(1000d , actual.get(UNKNOWN));
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
