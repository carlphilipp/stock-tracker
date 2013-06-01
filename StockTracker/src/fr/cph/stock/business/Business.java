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
import java.math.BigDecimal;
import java.math.MathContext;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import fr.cph.stock.dao.AccountDaoImpl;
import fr.cph.stock.dao.CompanyDaoImpl;
import fr.cph.stock.dao.CurrencyDaoImpl;
import fr.cph.stock.dao.EquityDaoImpl;
import fr.cph.stock.dao.FollowDaoImpl;
import fr.cph.stock.dao.IndexDaoImpl;
import fr.cph.stock.dao.PortfolioDaoImpl;
import fr.cph.stock.dao.ShareValueDaoImpl;
import fr.cph.stock.dao.UserDaoImpl;
import fr.cph.stock.entities.Account;
import fr.cph.stock.entities.Company;
import fr.cph.stock.entities.CurrencyData;
import fr.cph.stock.entities.Equity;
import fr.cph.stock.entities.Follow;
import fr.cph.stock.entities.Index;
import fr.cph.stock.entities.Portfolio;
import fr.cph.stock.entities.ShareValue;
import fr.cph.stock.entities.User;
import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.enumtype.Market;
import fr.cph.stock.exception.EquityException;
import fr.cph.stock.exception.LoginException;
import fr.cph.stock.exception.YahooException;
import fr.cph.stock.external.IExternalDataAccess;
import fr.cph.stock.external.YahooExternalDataAccess;
import fr.cph.stock.security.Security;
import fr.cph.stock.util.Info;
import fr.cph.stock.util.Mail;
import fr.cph.stock.util.Util;

public class Business implements IBusiness {

	private static final Logger log = Logger.getLogger(Business.class);

	private final int MAX_UPDATE_COMPANY = 15;
	private final int PAUSE = 400;
	private final MathContext mathContext = MathContext.DECIMAL32;

	private IExternalDataAccess yahoo;
	private CompanyDaoImpl daoCompany;
	private PortfolioDaoImpl daoPortfolio;
	private EquityDaoImpl daoEquity;
	private UserDaoImpl daoUser;
	private CurrencyDaoImpl daoCurrency;
	private ShareValueDaoImpl daoShareValue;
	private IndexDaoImpl daoIndex;
	private FollowDaoImpl daoFollow;
	private AccountDaoImpl daoAccount;

	public Business() {
		yahoo = new YahooExternalDataAccess();
		daoCompany = new CompanyDaoImpl();
		daoPortfolio = new PortfolioDaoImpl();
		daoEquity = new EquityDaoImpl();
		daoUser = new UserDaoImpl();
		daoCurrency = new CurrencyDaoImpl();
		daoShareValue = new ShareValueDaoImpl();
		daoIndex = new IndexDaoImpl();
		daoFollow = new FollowDaoImpl();
		daoAccount = new AccountDaoImpl();
	}

	@Override
	public void createEquity(int userId, String ticker, Equity equity) throws YahooException, EquityException {

		Company company = addOrUpdateCompany(ticker);
		Portfolio portfolio = daoPortfolio.selectPortfolioFromUserIdWithEquities(userId, null, null);

		boolean isAlreadyThere = false;
		for (Equity e : portfolio.getEquities()) {
			if (e.getCompanyId() == company.getId()) {
				isAlreadyThere = true;
				equity.setid(e.getId());
			}
		}
		equity.setCompanyId(company.getId());
		equity.setPortfolioId(portfolio.getId());
		if (isAlreadyThere) {
			throw new EquityException(ticker + EquityException.ENTITY_ALREADY_RECORDED);
		} else {
			daoEquity.insert(equity);
		}
	}

	@Override
	public void updateEquity(int userId, String ticker, Equity equity) throws UnsupportedEncodingException, YahooException {
		Company company = daoCompany.selectWithYahooId(ticker);
		Portfolio portfolio = daoPortfolio.selectPortfolioFromUserIdWithEquities(userId, null, null);

		boolean isAlreadyThere = false;
		for (Equity e : portfolio.getEquities()) {
			if (e.getCompanyId() == company.getId()) {
				isAlreadyThere = true;
				equity.setid(e.getId());
			}
		}
		equity.setCompanyId(company.getId());
		equity.setPortfolioId(portfolio.getId());
		if (isAlreadyThere) {
			daoEquity.update(equity);
		} else {
			daoEquity.insert(equity);
		}
	}

