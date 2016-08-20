/**
 * Copyright 2016 Carl-Philipp Harmant
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.cph.stock.business;

import fr.cph.stock.business.impl.EquityBusinessImpl;
import fr.cph.stock.business.impl.UserBusinessImpl;
import fr.cph.stock.dao.EquityDAO;
import fr.cph.stock.dao.PortfolioDAO;
import fr.cph.stock.dao.UserDAO;
import fr.cph.stock.entities.Equity;
import fr.cph.stock.entities.Portfolio;
import fr.cph.stock.entities.User;
import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.exception.LoginException;
import fr.cph.stock.exception.YahooException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public final class AccountBusinessTest {

	private User user;
	private UserDAO userDao;
	private Portfolio portfolio;
	private PortfolioDAO portfolioDao;

	@Before
	public void setUp() {
		user = new User();
		userDao = UserDAO.INSTANCE;
		user.setLogin("lolzcarlz");
		user.setPassword("passwordd");
		user.setEmail("poke@poke.com");
		userDao.insert(user);
		user = userDao.selectWithLogin("lolzcarlz");

		portfolio = new Portfolio();
		portfolioDao = PortfolioDAO.INSTANCE;
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
		EquityBusiness business = EquityBusinessImpl.INSTANCE;
		Equity equity = new Equity();
		equity.setUnitCostPrice(10.9);
		equity.setQuantity(10.0);
		business.updateEquity(user.getId(), "FP.PA", equity);
		EquityDAO daoEquity = EquityDAO.INSTANCE;
		Portfolio port = portfolioDao.selectPortfolioFromUserIdWithEquities(user.getId(), null, null);
		Equity eq = port.getEquities().get(0);
		assertNotNull(eq);
		daoEquity.delete(eq);
	}

	@Test
	public void testDeleteEquity() throws UnsupportedEncodingException, YahooException {
		EquityBusiness business = EquityBusinessImpl.INSTANCE;
		Equity equity = new Equity();
		equity.setUnitCostPrice(10.9);
		equity.setQuantity(10.0);
		business.updateEquity(user.getId(), "FP.PA", equity);
		Portfolio port = portfolioDao.selectPortfolioFromUserIdWithEquities(user.getId(), null, null);
		Equity eq = port.getEquities().get(0);
		assertNotNull(eq);
		business.deleteEquity(eq);
	}

	@Test
	public void testCreateDeleteUser() throws NoSuchAlgorithmException, UnsupportedEncodingException, LoginException {
		UserBusiness userBusiness = UserBusinessImpl.INSTANCE;
		String login = UUID.randomUUID().toString().substring(0, 10);
		String md5Password = "myEcryptedMd5Password";
		String email = "test@testderpderp.com";
		userBusiness.createUser(login, md5Password, email);
		User userLocal = userBusiness.getUser(login);
		assertNotNull(userLocal);
		assertEquals(login, userLocal.getLogin());
		assertEquals(128, userLocal.getPassword().length());
		assertEquals(email, userLocal.getEmail());
		Portfolio portfolioLocal = portfolioDao.selectPortfolioFromUserIdWithEquities(userLocal.getId(), null, null);
		assertNotNull(portfolioLocal);
		assertEquals(portfolioLocal.getCurrency(), Currency.EUR);
		assertEquals(0, portfolioLocal.getEquities().size());

		// Clean
		Portfolio port = new Portfolio();
		port.setId(portfolioLocal.getId());
		portfolioDao.delete(port);
		userBusiness.deleteUser(login);
		userLocal = userBusiness.getUser(login);
		assertNull(userLocal);
	}

	@Test
	public void testCheckUser() throws NoSuchAlgorithmException, UnsupportedEncodingException, LoginException {
		UserBusiness userBusiness = UserBusinessImpl.INSTANCE;
		String login = UUID.randomUUID().toString().substring(0, 10);
		String md5Password = "myEcryptedMd5Password";
		String email = "test@test.com";
		userBusiness.createUser(login, md5Password, email);
		User userLocal = userBusiness.checkUser(login, md5Password);
		assertNotNull(userLocal);

		// Clean
		Portfolio portfolioLocal = portfolioDao.selectPortfolioFromUserIdWithEquities(userLocal.getId(), null, null);
		Portfolio port = new Portfolio();
		port.setId(portfolioLocal.getId());
		portfolioDao.delete(port);
		userBusiness.deleteUser(login);
		userLocal = userBusiness.getUser(login);
		assertNull(userLocal);
	}

}
