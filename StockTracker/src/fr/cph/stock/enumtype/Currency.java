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

public enum Currency {
//	AUD("Australia Dollar", "$", "AUD"), 
//	CAD("Canada Dollar", "$", "CAD"), 
//	CHF("Switzerland Franc", "CHF", "CHF"), 
	EUR("Euro", "€", "EUR"), 
	GBP("United Kingdom Pound", "£", "GBP"), 
//	HKD("Hong Kong Dollar", "$", "HKD"), 
//	SGD("Singapore Dollar", "$", "SGD"), 
	USD("United States Dollar", "$", "USD");
//	ZAR("South Africa Rand", "R", "ZAR");

	Currency(String name, String symbol, String code) {
		this.name = name;
		this.symbol = symbol;
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public String getSymbol() {
		return symbol;
	}

	public String getCode() {
		return code;
	}

	public List<CurrencyData> getCurrencyData() {
		return currencyData;
	}

	public void setCurrencyData(List<CurrencyData> currencyData) {
		this.currencyData = currencyData;
	}

	public Double getParity(Currency currency) {
		Double res = new Double(0);
		for (CurrencyData currencyData : getCurrencyData()) {
			if (currencyData.getCurrency1() == currency) {
				res = currencyData.getValue();
				break;
			}
		}
		return res;
	}

	public static Currency getEnum(String value) {
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
	
	public JSONObject getJSONObject(){
		JSONObject json = new JSONObject();
		json.put("name", getName());
		json.put("symbol", getSymbol());
		json.put("code", getCode());
		return json;
	}

	private List<CurrencyData> currencyData;
	private String name;
	private String symbol;
	private String code;

}