	@Override
	public void deleteEquity(Equity equity) {
		daoEquity.delete(equity);
	}

	protected Company addOrUpdateCompany(String ticker) throws YahooException {
		List<String> tickers = new ArrayList<String>();
		tickers.add(ticker);
		Company companyYahoo = yahoo.getCompaniesData(tickers).get(0);
		Company companyInDB = daoCompany.selectWithYahooId(companyYahoo.getYahooId());
		if (companyInDB == null) {
			companyYahoo = yahoo.getCompanyInfo(companyYahoo);
			daoCompany.insert(companyYahoo);
			companyInDB = daoCompany.selectWithYahooId(companyYahoo.getYahooId());
		} else {
			companyInDB.setQuote(companyYahoo.getQuote());
			companyInDB.setYield(companyYahoo.getYield());
			companyInDB.setName(companyYahoo.getName());
			companyInDB.setCurrency(Market.getCurrency(companyYahoo.getMarket()));
			companyInDB.setMarketCapitalization(companyYahoo.getMarketCapitalization());
			companyInDB.setMarket(companyYahoo.getMarket());
			companyInDB.setYearHigh(companyYahoo.getYearHigh());
			companyInDB.setYearLow(companyYahoo.getYearLow());
			companyInDB.setYesterdayClose(companyYahoo.getYesterdayClose());
			daoCompany.update(companyInDB);
		}
		return companyInDB;
	}

	@Override
	public List<Company> addOrUpdateCompanies(List<String> tickers) throws YahooException {
		log.debug("Updating: " + tickers);
		List<Company> companies = yahoo.getCompaniesData(tickers);
		List<Company> companiesResult = new ArrayList<Company>();
		for (Company companyYahoo : companies) {
			Company companyInDB = daoCompany.selectWithYahooId(companyYahoo.getYahooId());
			if (companyInDB == null) {
				companyYahoo = yahoo.getCompanyInfo(companyYahoo);
				daoCompany.insert(companyYahoo);
				companyInDB = daoCompany.selectWithYahooId(companyYahoo.getYahooId());
			} else {
				companyInDB.setQuote(companyYahoo.getQuote());
				companyInDB.setYield(companyYahoo.getYield());
				companyInDB.setName(companyYahoo.getName());
				companyInDB.setCurrency(Market.getCurrency(companyYahoo.getMarket()));
				companyInDB.setMarketCapitalization(companyYahoo.getMarketCapitalization());
				companyInDB.setMarket(companyYahoo.getMarket());
				companyInDB.setYearHigh(companyYahoo.getYearHigh());
				companyInDB.setYearLow(companyYahoo.getYearLow());
				companyInDB.setYesterdayClose(companyYahoo.getYesterdayClose());
				companyInDB.setChangeInPercent(companyYahoo.getChangeInPercent());
				daoCompany.update(companyInDB);
			}
			companiesResult.add(companyInDB);
		}
		return companiesResult;
	}

	@Override
	public void addFollow(User user, String ticker, Double lower, Double higher) throws YahooException {
		Company company = addOrUpdateCompany(ticker);
		Follow foll = daoFollow.selectOneFollow(user.getId(), company.getId());
		if (foll == null) {
			Follow follow = new Follow();
			follow.setCompany(company);
			follow.setCompanyId(company.getId());
			follow.setUserId(user.getId());
			follow.setLowerLimit(lower);
			follow.setHigherLimit(higher);
			daoFollow.insert(follow);
		} else {
			foll.setLowerLimit(lower);
			foll.setHigherLimit(higher);
			daoFollow.update(foll);
		}
	}

	@Override
	public void updateFollow(User user, String ticker, Double lower, Double higher) {
		Company company = daoCompany.selectWithYahooId(ticker);
		Follow foll = daoFollow.selectOneFollow(user.getId(), company.getId());
		foll.setLowerLimit(lower);
		foll.setHigherLimit(higher);
		daoFollow.update(foll);
	}

