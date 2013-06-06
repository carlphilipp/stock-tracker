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

package fr.cph.stock.business;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.cph.stock.dao.CompanyDaoImpl;
import fr.cph.stock.dao.EquityDaoImpl;
import fr.cph.stock.dao.PortfolioDaoImpl;
import fr.cph.stock.dao.UserDaoImpl;
import fr.cph.stock.entities.Equity;
import fr.cph.stock.entities.Portfolio;
import fr.cph.stock.entities.User;
import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.exception.LoginException;
import fr.cph.stock.exception.YahooException;

public class BusinessTest {

	private User user;
	private UserDaoImpl userDao;
	private Portfolio portfolio;
	private PortfolioDaoImpl portfolioDao;

	@Before
	public void setUp() {
		user = new User();
		userDao = new UserDaoImpl();
		user.setLogin("lolzcarlz");
		user.setPassword("passwordd");
		user.setEmail("poke@poke.com");
		userDao.insert(user);
		user = userDao.selectWithLogin("lolzcarlz");

		portfolio = new Portfolio();
		portfolioDao = new PortfolioDaoImpl();
		portfolio.setCurrency(Currency.EUR);
		portfolio.setUserId(user.getId());
		portfolioDao.insert(portfolio);
		portfolio = portfolioDao.selectPortfolioFromUserIdWithEquities(user.getId(), null, null);
	}

	@After
	public void after() {
		userDao.delete(user);
		portfolioDao.delete(portfolio);
	}

	@Test
	public void testAddOrUpdateEquity() throws UnsupportedEncodingException, YahooException {
		IBusiness business = new Business();
		Equity equity = new Equity();
		equity.setUnitCostPrice(10.9);
		equity.setQuantity(10.0);
		business.updateEquity(user.getId(), "FP.PA", equity);
		EquityDaoImpl daoEquity = new EquityDaoImpl();
		CompanyDaoImpl daoCompany = new CompanyDaoImpl();
		Portfolio port = portfolioDao.selectPortfolioFromUserIdWithEquities(user.getId(), null, null);
		Equity eq = port.getEquities().get(0);
		Assert.assertNotNull(eq);
		daoEquity.delete(eq);
		daoCompany.delete(eq.getCompany());
	}

	@Test
	public void testDeleteEquity() throws UnsupportedEncodingException, YahooException {
		IBusiness business = new Business();
		Equity equity = new Equity();
		equity.setUnitCostPrice(10.9);
		equity.setQuantity(10.0);
		business.updateEquity(user.getId(), "FP.PA", equity);
		CompanyDaoImpl daoCompany = new CompanyDaoImpl();
		Portfolio port = portfolioDao.selectPortfolioFromUserIdWithEquities(user.getId(), null, null);
		Equity eq = port.getEquities().get(0);
		Assert.assertNotNull(eq);
		business.deleteEquity(eq);
		daoCompany.delete(eq.getCompany());
	}

	@Test
	public void testCreateDeleteUser() throws NoSuchAlgorithmException, UnsupportedEncodingException, LoginException {
		IBusiness business = new Business();
		String login = UUID.randomUUID().toString().substring(0, 10);
		String md5Password = "myEcryptedMd5Password";
		String email = "test@test.com";
		business.createUser(login, md5Password, email);
		User user = business.getUser(login);
		Assert.assertNotNull(user);
		Assert.assertEquals(login, user.getLogin());
		Assert.assertEquals(128, user.getPassword().length());
		Assert.assertEquals(email, user.getEmail());
		Portfolio portfolio = portfolioDao.selectPortfolioFromUserIdWithEquities(user.getId(), null, null);
		Assert.assertNotNull(portfolio);
		Assert.assertEquals(portfolio.getCurrency(), Currency.EUR);
		Assert.assertEquals(0, portfolio.getEquities().size());

		// Clean
		business.deleteUser(login);
		user = business.getUser(login);
		Assert.assertNull(user);
	}

	@Test
	public void testCheckUser() throws NoSuchAlgorithmException, UnsupportedEncodingException, LoginException{
		IBusiness business = new Business();
		String login = UUID.randomUUID().toString().substring(0, 10);
		String md5Password = "myEcryptedMd5Password";
		String email = "test@test.com";
		business.createUser(login, md5Password, email);
		User user = business.checkUser(login, md5Password);
		Assert.assertNotNull(user);
		
		// Clean
		business.deleteUser(login);
		user = business.getUser(login);
		Assert.assertNull(user);
	}

}