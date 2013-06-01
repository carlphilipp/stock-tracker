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

import java.util.UUID;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.cph.stock.entities.Company;
import fr.cph.stock.entities.Equity;
import fr.cph.stock.entities.Portfolio;
import fr.cph.stock.entities.User;
import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.enumtype.Market;

public class PortfolioDaoImplTest {
	private User user;
	private Portfolio portfolio;
	private Company company;
	private UserDaoImpl daoUser;
	private CompanyDaoImpl daoCompany;
	private EquityDaoImpl daoEquity;
	private Equity equity;
	
	@Before
	public void setUp(){
		daoUser = new UserDaoImpl();
		String uuid = UUID.randomUUID().toString().substring(0, 5);
		user = new User(uuid, "password");
		user.setEmail("carl@carl.com");
		daoUser.insert(user);
		user = daoUser.selectWithLogin(uuid);
	}
	
	@After
	public void after(){
		daoUser.delete(user);
	}
	
	@Test
	public void testCRUDPortfolio(){
		PortfolioDaoImpl dao = new PortfolioDaoImpl();
		
		int userId = user.getId();

		Portfolio portfolio = new Portfolio();
		portfolio.setUserId(userId);
		portfolio.setCurrency(Currency.EUR);
		dao.insert(portfolio);
		
		Portfolio port  = dao.selectPortfolioFromUserIdWithEquities(userId, null, null);
		Assert.assertEquals(userId, port.getUserId());
		Assert.assertEquals(Currency.EUR, port.getCurrency());
		
		port.setCurrency(Currency.USD);
		dao.update(port);
		
		port = dao.selectPortfolioFromUserIdWithEquities(userId, null, null);
		Assert.assertEquals(userId, port.getUserId());
		Assert.assertEquals(Currency.USD, port.getCurrency());
		
		dao.delete(port);
		portfolio = dao.select(port.getId());
		Assert.assertNull(portfolio);
	}
	
	@Test
	public void testselectPortfolioFromUser(){
		PortfolioDaoImpl dao = new PortfolioDaoImpl();
		
		daoEquity  = new EquityDaoImpl();
		
		dao = new PortfolioDaoImpl();
		daoCompany = new CompanyDaoImpl();
		portfolio = new Portfolio();
		portfolio.setCurrency(Currency.EUR);
		portfolio.setUserId(user.getId());
		dao.insert(portfolio);
		portfolio = dao.selectPortfolioFromUserIdWithEquities(user.getId(), null, null);
		
		company = new Company();
		company.setCurrency(Currency.USD);
		company.setIndustry("Dunno");
		company.setMarket(Market.PARIS);
		company.setName("Natixis");
		company.setQuote(5.0);
		company.setSector("Banque");
		company.setYahooId("NK.ECA");
		company.setYield(5.0);
		daoCompany.insert(company);
		company = daoCompany.selectWithYahooId("NK.ECA");
		
		daoEquity = new EquityDaoImpl();
		equity =  new Equity();
		equity.setPortfolioId(portfolio.getId());
		equity.setCompany(company);
		equity.setCompanyId(company.getId());
		
		equity.setQuantity(17.0);
		equity.setUnitCostPrice(4.5);
		equity.setParity(1.0);
		
		daoEquity.insert(equity);
		
		portfolio = dao.selectPortfolioFromUserIdWithEquities(user.getId(), null, null);
		Assert.assertEquals(1, portfolio.getEquities().size());
		equity = portfolio.getEquities().get(0);

		// Clean
		daoCompany.delete(company);
		dao.delete(portfolio);
		daoEquity.delete(equity);
	}
	
	
}
