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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import fr.cph.stock.entities.Account;
import fr.cph.stock.entities.Company;
import fr.cph.stock.entities.Equity;
import fr.cph.stock.entities.Follow;
import fr.cph.stock.entities.Index;
import fr.cph.stock.entities.Portfolio;
import fr.cph.stock.entities.ShareValue;
import fr.cph.stock.entities.User;
import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.exception.EquityException;
import fr.cph.stock.exception.LoginException;
import fr.cph.stock.exception.YahooException;

/**
 * Interface defining function that access database and process information
 * 
 * @author Carl-Philipp Harmant
 * @version 1
 */
public interface IBusiness {

	/**
	 * Create an equity
	 * 
	 * @param userId
	 * @param ticker
	 * @param equity
	 * @throws UnsupportedEncodingException
	 * @throws YahooException
	 * @throws EquityException
	 */
	void createEquity(int userId, String ticker, Equity equity) throws UnsupportedEncodingException, YahooException,
			EquityException;

	/**
	 * Update an equity
	 * 
	 * @param userId
	 * @param ticker
	 * @param equity
	 * @throws UnsupportedEncodingException
	 * @throws YahooException
	 */
	void updateEquity(int userId, String ticker, Equity equity) throws UnsupportedEncodingException, YahooException;

	/**
	 * Delete an equity
	 * 
	 * @param equity
	 */
	void deleteEquity(Equity equity);

	/**
	 * Create a user
	 * 
	 * @param login
	 * @param md5Password
	 * @param email
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 * @throws LoginException
	 */
	void createUser(String login, String md5Password, String email) throws NoSuchAlgorithmException,
			UnsupportedEncodingException, LoginException;

	/**
	 * Get user
	 * 
	 * @param login
	 * @return
	 */
	User getUser(String login);

	/**
	 * Delete user
	 * 
	 * @param login
	 */
	void deleteUser(String login);

	/**
	 * Check if user credentials are correct
	 * 
	 * @param login
	 * @param md5Password
	 * @return
	 * @throws LoginException
	 */
	User checkUser(String login, String md5Password) throws LoginException;

	/**
	 * Update a user
	 * 
	 * @param user
	 */
	void updateUser(User user);

	/**
	 * Allow a user to login
	 * 
	 * @param login
	 */
	void validateUser(String login);

	/**
	 * Get user with email
	 * 
	 * @param email
	 * @return
	 */
	User getUserWithEmail(String email);

	/**
	 * Update a user password
	 * 
	 * @param user
	 */
	void updateOneUserPassword(User user);

	/**
	 * Get a portfolio
	 * 
	 * @param userId
	 * @param from
	 * @param to
	 * @return
	 * @throws YahooException
	 */
	Portfolio getUserPortfolio(int userId, Date from, Date to) throws YahooException;

	/**
	 * Update a portfolio
	 * 
	 * @param portfolio
	 */
	void updatePortfolio(Portfolio portfolio);

	/**
	 * Update liquidity in the account
	 * 
	 * @param account
	 * @param liquidity
	 */
	void updateLiquidity(Account account, double liquidity);

	/**
	 * Load a currency with its data
	 * 
	 * @param currency
	 * @return
	 * @throws YahooException
	 */
	Currency loadCurrencyData(Currency currency) throws YahooException;

	/**
	 * Update all current currencies
	 * 
	 * @throws YahooException
	 */
	void updateAllCurrencies() throws YahooException;

	/**
	 * Update one currency
	 * 
	 * @param currency
	 * @throws YahooException
	 */
	void updateOneCurrency(Currency currency) throws YahooException;

	/**
	 * Get all currency data
	 * 
	 * @param currency
	 * @return
	 */
	Object[][] getAllCurrencyData(Currency currency);

	/**
	 * Add or update companies
	 * 
	 * @param tickers
	 * @return
	 * @throws YahooException
	 */
	List<Company> addOrUpdateCompanies(List<String> tickers) throws YahooException;

