/**
 * Copyright 2013 Carl-Philipp Harmant
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.cph.stock.dao;

import fr.cph.stock.entities.Company;
import fr.cph.stock.entities.Equity;
import fr.cph.stock.entities.Portfolio;
import fr.cph.stock.entities.User;
import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.enumtype.Market;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static fr.cph.stock.util.Constants.PASSWORD;
import static org.junit.Assert.assertEquals;

public class EquityDaoImplTest {

	private User user;
	private Portfolio portfolio;
	private Company company;
	private UserDAO daoUser;
	private PortfolioDAO daoPortfolio;
	private CompanyDAO daoCompany;

	@Before
	public void setUp() {
		daoUser = new UserDAO();
		daoPortfolio = new PortfolioDAO();
		daoCompany = new CompanyDAO();
		String uuid = UUID.randomUUID().toString().substring(0, 5);
		user = new User(uuid, PASSWORD);
		user.setEmail("carl@carl.com");
		daoUser.insert(user);
		user = daoUser.selectWithLogin(uuid);

		portfolio = new Portfolio();
		portfolio.setCurrency(Currency.EUR);
		portfolio.setUserId(user.getId());
		daoPortfolio.insert(portfolio);
		portfolio = daoPortfolio.selectPortfolioFromUserIdWithEquities(user.getId(), null, null);

		company = new Company();
		company.setCurrency(Currency.USD);
		company.setIndustry("Dunno");
		company.setMarket(Market.PARIS);
		company.setName("Natixis");
		company.setQuote(5.0);
		company.setSector("Banque");
		company.setYahooId("NK.ECA");
		company.setYield(5.0);
		company.setRealTime(true);
		company.setFound(false);
		daoCompany.insert(company);
		company = daoCompany.selectWithYahooId("NK.ECA");
	}

	@After
	public void after() {
		daoCompany.delete(company);
		daoPortfolio.delete(portfolio);
		daoUser.delete(user);
	}

	@Test
	public void testCRUDEquity() {
		IDAO<Equity> dao = new EquityDAO();
		Equity equity = new Equity();
		equity.setPortfolioId(portfolio.getId());
		equity.setCompany(company);
		equity.setCompanyId(company.getId());

		equity.setQuantity(17.0);
		equity.setUnitCostPrice(4.5);
		equity.setParity(1.0);

		// insert
		dao.insert(equity);
		PortfolioDAO portDao = new PortfolioDAO();
		Portfolio portfolio = portDao.selectPortfolioFromUserIdWithEquities(user.getId(), null, null);
		assertEquals(1, portfolio.getEquities().size());
		equity = portfolio.getEquities().get(0);
		assertEquals(17.0, equity.getQuantity(), 0.00001);
		assertEquals(4.5, equity.getUnitCostPrice(), 0.00001);
		equity.setQuantity(20.0);

		// update
		dao.update(equity);
		portfolio = portDao.selectPortfolioFromUserIdWithEquities(user.getId(), null, null);
		assertEquals(1, portfolio.getEquities().size());
		equity = portfolio.getEquities().get(0);
		assertEquals(20.0, equity.getQuantity(), 0.00001);

		// delete
		dao.delete(equity);

	}

}
