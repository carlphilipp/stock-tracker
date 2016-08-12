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

package fr.cph.stock.business.impl;

import fr.cph.stock.business.Business;
import fr.cph.stock.business.CompanyBusiness;
import fr.cph.stock.business.UserBusiness;
import fr.cph.stock.dao.*;
import fr.cph.stock.entities.*;
import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.exception.YahooException;
import fr.cph.stock.external.IExternalDataAccess;
import fr.cph.stock.external.YahooExternalDataAccess;
import fr.cph.stock.util.Info;
import fr.cph.stock.util.Mail;
import fr.cph.stock.util.Util;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;

/**
 * BusinessImpl class that access database and process data
 *
 * @author Carl-Philipp Harmant
 * @version 1
 */
public enum BusinessImpl implements Business {

	INSTANCE;

	private static final Logger LOG = Logger.getLogger(BusinessImpl.class);
	private static final MathContext MATHCONTEXT = MathContext.DECIMAL32;
	private static final int PERCENT = 100;
	private static final int PAUSE = 1000;

	private final IExternalDataAccess yahoo;
	private final PortfolioDAO daoPortfolio;
	private final UserDAO daoUser;
	private final CurrencyDAO daoCurrency;
	private final ShareValueDAO daoShareValue;
	private final IndexDAO daoIndex;
	private final AccountDAO daoAccount;

	private final CompanyBusiness companyBusiness;
	private final UserBusiness userBusiness;

	BusinessImpl() {
		yahoo = new YahooExternalDataAccess();
		daoPortfolio = new PortfolioDAO();
		daoUser = new UserDAO();
		daoCurrency = new CurrencyDAO();
		daoShareValue = new ShareValueDAO();
		daoIndex = new IndexDAO();
		daoAccount = new AccountDAO();
		companyBusiness = CompanyBusinessImpl.INSTANCE;
		userBusiness = UserBusinessImpl.INSTANCE;
	}

	// Currency
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

	// Share value
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

	@Override
	public final void deleteShareValue(final ShareValue sv) {
		daoShareValue.delete(sv);
	}

	@Override
	public final void addShareValue(final ShareValue share) {
		daoShareValue.insertWithDate(share);
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
						canUpdate = companyBusiness.updateAllCompanies();
						tryToUpdate = true;
					}
					if (canUpdate) {
						LOG.info("Update user portfolio: " + user.getLogin());
						portfolio = userBusiness.getUserPortfolio(user.getId(), null, null);
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
	public final ShareValue selectOneShareValue(final int id) {
		return daoShareValue.select(id);
	}

	@Override
	public final void updateCommentaryShareValue(final ShareValue shareValue) {
		daoShareValue.update(shareValue);
	}

	// Indexes
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

	// Account
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
}
