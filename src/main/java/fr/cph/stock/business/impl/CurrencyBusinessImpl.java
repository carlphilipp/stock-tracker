package fr.cph.stock.business.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import fr.cph.stock.business.CurrencyBusiness;
import fr.cph.stock.dao.CurrencyDAO;
import fr.cph.stock.dao.DAO;
import fr.cph.stock.entities.CurrencyData;
import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.exception.YahooException;
import fr.cph.stock.external.ExternalDataAccess;
import fr.cph.stock.util.Util;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
@Singleton
public class CurrencyBusinessImpl implements CurrencyBusiness {

	private static final int PAUSE = 1000;

	private ExternalDataAccess yahoo;
	private CurrencyDAO currencyDAO;

	@Inject
	public CurrencyBusinessImpl(final ExternalDataAccess yahoo, @Named("Currency") final DAO dao) {
		this.yahoo = yahoo;
		currencyDAO = (CurrencyDAO) dao;
	}

	@Override
	public final Currency loadCurrencyData(final Currency currency) throws YahooException {
		List<CurrencyData> currencyDataList = currencyDAO.selectListCurrency(currency.getCode());
		if (currencyDataList.size() == 0) {
			final List<CurrencyData> currenciesData = yahoo.getCurrencyData(currency);
			updateOrInsertCurrency(currenciesData);
			currencyDataList = currencyDAO.selectListCurrency(currency.getCode());
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
				for (final CurrencyData currencyData : currenciesData) {
					if (!(currencyDone.contains(currencyData.getCurrency1()) || currencyDone.contains(currencyData.getCurrency2()))) {
						Optional<CurrencyData> c = currencyDAO.selectOneCurrencyDataWithParam(currencyData);
						if (c.isPresent()) {
							currencyData.setId(c.get().getId());
							currencyDAO.update(currencyData);
						} else {
							currencyDAO.insert(currencyData);
						}
					}
				}
				currencyDone.add(currency);
			} else {
				log.warn("Impossible to update this currency: {}", currency.getCode());
			}
		}
	}

	@Override
	public final void updateOneCurrency(final Currency currency) throws YahooException {
		List<CurrencyData> currenciesData = yahoo.getCurrencyData(currency);
		if ((Currency.values().length - 1) * 2 == currenciesData.size()) {
			updateOrInsertCurrency(currenciesData);
		} else {
			throw new YahooException("The current table 'yahoo.finance.xchange' has been blocked. It exceeded the allotted quotas of either time or instructions");
		}
	}

	@Override
	public final Object[][] getAllCurrencyData(final Currency currency) {
		final List<CurrencyData> currencies = currencyDAO.selectListAllCurrency();
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

	private void updateOrInsertCurrency(final List<CurrencyData> currenciesData) {
		for (final CurrencyData currencyData : currenciesData) {
			final Optional<CurrencyData> c = currencyDAO.selectOneCurrencyDataWithParam(currencyData);
			if (c.isPresent()) {
				currencyData.setId(c.get().getId());
				currencyDAO.update(currencyData);
			} else {
				currencyDAO.insert(currencyData);
			}
		}
	}
}
