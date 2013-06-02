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

package fr.cph.stock.entities;

import net.sf.json.JSONObject;
import fr.cph.stock.enumtype.Currency;

/**
 * This class represents the account of the user
 * 
 * @author Carl-Philipp Harmant
 * 
 */
public class Account {

	/** id **/
	private int id;
	/** user id **/
	private int userId;
	/** name of the account **/
	private String name;
	/** currnecy of the account **/
	private Currency currency;
	/** liquidity of the account **/
	private Double liquidity;
	/** is it allowed to delete it from db ? **/
	private Boolean del;
	/** parity of the account **/
	private Double parity;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public double getLiquidity() {
		return liquidity;
	}

	public void setLiquidity(Double liquidity) {
		this.liquidity = liquidity;
	}

	public String toString() {
		return "[Account;id=" + id + "userId=" + userId + ";name=" + name + ";currency=" + currency + ";liquidity=" + liquidity
				+ "]";
	}

	public Boolean getDel() {
		return del;
	}

	public void setDel(Boolean del) {
		this.del = del;
	}

	public Double getParity() {
		return parity;
	}

	public void setParity(Double parity) {
		this.parity = parity;
	}

	/**
	 * Get a JSONObject of the current object
	 * 
	 * @return a json object
	 */
	public JSONObject getJSONObject() {
		JSONObject json = new JSONObject();
		json.accumulate("id", id);
		json.accumulate("name", name);
		json.accumulate("currency", currency.getCode());
		json.accumulate("liquidity", liquidity);
		return json;
	}

}
