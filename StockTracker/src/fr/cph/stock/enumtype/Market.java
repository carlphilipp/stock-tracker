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

/**
 * Enum that represents the market place
 * 
 * @author Carl-Philipp Harmant
 * 
 */
public enum Market {
	/** **/
	PARIS("Paris"),
	/** **/
	NASDAQNM("NasdaqNM"),
	/** **/
	NYSE("Nyse"),
	/** **/
	AMSTERDAM("Amsterdam"),
	/** **/
	XETRA("Xetra"),
	/** **/
	NGM("NGM"),
	/** **/
	NCM("NCM"),
	/** **/
	LONDON("London"),
	/** **/
	MILAN("Milan"),
	/** **/
	PCX("PCX"),
	/** **/
	AMEX("AMEX"),
	/** **/
	FRANKFURT("Frankfurt"),
	/** **/
	OTC("Other OTC"),
	/** **/
	UNKNOWN("unknown");

	/**
	 * Constructor
	 * 
	 * @param market
	 *            the market type
	 */
	Market(final String market) {
		this.marketType = market;
	}

	/**
	 * Get market
	 * 
	 * @return the market
	 */
	public String getMarket() {
		return marketType;
	}

	/**
	 * Get market
	 * 
	 * @param mark
	 *            the market
	 * @return the market
	 */
	public static Market getMarket(final String mark) {
		Market market = null;
		try {
			market = valueOf(mark);
		} catch (Exception e) {
			if (mark.equals("OTHER OTC")) {
				market = Market.OTC;
			} else {
				market = Market.UNKNOWN;
			}
		}
		return market;
	}

	/**
	 * Get market from suffix
	 * 
	 * @param suffix
	 *            the suffix
	 * @return the market
	 */
	public static Market getMarketFromSuffix(final String suffix) {
		Market market;
		switch (suffix) {
		case "PA":
			market = Market.PARIS;
			break;
		case "AM":
			market = Market.AMSTERDAM;
			break;
		default:
			market = null;
		}
		return market;
	}

	/**
	 * Get currency from the given market
	 * 
	 * @param m
	 *            the market
	 * @return the currency
	 */
	public static Currency getCurrency(final Market m) {
		Currency currency = null;
		switch (m) {
		case PARIS:
		case AMSTERDAM:
		case XETRA:
		case MILAN:
		case FRANKFURT:
		case OTC:
			currency = Currency.EUR;
			break;
		case NASDAQNM:
		case NYSE:
		case NGM:
		case PCX:
			currency = Currency.USD;
			break;
		case LONDON:
			currency = Currency.GBP;
			break;
		case NCM:
			currency = Currency.AUD;
			break;
		case UNKNOWN:
			currency = null;
			break;
		default:
			break;
		}
		return currency;
	}

	/** Market type **/
	private String marketType;
}
