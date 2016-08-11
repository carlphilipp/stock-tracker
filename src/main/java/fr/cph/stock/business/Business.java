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

import fr.cph.stock.entities.*;
import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.exception.LoginException;
import fr.cph.stock.exception.YahooException;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Interface defining function that access database and process information
 *
 * @author Carl-Philipp Harmant
 * @version 1
 */
public interface Business {

	/**
	 * Create a user
	 *
	 * @param login
	 *            the login
	 * @param md5Password
	 *            the md5 password
	 * @param email
	 *            the email
	 * @throws NoSuchAlgorithmException
	 *             the NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 *             the UnsupportedEncodingException
	 * @throws LoginException
	 *             the LoginException
	 */
	void createUser(final String login, final String md5Password, final String email) throws NoSuchAlgorithmException, UnsupportedEncodingException, LoginException;

	/**
	 * Get user
	 *
	 * @param login
	 *            the login
	 * @return a user
	 */
	User getUser(final String login);

	/**
	 * Delete user
	 *
	 * @param login
	 *            the login to delete
	 */
	void deleteUser(final String login);

	/**
	 * Check if user credentials are correct
	 *
	 * @param login
	 *            the login
	 * @param md5Password
	 *            the md5 password
	 * @return a user
	 * @throws LoginException
	 *             the LoginException
	 */
	User checkUser(final String login, final String md5Password) throws LoginException;

	/**
	 * Update a user
	 *
	 * @param user
	 *            the user
	 */
	void updateUser(final User user);

	/**
	 * Allow a user to login
	 *
	 * @param login
	 *            the login
	 */
	void validateUser(final String login);

	/**
	 * Get user with email
	 *
	 * @param email
	 *            the email
	 * @return a user
	 */
	User getUserWithEmail(final String email);

	/**
	 * Update a user password
	 *
	 * @param user
	 *            a user
	 */
	void updateOneUserPassword(final User user);

	/**
	 * Get a portfolio
	 *
	 * @param userId
	 *            the user id
	 * @param from
	 *            the from date
	 * @param to
	 *            the to date
	 * @return a Portfolio
	 * @throws YahooException
	 *             the yahoo exception
	 */
	Portfolio getUserPortfolio(final int userId, final Date from, final Date to) throws YahooException;

	/**
	 * Update a portfolio
	 *
	 * @param portfolio
	 *            the portfolio to update
	 */
	void updatePortfolio(final Portfolio portfolio);

	/**
	 * Update liquidity in the account
	 *
	 * @param account
	 *            the account
	 * @param liquidity
	 *            the liquidity
	 */
	void updateLiquidity(final Account account, final double liquidity);

	/**
	 * Load a currency with its data
	 *
	 * @param currency
	 *            the currency
	 * @return a currency
	 * @throws YahooException
	 *             the yahoo exception
	 */
	Currency loadCurrencyData(final Currency currency) throws YahooException;

	/**
	 * Update all current currencies
	 *
	 * @throws YahooException
	 *             the yahoo exception
	 */
	void updateAllCurrencies() throws YahooException;

	/**
	 * Update one currency
	 *
	 * @param currency
	 *            the currency
	 * @throws YahooException
	 *             the yahoo exception
	 */
	void updateOneCurrency(final Currency currency) throws YahooException;

	/**
	 * Get all currency data
	 *
	 * @param currency
	 *            the currency
	 * @return a 2 dim array of object
	 */
	Object[][] getAllCurrencyData(final Currency currency);

	/**
	 * Add or update companies
	 *
	 * @param tickers
	 *            a list of yahoo tickers
	 * @return a list of company
	 * @throws YahooException
	 *             the yahoo exception
	 */
	List<Company> addOrUpdateCompanies(final List<String> tickers) throws YahooException;

	/**
	 * Update company that do not have any real time data. (usually funds)
	 */
	void updateCompaniesNotRealTime();

	/**
	 *
	 * @param companiesYahooIdRealTime
	 *            a list of company id
	 * @return a string
	 * @throws YahooException
	 *             the yahoo exception
	 */
	String addOrUpdateCompaniesLimitedRequest(final List<String> companiesYahooIdRealTime) throws YahooException;

