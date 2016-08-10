/**
 * Copyright 2013 Carl-Philipp Harmant
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

import fr.cph.stock.dao.*;
import fr.cph.stock.entities.*;
import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.enumtype.Market;
import fr.cph.stock.exception.EquityException;
import fr.cph.stock.exception.LoginException;
import fr.cph.stock.exception.YahooException;
import fr.cph.stock.exception.YahooUnknownTickerException;
import fr.cph.stock.external.IExternalDataAccess;
import fr.cph.stock.external.YahooExternalDataAccess;
import fr.cph.stock.security.Security;
import fr.cph.stock.util.Info;
import fr.cph.stock.util.Mail;
import fr.cph.stock.util.Util;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Business class that access database and process data
 *
 * @author Carl-Philipp Harmant
 * @version 1
 */
public final class Business implements IBusiness {

	private static final Logger LOG = Logger.getLogger(Business.class);
	private static final Object LOCK = new Object();
	private static final MathContext MATHCONTEXT = MathContext.DECIMAL32;
	private static final int MAX_UPDATE_COMPANY = 15;
	private static final int PAUSE = 1000;
	private static final int PERCENT = 100;
	private static Business BUSINESS;

	private final IExternalDataAccess yahoo;
	private final CompanyDAO daoCompany;
	private final PortfolioDAO daoPortfolio;
	private final EquityDAO daoEquity;
	private final UserDAO daoUser;
	private final CurrencyDAO daoCurrency;
	private final ShareValueDAO daoShareValue;
	private final IndexDAO daoIndex;
	private final FollowDAO daoFollow;
	private final AccountDAO daoAccount;

	private Business() {
		this.yahoo = new YahooExternalDataAccess();
		this.daoCompany = new CompanyDAO();
		this.daoPortfolio = new PortfolioDAO();
		this.daoEquity = new EquityDAO();
		this.daoUser = new UserDAO();
		this.daoCurrency = new CurrencyDAO();
		this.daoShareValue = new ShareValueDAO();
		this.daoIndex = new IndexDAO();
		this.daoFollow = new FollowDAO();
		this.daoAccount = new AccountDAO();
	}

	/**
	 * Static singleton getter
	 *
	 * @return a IBusiness instance
	 */
	public static IBusiness getInstance() {
		if (BUSINESS == null) {
			synchronized (LOCK) {
				if (BUSINESS == null) {
					BUSINESS = new Business();
				}
			}
		}
		return BUSINESS;
	}

	@Override
	public final void createEquity(final int userId, final String ticker, final Equity equity) throws EquityException, YahooException {
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
	public final void createManualEquity(final int userId, final Company company, final Equity equity) throws EquityException {
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
			throw new EquityException(company.getName() + EquityException.ENTITY_ALREADY_RECORDED);
		} else {
			daoEquity.insert(equity);
		}
	}

