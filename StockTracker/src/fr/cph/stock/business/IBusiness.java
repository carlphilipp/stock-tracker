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

public interface IBusiness {

	void updateEquity(int userId, String ticker, Equity equity)
			throws UnsupportedEncodingException, YahooException;

	public void deleteEquity(Equity equity);

	public void createUser(String login, String md5Password, String email)
			throws NoSuchAlgorithmException, UnsupportedEncodingException,
			LoginException;

	User getUser(String login);

	void deleteUser(String login);

	User checkUser(String login, String md5Password) throws LoginException;

	Portfolio getUserPortfolio(int userId, Date from, Date to)
			throws YahooException;

	Currency loadCurrencyData(Currency currency) throws YahooException;

	void createEquity(int userId, String ticker, Equity equity)
			throws UnsupportedEncodingException, YahooException,
			EquityException;

	List<Company> addOrUpdateCompanies(List<String> tickers)
			throws YahooException;

	List<ShareValue> getShareValue(User user);;

	void updateAllCurrencies() throws YahooException;

	List<Index> getIndexes(String yahooId, Date from, Date to);

	List<Follow> getListFollow(int userId);

	void deleteShareValue(ShareValue sv);

	void addFollow(User user, String ticker, Double lower, Double higher)
			throws YahooException;

	void deleteFollow(int id);

	void addShareValue(ShareValue share);

	Object[][] getAllCurrencyData(Currency currency);

	boolean updateIndex(String yahooId, Date from, Date to, boolean force)
			throws YahooException;

	void updatePortfolio(Portfolio portfolio);

	void updateUser(User user);

	void updateIndex(String yahooId) throws YahooException;

	void updateFollow(User user, String ticker, Double lower, Double higher);

	void updateCompaniesNotRealTime();

	void updateCompaniesRealTime();

	void addOrUpdateCompaniesLimitedRequest(
			List<String> companiesYahooIdRealTime) throws YahooException;

	Account selectOneAccountWithName(int userId, String name);

	void updateLiquidity(Account account, double liquidity);

	void addAccount(Account account);

	void updateAccount(Account account);

	void deleteAccount(Account account);

	void updateOneCurrency(Currency currency) throws YahooException;

	void updateCommentaryShareValue(ShareValue sv);

	ShareValue selectOneShareValue(int id);

	void validateUser(String login);

	User getUserWithEmail(String email);

	void updateOneUserPassword(User user);

	void cleanDB();

	void autoUpdateUserShareValue(Calendar date) throws YahooException;

	void updateCurrentShareValue(Portfolio portfolio, Account account,
			Double liquidityMovement, Double yield, Double buy, Double sell,
			Double taxe, String commentary);

	void checkUpdateIndex(String yahooId, TimeZone timeZone)
			throws YahooException;

}