	@Override
	public void deleteFollow(int id) {
		Follow follow = new Follow();
		follow.setId(id);
		daoFollow.delete(follow);
	}

	@Override
	public void createUser(String login, String md5Password, String email) throws NoSuchAlgorithmException,
			UnsupportedEncodingException, LoginException {
		String md5PasswordHashed = Security.encodeToSha256(md5Password);
		String saltHashed = Security.generateSalt();
		String cryptedPasswordSalt = Security.encodeToSha256(md5PasswordHashed + saltHashed);
		User userInDbWithLogin = getUser(login);
		User userInDbWithEmail = getUserWithEmail(email);
		if (userInDbWithLogin != null) {
			throw new LoginException("Sorry, '" + login + "' is not available!");
		}
		if (userInDbWithEmail != null) {
			throw new LoginException("Sorry, '" + email + "' is not available!");
		}
		User user = new User();
		user.setLogin(login);
		user.setPassword(saltHashed + cryptedPasswordSalt);
		user.setEmail(email);
		user.setAllow(false);
		daoUser.insert(user);
		StringBuilder body = new StringBuilder();
		String check = Security.encodeToSha256(login + saltHashed + cryptedPasswordSalt + email);
		body.append("Welcome to " + Info.NAME + ",\n\nPlease valid your account by clicking on that link:" + Info.ADDRESS
				+ Info.FOLDER + "/check?&login=" + login + "&check=" + check + ".\n\nBest regards,\nThe " + Info.NAME + " team.");
		Mail.sendMail("[Registration] " + Info.NAME, body.toString(), new String[] { email }, null);
		createUserPortfolio(user.getLogin());
		createUserDefautAccount(user);
	}

	@Override
	public User getUser(String login) {
		return daoUser.selectWithLogin(login);
	}

	@Override
	public User getUserWithEmail(String email) {
		return daoUser.selectWithEmail(email);
	}

	@Override
	public void deleteUser(String login) {
		User user = new User();
		user.setLogin(login);
		daoUser.delete(user);
	}