	@Override
	public final void updateEquity(final int userId, final String ticker, final Equity equity) throws UnsupportedEncodingException, YahooException {
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
	public final void deleteEquity(final Equity equity) {
		daoEquity.delete(equity);
	}

	@Override
	public final List<Company> addOrUpdateCompanies(final List<String> tickers) throws YahooException {
		LOG.debug("Updating: " + tickers);
		List<Company> companies = yahoo.getCompaniesData(tickers);
		List<Company> companiesResult = new ArrayList<>();
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
				// companyInDB.setCurrency(Market.getCurrency(companyYahoo.getMarket()));
				companyInDB.setCurrency(companyYahoo.getCurrency());
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
	public final void addFollow(final User user, final String ticker, final Double lower, final Double higher) throws YahooException {
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
	public final void updateFollow(final User user, final String ticker, final Double lower, final Double higher) {
		Company company = daoCompany.selectWithYahooId(ticker);
		Follow foll = daoFollow.selectOneFollow(user.getId(), company.getId());
		foll.setLowerLimit(lower);
		foll.setHigherLimit(higher);
		daoFollow.update(foll);
	}

	@Override
	public final void deleteFollow(final int id) {
		Follow follow = new Follow();
		follow.setId(id);
		daoFollow.delete(follow);
	}

	@Override
	public final void createUser(final String login, final String md5Password, final String email) throws NoSuchAlgorithmException,
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
		body.append("Welcome to ")
			.append(Info.NAME)
			.append(",\n\nPlease valid your account by clicking on that link:")
			.append(Info.ADDRESS)
			.append(Info.FOLDER)
			.append("/check?&login=")
			.append(login)
			.append("&check=")
			.append(check)
			.append(".\n\nBest regards,\nThe ")
			.append(Info.NAME)
			.append(" team.");
		Mail.sendMail("[Registration] " + Info.NAME, body.toString(), new String[]{email}, null);
		createUserPortfolio(user.getLogin());
		createUserDefaultAccount(user);
	}

	@Override
	public final User getUser(final String login) {
		return daoUser.selectWithLogin(login);
	}

	@Override
	public final User getUserWithEmail(final String email) {
		return daoUser.selectWithEmail(email);
	}

	@Override
	public final void deleteUser(final String login) {
		User user = new User();
		user.setLogin(login);
		daoUser.delete(user);
	}

	@Override
	public final User checkUser(final String login, final String md5Password) throws LoginException {
		User user = daoUser.selectWithLogin(login);
		final int sixtyFour = 64;
		if (user != null) {
			String md5PasswordHashed;
			try {
				md5PasswordHashed = Security.encodeToSha256(md5Password);
				String saltHashed = user.getPassword().substring(0, sixtyFour);
				String cryptedPasswordSalt = user.getPassword().substring(sixtyFour, user.getPassword().length());
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
	public final Portfolio getUserPortfolio(final int userId, final Date from, final Date to) throws YahooException {
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
		liquidity = new BigDecimal(liquidity, MATHCONTEXT).doubleValue();
		portfolio.setLiquidity(liquidity);
		portfolio.compute();
		return portfolio;
	}

	@Override
	public final void updateLiquidity(final Account account, final double liquidity) {
		account.setLiquidity(liquidity);
		daoAccount.update(account);
	}

	@Override
	public final Currency loadCurrencyData(final Currency currency) throws YahooException {
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
	public final void updateAllCurrencies() throws YahooException {
		List<Currency> currencyDone = new ArrayList<>();
		for (Currency currency : Currency.values()) {
			List<CurrencyData> currenciesData = yahoo.getCurrencyData(currency);
			Util.makeAPause(PAUSE);
			if ((Currency.values().length - 1) * 2 == currenciesData.size()) {
				for (CurrencyData currencyData : currenciesData) {
					if (!(currencyDone.contains(currencyData.getCurrency1()) || currencyDone.contains(currencyData.getCurrency2()))) {
						CurrencyData c = daoCurrency.selectOneCurrencyDataWithParam(currencyData);
						if (c == null) {
							daoCurrency.insert(currencyData);
						} else {
							currencyData.setId(c.getId());
							daoCurrency.update(currencyData);
						}
					}
				}
				currencyDone.add(currency);
			} else {
				LOG.warn("Impossible to update this currency: " + currency.getCode());
			}
		}
	}

	@Override
	public final void updateOneCurrency(final Currency currency) throws YahooException {
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
	public final Object[][] getAllCurrencyData(final Currency currency) {
		final List<CurrencyData> currencies = daoCurrency.selectListAllCurrency();
		final Currency[] currencyTab = Currency.values();
		final Object[][] res = new Object[currencyTab.length - 1][6];
		int i = 0;
		for (final Currency c : currencyTab) {
			if (c != currency) {
				res[i][0] = c.toString();
				res[i][1] = c.getName();
				for (final CurrencyData currencyData : currencies) {
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
	public final void updateCurrentShareValue(final Portfolio portfolio, final Account account, final Double liquidityMovement, final Double yield,
											  final Double buy, final Double sell, final Double taxe, final String commentary) {
		final ShareValue shareValue = new ShareValue();
		shareValue.setUserId(portfolio.getUserId());
		final double monthlyYield = new BigDecimal(portfolio.getYieldYear() / 12, MATHCONTEXT).doubleValue();
		shareValue.setMonthlyYield(monthlyYield);
		shareValue.setPortfolioValue(new BigDecimal(portfolio.getTotalValue(), MATHCONTEXT).doubleValue());
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
			shareValue.setShareQuantity(portfolio.getTotalValue() / PERCENT);
			shareValue.setShareValue((double) PERCENT);
			daoShareValue.insert(shareValue);
		} else {
			double parity;
			if (portfolio.getCurrency() == account.getCurrency()) {
				parity = 1;
			} else {
				parity = portfolio.getCurrency().getParity(account.getCurrency());
			}
			final Double quantity = lastShareValue.getShareQuantity() + (liquidityMovement * parity)
				/ ((portfolio.getTotalValue() - liquidityMovement * parity) / lastShareValue.getShareQuantity());
			shareValue.setShareQuantity(new BigDecimal(quantity, MATHCONTEXT).doubleValue());

			final Double shareValue2 = portfolio.getTotalValue() / quantity;
			shareValue.setShareValue(new BigDecimal(shareValue2, MATHCONTEXT).doubleValue());
			daoShareValue.insert(shareValue);
		}
	}

	// @Override
	// public final List<ShareValue> getShareValue(final User user) {
	// return daoShareValue.selectAllValue(user.getId());
	// }

	@Override
	public final List<Index> getIndexes(final String yahooId, final Date from, final Date to) {
		final List<Index> indexes = daoIndex.selectListFrom(yahooId, from, to);
		for (int i = 0; i < indexes.size(); i++) {
			final Index currentIndex = indexes.get(i);
			if (i == 0) {
				currentIndex.setShareValue((double) PERCENT);
				// To make it pretty in chart
				currentIndex.setDate(from);
			} else {
				final Index lastIndex = indexes.get(i - 1);
				double shareValue = currentIndex.getValue() * lastIndex.getShareValue() / lastIndex.getValue();
				shareValue = new BigDecimal(shareValue, MATHCONTEXT).doubleValue();
				currentIndex.setShareValue(shareValue);
			}
		}
		return indexes;
	}

	@Override
	public final void updateIndex(final String yahooId) throws YahooException {
		final Index index = yahoo.getIndexData(yahooId);
		daoIndex.insert(index);
	}

	@Override
	public final void checkUpdateIndex(final String yahooId, final TimeZone timeZone) throws YahooException {
		final Index index = daoIndex.selectLast(yahooId);
		final Calendar currentCal = Util.getCurrentCalendarInTimeZone(timeZone);
		final Calendar indexCal = Util.getDateInTimeZone(index.getDate(), timeZone);
		LOG.debug("Check update for " + yahooId + " in timezone : " + timeZone.getDisplayName());
		LOG.debug("CurrentHour: " + currentCal.get(Calendar.HOUR_OF_DAY) + "h" + currentCal.get(Calendar.MINUTE) + " / indexHour: "
			+ indexCal.get(Calendar.HOUR_OF_DAY) + "h" + indexCal.get(Calendar.MINUTE));
		if (!Util.isSameDay(currentCal, indexCal)) {
			LOG.debug("Update index after checking! " + yahooId);
			updateIndex(yahooId);
		}
	}

	// @Override
	// public final boolean updateIndex(final String yahooId, final Date from, final Date to, final boolean force)
	// throws YahooException {
	// List<Index> indexes;
	// if (force) {
	// indexes = yahoo.getIndexDataHistory(yahooId, from, to);
	// } else {
	// Index index = daoIndex.selectLast(yahooId);
	//
	// if (index == null) {
	// indexes = yahoo.getIndexDataHistory(yahooId, from, to);
	// } else {
	// Date lastUpdate = index.getDate();
	// Calendar cal = Calendar.getInstance();
	// cal.setTime(lastUpdate);
	// cal.add(Calendar.DATE, 1);
	// cal.add(Calendar.HOUR_OF_DAY, 20);
	// cal.add(Calendar.SECOND, 0);
	// cal.add(Calendar.MILLISECOND, 0);
	// indexes = yahoo.getIndexDataHistory(yahooId, cal.getTime(), to);
	// }
	// }
	// if (!force) {
	// for (Index indexTemp : indexes) {
	// daoIndex.insert(indexTemp);
	// }
	// } else {
	// for (Index indexTemp : indexes) {
	// Index ind = daoIndex.selectOneIndexWithIdAndIndex(indexTemp);
	// if (ind == null) {
	// daoIndex.insert(indexTemp);
	// }
	// }
	// }
	// boolean res = indexes.size() == 0 ? false : true;
	// return res;
	// }

	@Override
	public final void updateCompaniesNotRealTime() {
		final List<Company> companies = daoCompany.selectAllCompany(false);
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -7);
		try {
			for (final Company company : companies) {
				final List<Company> data = yahoo.getCompanyDataHistory(company.getYahooId(), cal.getTime(), null);
				if (data.size() != 0) {
					final Company temp = data.get(0);
					company.setQuote(temp.getQuote());
					daoCompany.update(company);
				}
			}
		} catch (YahooException e) {
			LOG.warn("Company update not real time error: " + e.getMessage());
		}
	}

	@Override
	public final String addOrUpdateCompaniesLimitedRequest(final List<String> companiesYahooIdRealTime) throws YahooException {
		final StringBuilder sb = new StringBuilder();
		if (companiesYahooIdRealTime.size() <= MAX_UPDATE_COMPANY) {
			try {
				addOrUpdateCompanies(companiesYahooIdRealTime);
			} catch (final YahooUnknownTickerException e) {
				sb.append(e.getMessage()).append(" ");
			}
		} else {
			int from = 0;
			int to = MAX_UPDATE_COMPANY;
			boolean isOk = true;
			while (isOk) {
				if (to > companiesYahooIdRealTime.size()) {
					to = companiesYahooIdRealTime.size();
				}
				try {
					addOrUpdateCompanies(companiesYahooIdRealTime.subList(from, to));
					Util.makeAPause(PAUSE);
				} catch (final YahooUnknownTickerException e) {
					sb.append(e.getMessage()).append(" ");
				}
				if (to == companiesYahooIdRealTime.size()) {
					isOk = false;
				}
				from = to;
				to = to + MAX_UPDATE_COMPANY;
			}
		}
		return sb.toString();
	}

	// @Override
	// public final void updateCompaniesRealTime() {
	// List<Company> companies = daoCompany.selectAllCompany(true);
	// List<String> yahooIdList = new ArrayList<>();
	// for (Company c : companies) {
	// if (c.getRealTime()) {
	// yahooIdList.add(c.getYahooId());
	// }
	// }
	// if (yahooIdList.size() <= MAX_UPDATE_COMPANY) {
	// try {
	// addOrUpdateCompanies(yahooIdList);
	// } catch (YahooException e) {
	// LOG.warn(e.getMessage());
	// }
	// } else {
	// int from = 0;
	// int to = MAX_UPDATE_COMPANY;
	// boolean isOk = true;
	// while (isOk) {
	// if (to > yahooIdList.size()) {
	// to = yahooIdList.size();
	// }
	// try {
	// addOrUpdateCompanies(yahooIdList.subList(from, to));
	// } catch (YahooException e) {
	// LOG.error("Company update real time error: " + e.getMessage());
	// }
	// if (to == yahooIdList.size()) {
	// isOk = false;
	// }
	// from = to;
	// to = to + MAX_UPDATE_COMPANY;
	// }
	// }
	// }

	@Override
	public final List<Follow> getListFollow(final int userId) {
		return daoFollow.selectListFollow(userId);
	}

	@Override
	public final void deleteShareValue(final ShareValue sv) {
		daoShareValue.delete(sv);
	}

	@Override
	public final void addShareValue(final ShareValue share) {
		daoShareValue.insertWithDate(share);
	}

	@Override
	public final void updatePortfolio(final Portfolio portfolio) {
		daoPortfolio.update(portfolio);
	}

	@Override
	public final void updateUser(final User user) {
		daoUser.update(user);
	}

	@Override
	public final void updateOneUserPassword(final User user) {
		daoUser.updateOneUserPassword(user);
	}

	// @Override
	// public final Account selectOneAccountWithName(final int userId, final String name) {
	// return daoAccount.selectOneAccountWithName(userId, name);
	// }

	@Override
	public final void addAccount(final Account account) {
		daoAccount.insert(account);
	}

	@Override
	public final void updateAccount(final Account account) {
		daoAccount.update(account);
	}

	@Override
	public final void deleteAccount(final Account account) {
		daoAccount.delete(account);
	}

	@Override
	public final ShareValue selectOneShareValue(final int id) {
		return daoShareValue.select(id);
	}

	@Override
	public final void updateCommentaryShareValue(final ShareValue shareValue) {
		daoShareValue.update(shareValue);
	}

	@Override
	public final void validateUser(final String login) {
		final User user = daoUser.selectWithLogin(login);
		user.setAllow(true);
		daoUser.update(user);
	}

	@Override
	public final void deleteCompany(final Company company) {
		daoCompany.delete(company);
	}

	@Override
	public final void cleanDB() {
		final List<Integer> companies = daoCompany.selectAllUnusedCompanyIds();
		Company company;
		for (final Integer id : companies) {
			company = new Company();
			company.setId(id);
			daoCompany.delete(company);
		}
	}

	@Override
	public final void autoUpdateUserShareValue(final Calendar calendar) throws YahooException {
		boolean tryToUpdate = false, canUpdate = false;
		final List<User> users = daoUser.selectAllUsers();
		Portfolio portfolio;
		Account account;
		for (final User user : users) {
			if (user.getUpdateHourTime() != null) {
				final int hourDiff = Util.timeZoneDiff(TimeZone.getTimeZone(user.getTimeZone()));
				final int hour = Util.getRealHour(user.getUpdateHourTime(), hourDiff);

				/*
				 * if (user.getLogin().equals("carl") || user.getLogin().equals("carlphilipp")) { LOG.info("========================");
				 * LOG.info("User : " + user.getLogin()); LOG.info("Current paris hour: " + calendar.get(Calendar.HOUR_OF_DAY));
				 * LOG.info("User current time zone: " + user.getTimeZone()); LOG.info("Hour diff: " + hourDiff); LOG.info("User wants to update at "
				 * + user.getUpdateHourTime()); LOG.info("Hour retained for user: " + hour); }
				 */

				final int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
				if (hour == currentHour) {
					if (!tryToUpdate) {
						canUpdate = updateAllCompanies();
						tryToUpdate = true;
					}
					if (canUpdate) {
						LOG.info("Update user portfolio: " + user.getLogin());
						portfolio = getUserPortfolio(user.getId(), null, null);
						account = portfolio.getFirstAccount();
						updateCurrentShareValue(portfolio, account, 0.0, 0.0, 0.0, 0.0, 0.0, "Auto update");
					} else {
						if (user.getUpdateSendMail()) {
							final String body = ("Dear "
								+ user.getLogin()
								+ ",\n\nThe update today did not work, probably because of Yahoo's API.\nSorry for the inconvenience. You still can try do it manually."
								+ "\n\nBest regards,\nThe " + Info.NAME + " team.");
							Mail.sendMail("[Auto-update fail] " + Info.NAME, body, new String[]{user.getEmail()}, null);
						}
					}
				}
			}
		}
	}

	@Override
	public final Company createManualCompany(final String name, final String industry, final String sector, final Currency currency, final double quote) {
		final Company company = new Company();
		final String uuid = UUID.randomUUID().toString();
		company.setYahooId(uuid);
		company.setName(name);
		company.setCurrency(currency);
		company.setIndustry(industry);
		company.setQuote(quote);
		company.setSector(sector);
		company.setManual(true);
		company.setRealTime(false);
		company.setFund(false);
		daoCompany.insert(company);
		return daoCompany.selectWithYahooId(uuid);
	}

	@Override
	public void updateCompanyManual(final Integer companyId, final Double newQuote) {
		final Company company = daoCompany.select(companyId);
		company.setQuote(newQuote);
		daoCompany.update(company);
	}

	/**
	 * @return a boolean
	 */
	private boolean updateAllCompanies() {
		final List<Company> companies = daoCompany.selectAllCompany(true);
		final List<String> yahooIdList = new ArrayList<>();
		boolean canUpdate = true;
		for (final Company c : companies) {
			if (c.getRealTime()) {
				yahooIdList.add(c.getYahooId());
			}
		}
		if (yahooIdList.size() <= MAX_UPDATE_COMPANY) {
			try {
				addOrUpdateCompanies(yahooIdList);
			} catch (final YahooUnknownTickerException e) {
				LOG.warn(e.getMessage());
				Mail.sendMail("[Error] " + Info.NAME, e.getMessage(), Info.ADMINS.toArray(new String[Info.ADMINS.size()]), null);
			} catch (final YahooException e) {
				canUpdate = false;
				LOG.warn("All companies update failed: " + e.getMessage());
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
				} catch (final YahooUnknownTickerException e) {
					LOG.warn(e.getMessage());
					Mail.sendMail("[Error] " + Info.NAME, e.getMessage(), Info.ADMINS.toArray(new String[Info.ADMINS.size()]), null);
				} catch (final YahooException e) {
					canUpdate = false;
					isOk = false;
					LOG.warn("All companies update failed: " + e.getMessage() + " | Issue trying to update at limit [" + from + ", " + to + "]");
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

	/**
	 * @param ticker the ticker
	 * @return a company
	 * @throws YahooException the yahoo exception
	 */
	private Company addOrUpdateCompany(final String ticker) throws YahooException {
		final List<String> tickers = new ArrayList<>();
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

	/**
	 * @param login the login
	 */
	private final void createUserPortfolio(final String login) {
		final User user = daoUser.selectWithLogin(login);
		final Portfolio portfolio = new Portfolio();
		portfolio.setCurrency(Currency.EUR);
		portfolio.setUserId(user.getId());
		daoPortfolio.insert(portfolio);
	}

	/**
	 * @param u the user
	 */
	private final void createUserDefaultAccount(final User u) {
		final User user = daoUser.selectWithLogin(u.getLogin());
		final Account account = new Account();
		account.setCurrency(Currency.EUR);
		account.setLiquidity(0.0);
		account.setName("Default");
		account.setUserId(user.getId());
		account.setDel(false);
		daoAccount.insert(account);
	}
}
