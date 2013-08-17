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

package fr.cph.stock.enumtype;

import java.util.List;

import net.sf.json.JSONObject;
import fr.cph.stock.entities.CurrencyData;

/**
 * Enum that represents the currencies
 * 
 * @author Carl-Philipp Harmant
 * 
 */
public enum Currency {
	// AUD("Australia Dollar", "$", "AUD"),
	// CAD("Canada Dollar", "$", "CAD"),
	// CHF("Switzerland Franc", "CHF", "CHF"),
	/** **/
	EUR("Euro", "€", "EUR"), GBP("United Kingdom Pound", "£", "GBP"),
	// HKD("Hong Kong Dollar", "$", "HKD"),
	// SGD("Singapore Dollar", "$", "SGD"),
	/** **/
	USD("United States Dollar", "$", "USD");
	// ZAR("South Africa Rand", "R", "ZAR");

	/**
	 * Constructor
	 * 
	 * @param n
	 *            the name
	 * @param sy
	 *            the symbol
	 * @param c
	 *            the code
	 */
	Currency(final String n, final String sy, final String c) {
		this.name = n;
		this.symbol = sy;
		this.code = c;
	}

	/**
	 * Get the name
	 * 
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Get Symbol
	 * 
	 * @return the symbol
	 */
	public final String getSymbol() {
		return symbol;
	}

	/**
	 * Get the code
	 * 
	 * @return the code
	 */
	public final String getCode() {
		return code;
	}

	/**
	 * Get a list of currency data
	 * 
	 * @return the list of currency data
	 */
	public final List<CurrencyData> getCurrencyData() {
		return currencyData;
	}

	/**
	 * Set currency data list
	 * 
	 * @param currencyD
	 *            the currency data list
	 */
	public final void setCurrencyData(final List<CurrencyData> currencyD) {
		this.currencyData = currencyD;
	}

	/**
	 * Get parity
	 * 
	 * @param currency
	 *            the currency
	 * @return the parity
	 */
	public final Double getParity(final Currency currency) {
		Double res = new Double(0);
		for (CurrencyData currencyD : getCurrencyData()) {
			if (currencyD.getCurrency1() == currency) {
				res = currencyD.getValue();
				break;
			}
		}
		return res;
	}

	/**
	 * Get enum from str
	 * 
	 * @param value
	 *            the str value
	 * @return the currency
	 */
	public static Currency getEnum(final String value) {
		if (value == null) {
			throw new IllegalArgumentException();
		}
		for (Currency c : values()) {
			if (value.equalsIgnoreCase(c.getCode())) {
				return c;
			}
		}
		throw new IllegalArgumentException();
	}

	/**
	 * Get jsonobject
	 * 
	 * @return the JSONObject
	 */
	public final JSONObject getJSONObject() {
		JSONObject json = new JSONObject();
		json.put("name", getName());
		json.put("symbol", getSymbol());
		json.put("code", getCode());
		return json;
	}

	/** List of currency data **/
	private List<CurrencyData> currencyData;
	/** name **/
	private String name;
	/** symbol **/
	private String symbol;
	/** cod **/
	private String code;

}
