package fr.cph.stock.business;

import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.exception.YahooException;

public interface CurrencyBusiness {

	/**
	 * Load a currency with its data
	 *
	 * @param currency the currency
	 * @return a currency
	 * @throws YahooException the yahoo exception
	 */
	Currency loadCurrencyData(Currency currency) throws YahooException;

	/**
	 * Update all current currencies
	 *
	 * @throws YahooException the yahoo exception
	 */
	void updateAllCurrencies() throws YahooException;

	/**
	 * Update one currency
	 *
	 * @param currency the currency
	 * @throws YahooException the yahoo exception
	 */
	void updateOneCurrency(Currency currency) throws YahooException;

	/**
	 * Get all currency data
	 *
	 * @param currency the currency
	 * @return a 2 dim array of object
	 */
	Object[][] getAllCurrencyData(Currency currency);
}
