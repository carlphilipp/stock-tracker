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

import static fr.cph.stock.util.Constants.CURRENCY;
import static fr.cph.stock.util.Constants.LIQUIDITY;

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

	/**
	 * 
	 * @return the id
	 */
	public final int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id
	 */
	public final void setId(final int id) {
		this.id = id;
	}

	/**
	 * @return the user id
	 */
	public final int getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            the user id
	 */
	public final void setUserId(final int userId) {
		this.userId = userId;
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name
	 */
	public final void setName(final String name) {
		this.name = name;
	}

	/**
	 * @return the currency
	 */
	public final Currency getCurrency() {
		return currency;
	}

	/**
	 * @param currency
	 *            the currency
	 */
	public final void setCurrency(final Currency currency) {
		this.currency = currency;
	}

	/**
	 * @return the liquidity
	 */
	public final double getLiquidity() {
		return liquidity;
	}

	/**
	 * @param liquidity
	 *            the liquidity
	 */
	public final void setLiquidity(final Double liquidity) {
		this.liquidity = liquidity;
	}

	/**
	 * @return a boolean
	 */
	public final Boolean getDel() {
		return del;
	}

	/**
	 * @param del
	 *            the boolean
	 */
	public final void setDel(final Boolean del) {
		this.del = del;
	}

	/**
	 * @return the parity
	 */
	public final Double getParity() {
		return parity;
	}

	/**
	 * @param parity
	 *            the parity
	 */
	public final void setParity(final Double parity) {
		this.parity = parity;
	}

	/**
	 * Get a JSONObject of the current object
	 * 
	 * @return a json object
	 */
	public final JSONObject getJSONObject() {
		JSONObject json = new JSONObject();
		json.accumulate("id", id);
		json.accumulate("name", name);
		json.accumulate(CURRENCY, currency.getCode());
		json.accumulate(LIQUIDITY, liquidity);
		return json;
	}

	@Override
	public final String toString() {
		return "[Account;id=" + id + "userId=" + userId + ";name=" + name + ";currency=" + currency + ";liquidity=" + liquidity
				+ "]";
	}
}