	/**
	 * Update company that do not have any real time data. (usually funds)
	 */
	void updateCompaniesNotRealTime();

	/**
	 * Update company
	 */
	void updateCompaniesRealTime();

	/**
	 * 
	 * @param companiesYahooIdRealTime
	 * @return 
	 * @throws YahooException
	 */
	String addOrUpdateCompaniesLimitedRequest(List<String> companiesYahooIdRealTime) throws YahooException;

	/**
	 * Get a list of share value that belong to a user
	 * 
	 * @param user
	 * @return
	 */
	List<ShareValue> getShareValue(User user);

	/**
	 * Add a share value
	 * 
	 * @param share
	 */
	void addShareValue(ShareValue share);

	/**
	 * Get a share value
	 * 
	 * @param id
	 * @return
	 */
	ShareValue selectOneShareValue(int id);

	/**
	 * Delete a share value
	 * 
	 * @param sv
	 */
	void deleteShareValue(ShareValue sv);

	/**
	 * Get index information
	 * 
	 * @param yahooId
	 * @param from
	 * @param to
	 * @return
	 */
	List<Index> getIndexes(String yahooId, Date from, Date to);

	/**
	 * Update an index
	 * 
	 * @param yahooId
	 * @param from
	 * @param to
	 * @param force
	 * @return
	 * @throws YahooException
	 */
	boolean updateIndex(String yahooId, Date from, Date to, boolean force) throws YahooException;

	/**
	 * Update an index
	 * 
	 * @param yahooId
	 * @throws YahooException
	 */
	void updateIndex(String yahooId) throws YahooException;

	/**
	 * Don't remember what it does
	 * 
	 * @param yahooId
	 * @param timeZone
	 * @throws YahooException
	 */
	void checkUpdateIndex(String yahooId, TimeZone timeZone) throws YahooException;

	/**
	 * Get a list of followed companies
	 * 
	 * @param userId
	 * @return
	 */
	List<Follow> getListFollow(int userId);

	/**
	 * Add a company to follow
	 * 
	 * @param user
	 * @param ticker
	 * @param lower
	 * @param higher
	 * @throws YahooException
	 */
	void addFollow(User user, String ticker, Double lower, Double higher) throws YahooException;

	/**
	 * Update a company to follow
	 * 
	 * @param user
	 * @param ticker
	 * @param lower
	 * @param higher
	 */
	void updateFollow(User user, String ticker, Double lower, Double higher);

	/**
	 * Delete a company that the user follow
	 * 
	 * @param id
	 */
	void deleteFollow(int id);

	/**
	 * Add an account
	 * 
	 * @param account
	 */
	void addAccount(Account account);

	/**
	 * Update an account
	 * 
	 * @param account
	 */
	void updateAccount(Account account);

	/**
	 * Get one account with its name
	 * 
	 * @param userId
	 * @param name
	 * @return
	 */
	Account selectOneAccountWithName(int userId, String name);

	/**
	 * 
	 * @param account
	 */
	void deleteAccount(Account account);

	/**
	 * Update commentare in share value
	 * 
	 * @param sv
	 */
	void updateCommentaryShareValue(ShareValue sv);

	/**
	 * Auto update all companies data and if there is no error, auto update the share value of selected user
	 * 
	 * @param date
	 * @throws YahooException
	 */
	void autoUpdateUserShareValue(Calendar date) throws YahooException;

	/**
	 * Update current share value
	 * 
	 * @param portfolio
	 * @param account
	 * @param liquidityMovement
	 * @param yield
	 * @param buy
	 * @param sell
	 * @param taxe
	 * @param commentary
	 */
	void updateCurrentShareValue(Portfolio portfolio, Account account, Double liquidityMovement, Double yield, Double buy,
			Double sell, Double taxe, String commentary);

	/**
	 * Delete companies that are not used anymore
	 */
	void cleanDB();

}