	@Override
	public User checkUser(String login, String md5Password) throws LoginException {
		User user = daoUser.selectWithLogin(login);
		if (user != null) {
			String md5PasswordHashed;
			try {
				md5PasswordHashed = Security.encodeToSha256(md5Password);
				String saltHashed = user.getPassword().substring(0, 64);
				String cryptedPasswordSalt = user.getPassword().substring(64, user.getPassword().length());
				String cryptedPasswordSaltToTest = Security.encodeToSha256(md5PasswordHashed + saltHashed);
				if (!cryptedPasswordSalt.equals(cryptedPasswordSaltToTest)) {
					user = null;
				} else {
					user.setPassword(null);
				}
			} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
				throw new LoginException(e.getMessage(), e);
			}
		}
		return user;
	}

	@Override
	public Portfolio getUserPortfolio(int userId, Date from, Date to) throws YahooException {
		Portfolio portfolio = daoPortfolio.selectPortfolioFromUserIdWithEquities(userId, from, to);
		Collections.sort(portfolio.getEquities());
		Currency currency = loadCurrencyData(portfolio.getCurrency());
		portfolio.setCurrency(currency);
		for (Equity e : portfolio.getEquities()) {
			if (e.getCompany().getCurrency() == portfolio.getCurrency()) {
				e.setParity(1.0);
			} else {
				e.setParity(portfolio.getCurrency().getParity(e.getCompany().getCurrency()));
			}
		}
		double liquidity = 0.0;
		for (Account acc : portfolio.getAccounts()) {
			if (acc.getCurrency() == portfolio.getCurrency()) {
				liquidity += acc.getLiquidity();
				acc.setParity(1.0);
			} else {
				liquidity += acc.getLiquidity() * portfolio.getCurrency().getParity(acc.getCurrency());
				acc.setParity(portfolio.getCurrency().getParity(acc.getCurrency()));
			}
		}
		liquidity = (new BigDecimal(liquidity, mathContext)).doubleValue();
		portfolio.setLiquidity(liquidity);
		portfolio.compute();
		return portfolio;
	}

	@Override
	public void updateLiquidity(Account account, double liquidity) {
		// Portfolio portfolio = daoPortfolio.selectPortfolioWithId(userId);
		// portfolio.setLiquidity(liquidity);
		// daoPortfolio.update(portfolio);
		account.setLiquidity(liquidity);
		daoAccount.update(account);
	}

	@Override
	public Currency loadCurrencyData(Currency currency) throws YahooException {
		List<CurrencyData> currencyDataList = daoCurrency.selectListCurrency(currency.getCode());
		if (currencyDataList.size() == 0) {
			List<CurrencyData> currenciesData = yahoo.getCurrencyData(currency);
			for (CurrencyData currencyData : currenciesData) {
				CurrencyData c = daoCurrency.selectOneCurrencyDataWithParam(currencyData);
				if (c == null) {
					daoCurrency.insert(currencyData);
				} else {
					currencyData.setId(c.getId());
					daoCurrency.update(currencyData);
				}
			}
			currencyDataList = daoCurrency.selectListCurrency(currency.getCode());
		}
		currency.setCurrencyData(currencyDataList);
		return currency;
	}

	@Override
	public void updateAllCurrencies() throws YahooException {
		for (Currency currency : Currency.values()) {
			List<CurrencyData> currenciesData = yahoo.getCurrencyData(currency);
			if ((Currency.values().length - 1) * 2 == currenciesData.size()) {
				for (CurrencyData currencyData : currenciesData) {
					CurrencyData c = daoCurrency.selectOneCurrencyDataWithParam(currencyData);
					if (c == null) {
						daoCurrency.insert(currencyData);
					} else {
						currencyData.setId(c.getId());
						daoCurrency.update(currencyData);
					}
				}
			} else {
				log.warn("Impossible to update this currency: " + currency.getCode());
			}
		}
	}

	@Override
	public void updateOneCurrency(Currency currency) throws YahooException {
		List<CurrencyData> currenciesData = yahoo.getCurrencyData(currency);
		if ((Currency.values().length - 1) * 2 == currenciesData.size()) {
			for (CurrencyData currencyData : currenciesData) {
				CurrencyData c = daoCurrency.selectOneCurrencyDataWithParam(currencyData);
				if (c == null) {
					daoCurrency.insert(currencyData);
				} else {
					currencyData.setId(c.getId());
					daoCurrency.update(currencyData);
				}
			}
		} else {
			throw new YahooException(
					"The current table 'yahoo.finance.xchange' has been blocked. It exceeded the allotted quotas of either time or instructions");
		}
	}

	@Override
	public Object[][] getAllCurrencyData(Currency currency) {
		List<CurrencyData> currencies = daoCurrency.selectListAllCurrency();
		Currency[] currencyTab = Currency.values();
		Object[][] res = new Object[currencyTab.length - 1][6];
		int i = 0;
		for (Currency c : currencyTab) {
			if (c != currency) {
				res[i][0] = c.toString();
				res[i][1] = c.getName();
				for (CurrencyData currencyData : currencies) {
					if (c == currencyData.getCurrency1() && currency == currencyData.getCurrency2()) {
						res[i][3] = currencyData.getValue().toString();
						res[i][4] = currencyData.getLastUpdate();
					}
					if (currency == currencyData.getCurrency1() && c == currencyData.getCurrency2()) {
						res[i][2] = currencyData.getValue().toString();
					}
				}
				i++;
			}
		}
		return res;
	}

	@Override
	public void updateCurrentShareValue(Portfolio portfolio, Account account, Double liquidityMovement, Double yield, Double buy,
			Double sell, Double taxe, String commentary) {
		ShareValue shareValue = new ShareValue();
		shareValue.setUserId(portfolio.getUserId());
		double montlyYield = (new BigDecimal(portfolio.getYieldYear() / 12, mathContext)).doubleValue();
		shareValue.setMonthlyYield(montlyYield);
		shareValue.setPortfolioValue(new BigDecimal(portfolio.getTotalValue(), mathContext).doubleValue());
		shareValue.setLiquidityMovement(liquidityMovement);
		shareValue.setYield(yield);
		shareValue.setBuy(buy);
		shareValue.setSell(sell);
		shareValue.setTaxe(taxe);
		shareValue.setAccount(account);
		// shareValue.setAccountName(account.getName());
		shareValue.setCommentary(commentary);
		shareValue.setDetails(portfolio.getPortfolioReview());
		ShareValue lastShareValue = daoShareValue.selectLastValue(portfolio.getUserId());
		if (lastShareValue == null) {
			shareValue.setShareQuantity(portfolio.getTotalValue() / 100);
			shareValue.setShareValue(100.0);
			daoShareValue.insert(shareValue);
		} else {
			double parity;
			if (portfolio.getCurrency() == account.getCurrency()) {
				parity = 1;
			} else {
				parity = portfolio.getCurrency().getParity(account.getCurrency());
			}
			Double quantity = lastShareValue.getShareQuantity() + (liquidityMovement * parity)
					/ ((portfolio.getTotalValue() - (liquidityMovement * parity)) / lastShareValue.getShareQuantity());
			shareValue.setShareQuantity((new BigDecimal(quantity, mathContext)).doubleValue());

			Double shareValue2 = portfolio.getTotalValue() / quantity;
			shareValue.setShareValue((new BigDecimal(shareValue2, mathContext)).doubleValue());
			daoShareValue.insert(shareValue);
		}
	}

	@Override
	public List<ShareValue> getShareValue(User user) {
		return daoShareValue.selectAllValue(user.getId());
	}

	@Override
	public List<Index> getIndexes(String yahooId, Date from, Date to) {
		List<Index> indexes = daoIndex.selectListFrom(yahooId, from, to);
		for (int i = 0; i < indexes.size(); i++) {
			Index currentIndex = indexes.get(i);
			if (i == 0) {
				currentIndex.setShareValue(100.0);
				// To make it pretty in chart
				currentIndex.setDate(from);
			} else {
				Index lastIndex = indexes.get(i - 1);
				 double shareValue = currentIndex.getValue() * lastIndex.getShareValue() / lastIndex.getValue();
				shareValue = (new BigDecimal(shareValue, mathContext)).doubleValue();
				currentIndex.setShareValue(shareValue);
			}
		}
		return indexes;
	}

	@Override
	public void updateIndex(String yahooId) throws YahooException {
		Index index = yahoo.getIndexData(yahooId);
		daoIndex.insert(index);
	}

	@Override
	public void checkUpdateIndex(String yahooId, TimeZone timeZone) throws YahooException {
		Index index = daoIndex.selectLast(yahooId);
		Calendar currentCal = Util.getCurrentCalendarInTimeZone(timeZone);
		Calendar indexCal = Util.getDateInTimeZone(index.getDate(), timeZone);
		log.debug("Check update for " + yahooId + " in timezone : " + timeZone.getDisplayName());
		log.debug("CurrentHour: " + currentCal.get(Calendar.HOUR_OF_DAY) + "h" + currentCal.get(Calendar.MINUTE)
				+ " / indexHour: " + indexCal.get(Calendar.HOUR_OF_DAY) + "h" + indexCal.get(Calendar.MINUTE));
		if (!Util.isSameDay(currentCal, indexCal)) {
			log.debug("Update index after checking! " + yahooId);
			updateIndex(yahooId);
		}
	}

	@Override
	public boolean updateIndex(String yahooId, Date from, Date to, boolean force) throws YahooException {
		List<Index> indexes;
		if (force) {
			indexes = yahoo.getIndexDataHistory(yahooId, from, to);
		} else {
			Index index = daoIndex.selectLast(yahooId);

			if (index == null) {
				indexes = yahoo.getIndexDataHistory(yahooId, from, to);
			} else {
				Date lastUpdate = index.getDate();
				Calendar cal = Calendar.getInstance();
				cal.setTime(lastUpdate);
				cal.add(Calendar.DATE, 1);
				cal.add(Calendar.HOUR_OF_DAY, 20);
				cal.add(Calendar.SECOND, 0);
				cal.add(Calendar.MILLISECOND, 0);
				indexes = yahoo.getIndexDataHistory(yahooId, cal.getTime(), to);
			}
		}
		if (!force) {
			for (Index indexTemp : indexes) {
				daoIndex.insert(indexTemp);
			}
		} else {
			for (Index indexTemp : indexes) {
				Index ind = daoIndex.selectOneIndexWithIdAndIndex(indexTemp);
				if (ind == null) {
					daoIndex.insert(indexTemp);
				}
			}
		}
		boolean res = indexes.size() == 0 ? false : true;
		return res;
	}

	@Override
	public void updateCompaniesNotRealTime() {
		List<Company> companies = daoCompany.selectAllCompany(false);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -7);
		try {
			for (Company company : companies) {
				List<Company> data = yahoo.getCompanyDataHistory(company.getYahooId(), cal.getTime(), null);
				if (data.size() != 0) {
					Company temp = data.get(0);
					company.setQuote(temp.getQuote());
					daoCompany.update(company);
				}
			}
		} catch (YahooException e) {
			log.warn("Company update not real time error: " + e.getMessage());
		}
	}

	@Override
	public void addOrUpdateCompaniesLimitedRequest(List<String> companiesYahooIdRealTime) throws YahooException {
		if (companiesYahooIdRealTime.size() <= MAX_UPDATE_COMPANY) {
			addOrUpdateCompanies(companiesYahooIdRealTime);
		} else {
			int from = 0;
			int to = MAX_UPDATE_COMPANY;
			boolean isOk = true;
			while (isOk) {
				if (to > companiesYahooIdRealTime.size()) {
					to = companiesYahooIdRealTime.size();
				}
				addOrUpdateCompanies(companiesYahooIdRealTime.subList(from, to));
				Util.makeAPause(PAUSE);
				if (to == companiesYahooIdRealTime.size()) {
					isOk = false;
				}
				from = to;
				to = to + MAX_UPDATE_COMPANY;
			}
		}
	}

	@Override
	public void updateCompaniesRealTime() {
		List<Company> companies = daoCompany.selectAllCompany(true);
		List<String> yahooIdList = new ArrayList<String>();
		for (Company c : companies) {
			if (c.getRealTime()) {
				yahooIdList.add(c.getYahooId());
			}
		}
		if (yahooIdList.size() <= MAX_UPDATE_COMPANY) {
			try {
				addOrUpdateCompanies(yahooIdList);
			} catch (YahooException e) {
				log.warn(e.getMessage());
			}
		} else {
			int from = 0;
			int to = MAX_UPDATE_COMPANY;
			boolean isOk = true;
			while (isOk) {
				if (to > yahooIdList.size()) {
					to = yahooIdList.size();
				}
				try {
					addOrUpdateCompanies(yahooIdList.subList(from, to));
				} catch (YahooException e) {
					log.error("Company update real time error: " + e.getMessage());
				}
				if (to == yahooIdList.size()) {
					isOk = false;
				}
				from = to;
				to = to + MAX_UPDATE_COMPANY;
			}
		}
	}

	@Override
	public List<Follow> getListFollow(int userId) {
		return daoFollow.selectListFollow(userId);
	}

	@Override
	public void deleteShareValue(ShareValue sv) {
		daoShareValue.delete(sv);
	}

	@Override
	public void addShareValue(ShareValue share) {
		daoShareValue.insertWithDate(share);
	}

	@Override
	public void updatePortfolio(Portfolio portfolio) {
		daoPortfolio.update(portfolio);
	}

	@Override
	public void updateUser(User user) {
		daoUser.update(user);
	}

	@Override
	public void updateOneUserPassword(User user) {
		daoUser.updateOneUserPassword(user);
	}

	@Override
	public Account selectOneAccountWithName(int userId, String name) {
		return daoAccount.selectOneAccountWithName(userId, name);
	}

	@Override
	public void addAccount(Account account) {
		daoAccount.insert(account);
	}

	@Override
	public void updateAccount(Account account) {
		daoAccount.update(account);
	}

	@Override
	public void deleteAccount(Account account) {
		daoAccount.delete(account);
	}

	@Override
	public ShareValue selectOneShareValue(int id) {
		return daoShareValue.select(id);
	}

	@Override
	public void updateCommentaryShareValue(ShareValue shareValue) {
		daoShareValue.update(shareValue);
	}

	protected void createUserPortfolio(String login) {
		User user = daoUser.selectWithLogin(login);
		Portfolio portfolio = new Portfolio();
		portfolio.setCurrency(Currency.EUR);
		portfolio.setUserId(user.getId());
		daoPortfolio.insert(portfolio);
	}

	protected void createUserDefautAccount(User u) {
		User user = daoUser.selectWithLogin(u.getLogin());
		Account account = new Account();
		account.setCurrency(Currency.EUR);
		account.setLiquidity(0.0);
		account.setName("Default");
		account.setUserId(user.getId());
		account.setDel(false);
		daoAccount.insert(account);
	}

	@Override
	public void validateUser(String login) {
		User user = daoUser.selectWithLogin(login);
		user.setAllow(true);
		daoUser.update(user);
	}

	@Override
	public void cleanDB() {
		List<Integer> companies = daoCompany.selectAllUnusedCompanyIds();
		Company company = null;
		for (Integer id : companies) {
			company = new Company();
			company.setId(id);
			daoCompany.delete(company);
		}
	}

	@Override
	public void autoUpdateUserShareValue(Calendar calendar) throws YahooException {
		boolean tryToUpdate = false, canUpdate = false;
		List<User> users = daoUser.selectAllUsers();
		Portfolio portfolio;
		Account account;
		for (User user : users) {
			log.debug("========================");
			log.debug("User : " + user.getLogin());
			if (user.getUpdateHourTime() != null) {
				int hourDiff = Util.timeZoneDiff(TimeZone.getTimeZone(user.getTimeZone()));
				int hour = Util.getRealHour(user.getUpdateHourTime(), hourDiff);

				log.debug("Current paris hour: " + calendar.get(Calendar.HOUR_OF_DAY));
				log.debug("User current time zone: " + user.getTimeZone());
				log.debug("Hour diff: " + hourDiff);
				log.debug("User wants to update at " + user.getUpdateHourTime());
				log.debug("Hour retained for user: " + hour);

				int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
				if (hour == currentHour) {
					if (!tryToUpdate) {
						canUpdate = updateAllCompanies();
						tryToUpdate = true;
					}
					if (canUpdate) {
						portfolio = getUserPortfolio(user.getId(), null, null);
						account = portfolio.getFirstAccount();
						updateCurrentShareValue(portfolio, account, 0.0, 0.0, 0.0, 0.0, 0.0, "Auto update");
					} else {
						if (user.getUpdateSendMail()) {
							StringBuilder body = new StringBuilder();
							body.append("Dear "
									+ user.getLogin()
									+ ",\n\nThe update today did not work, probably because of Yahoo's API.\nSorry for the inconvenience. You still can try do it manually."
									+ "\n\nBest regards,\nThe " + Info.NAME + " team.");
							Mail.sendMail("[Auto-update fail] " + Info.NAME, body.toString(), new String[] { user.getEmail() },
									null);
						}
					}
				}
			}
		}
	}

	protected boolean updateAllCompanies() {
		List<Company> companies = daoCompany.selectAllCompany(true);
		List<String> yahooIdList = new ArrayList<String>();
		boolean canUpdate = true;
		for (Company c : companies) {
			if (c.getRealTime()) {
				yahooIdList.add(c.getYahooId());
			}
		}
		if (yahooIdList.size() <= MAX_UPDATE_COMPANY) {
			try {
				addOrUpdateCompanies(yahooIdList);
			} catch (YahooException e) {
				canUpdate = false;
				log.warn("All companies update failed: " + e.getMessage());
			}
		} else {
			int from = 0;
			int to = MAX_UPDATE_COMPANY;
			boolean isOk = true;
			while (isOk) {
				if (to > yahooIdList.size()) {
					to = yahooIdList.size();
				}
				try {
					addOrUpdateCompanies(yahooIdList.subList(from, to));
					Util.makeAPause(PAUSE);
				} catch (YahooException e) {
					canUpdate = false;
					isOk = false;
					log.warn("All companies update failed: " + e.getMessage() + " | Issue trying to update at limit [" + from
							+ ", " + to + "]");
				}
				if (to == yahooIdList.size()) {
					isOk = false;
				}
				from = to;
				to = to + MAX_UPDATE_COMPANY;
			}
		}
		return canUpdate;
	}
}