	/**
	 * Add a share value
	 *
	 * @param share
	 *            a sharevalue
	 */
	void addShareValue(final ShareValue share);

	/**
	 * Get a share value
	 *
	 * @param id
	 *            the id of the sharevalue to get
	 * @return a share value
	 */
	ShareValue selectOneShareValue(final int id);

	/**
	 * Delete a share value
	 *
	 * @param sv
	 *            the share value
	 */
	void deleteShareValue(final ShareValue sv);

	/**
	 * Get index information
	 *
	 * @param yahooId
	 *            the yahoo id
	 * @param from
	 *            the from date
	 * @param to
	 *            the to date
	 * @return a list of index
	 */
	List<Index> getIndexes(final String yahooId, final Date from, final Date to);

	/**
	 * Update an index
	 *
	 * @param yahooId
	 *            the yahoo id
	 * @throws YahooException
	 *             the yahoo exception
	 */
	void updateIndex(final String yahooId) throws YahooException;

	/**
	 * Don't remember what it does
	 *
	 * @param yahooId
	 *            the yahoo id
	 * @param timeZone
	 *            the timezone
	 * @throws YahooException
	 *             the yahoo exception
	 */
	void checkUpdateIndex(final String yahooId, final TimeZone timeZone) throws YahooException;

	/**
	 * Get a list of followed companies
	 *
	 * @param userId
	 *            the user id
	 * @return a list of follow
	 */
	List<Follow> getListFollow(final int userId);

	/**
	 * Add a company to follow
	 *
	 * @param user
	 *            the user
	 * @param ticker
	 *            the yahoo ticker
	 * @param lower
	 *            the lower
	 * @param higher
	 *            the higher
	 * @throws YahooException
	 *             the yahoo exception
	 */
	void addFollow(final User user, final String ticker, final Double lower, final Double higher) throws YahooException;

	/**
	 * Update a company to follow
	 *
	 * @param user
	 *            the user
	 * @param ticker
	 *            the ticker
	 * @param lower
	 *            the loweer
	 * @param higher
	 *            the higher
	 */
	void updateFollow(final User user, final String ticker, final Double lower, final Double higher);

	/**
	 * Delete a company that the user follow
	 *
	 * @param id
	 *            the follow id
	 */
	void deleteFollow(final int id);

	/**
	 * Add an account
	 *
	 * @param account
	 *            the account to add
	 */
	void addAccount(final Account account);

	/**
	 * Update an account
	 *
	 * @param account
	 *            the account to update
	 */
	void updateAccount(final Account account);

	/**
	 * Delete account
	 *
	 * @param account
	 *            the account to delete
	 */
	void deleteAccount(final Account account);

	/**
	 * Update commentare in share value
	 *
	 * @param sv
	 *            the share value
	 */
	void updateCommentaryShareValue(final ShareValue sv);

	/**
	 * Auto update all companies data and if there is no error, auto update the share value of selected user
	 *
	 * @param date
	 *            the date
	 * @throws YahooException
	 *             the yahoo exception
	 */
	void autoUpdateUserShareValue(final Calendar date) throws YahooException;

	/**
	 * Update current share value
	 *
	 * @param portfolio
	 *            the portfolio
	 * @param account
	 *            the account
	 * @param liquidityMovement
	 *            the liquidity movement
	 * @param yield
	 *            the yield
	 * @param buy
	 *            the amount buy
	 * @param sell
	 *            the amount sell
	 * @param taxe
	 *            the taxe
	 * @param commentary
	 *            the commentary
	 */
	void updateCurrentShareValue(final Portfolio portfolio, final Account account, final Double liquidityMovement,
			final Double yield, final Double buy, final Double sell, final Double taxe, final String commentary);

	Company addOrUpdateCompany(String ticker) throws YahooException;

	/**
	 * Delete companies that are not used anymore
	 */
	void cleanDB();

	/**
	 * @param name
	 * @param industry
	 * @param sector
	 * @param currency
	 * @param quote
	 * @return
	 */
	Company createManualCompany(String name, String industry, String sector, Currency currency, double quote);

	/**
	 * @param company
	 */
	void deleteCompany(final Company company);

	void updateCompanyManual(Integer companyIdRes, Double quoteRes);

}
