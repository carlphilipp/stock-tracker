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

import java.util.Date;

import net.sf.json.JSONObject;

/**
 * This class represents an share value
 * 
 * @author Carl-Philipp Harmant
 * 
 */
public class ShareValue {

	/** Account **/
	private Account account;
	/** Id **/
	private int id;
	/** User id **/
	private int userId;
	/** Date **/
	private Date date;
	/** Liquidity Movement **/
	private Double liquidityMovement;
	/** Yield **/
	private Double yield;
	/** Buy **/
	private Double buy;
	/** Sell **/
	private Double sell;
	/** Taxe **/
	private Double taxe;
	/** Portfolio value **/
	private Double portfolioValue;
	/** Share quantity **/
	private Double shareQuantity;
	/** Share value **/
	private Double shareValue;
	/** Monthly yield **/
	private Double monthlyYield;
	/** Commentary **/
	private String commentary;
	/** Details **/
	private String details;

	/**
	 * Get the account
	 * 
	 * @return the account
	 */
	public Account getAccount() {
		return account;
	}

	/**
	 * Get the amount bought
	 * 
	 * @return the amount buy
	 */
	public Double getBuy() {
		return buy;
	}

	/**
	 * Get commentary
	 * 
	 * @return the commentary
	 */
	public String getCommentary() {
		return commentary;
	}

	/**
	 * Get date
	 * 
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Get details
	 * 
	 * @return the details
	 */
	public String getDetails() {
		return details;
	}

	/**
	 * Get id
	 * 
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Get the jsonObject view of this class
	 * 
	 * @return the jsonObject
	 */
	public JSONObject getJSONObject() {
		JSONObject json = new JSONObject();
		json.put("date", date);
		json.put("account", account.getName());
		json.put("commentary", commentary);
		json.put("shareValue", shareValue);
		json.put("portfolioValue", portfolioValue);
		json.put("shareQuantity", shareQuantity);
		json.put("monthlyYield", monthlyYield);
		return json;
	}

	/**
	 * Get liquidites
	 * 
	 * @return the liquidites
	 */
	public Double getLiquidities() {
		String details = getDetails();
		String updated = null;
		if (details != null) {
			int begin = details.indexOf("<tr><td colspan=3><b>Liquidity:</b> ");
			details = details.substring(begin);
			String pattern = "<tr><td colspan=3><b>Liquidity:</b> (\\-?[0-9]+\\.?[0-9]*) \\(.*";
			updated = details.replaceAll(pattern, "$1");
		}
		return updated == null ? null : Double.valueOf(updated);
	}

	/**
	 * Get liquidity movement
	 * 
	 * @return the liquidity movement
	 */
	public Double getLiquidityMovement() {
		return liquidityMovement;
	}

	/**
	 * Get monthly yield
	 * 
	 * @return the monthly yield value
	 */
	public Double getMonthlyYield() {
		return monthlyYield;
	}

	/**
	 * Get portfolio value
	 * 
	 * @return the portfolio value
	 */
	public Double getPortfolioValue() {
		return portfolioValue;
	}

	/**
	 * Get the amount sold
	 * 
	 * @return the amount sold
	 */
	public Double getSell() {
		return sell;
	}

	/**
	 * Get share quantity
	 * 
	 * @return the share quantity
	 */
	public Double getShareQuantity() {
		return shareQuantity;
	}

	/**
	 * Get share value
	 * 
	 * @return the share value
	 */
	public Double getShareValue() {
		return shareValue;
	}

	/**
	 * Get the amount of taxe
	 * 
	 * @return the amount of taxe
	 */
	public Double getTaxe() {
		return taxe;
	}

	/**
	 * Get user id
	 * 
	 * @return the user id
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * Get the received yield
	 * 
	 * @return the received yield
	 */
	public Double getYield() {
		return yield;
	}

	/**
	 * Set account
	 * 
	 * @param account
	 *            the account
	 */
	public void setAccount(Account account) {
		this.account = account;
	}

	/**
	 * Set the amount bought
	 * 
	 * @param buy
	 *            the amount bought
	 */
	public void setBuy(Double buy) {
		this.buy = buy;
	}

	/**
	 * Set commentary
	 * 
	 * @param commentary
	 *            the commentary
	 */
	public void setCommentary(String commentary) {
		this.commentary = commentary;
	}

	/**
	 * The date
	 * 
	 * @param date
	 *            the date
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * The details
	 * 
	 * @param details
	 *            the details
	 */
	public void setDetails(String details) {
		this.details = details;
	}

	/**
	 * Set the id
	 * 
	 * @param id
	 *            the id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Set the liquidity movement
	 * 
	 * @param liquidityMovement
	 *            the liquidity movement
	 */
	public void setLiquidityMovement(Double liquidityMovement) {
		this.liquidityMovement = liquidityMovement;
	}

	/**
	 * Set the monthly yield
	 * 
	 * @param monthlyYield
	 *            the monthly yield
	 */
	public void setMonthlyYield(Double monthlyYield) {
		this.monthlyYield = monthlyYield;
	}

	/**
	 * Set the portfolio value
	 * 
	 * @param portfolioValue
	 *            the portfolio value
	 */
	public void setPortfolioValue(Double portfolioValue) {
		this.portfolioValue = portfolioValue;
	}

	/**
	 * Set the amount sold
	 * 
	 * @param sell
	 *            the amount sold
	 */
	public void setSell(Double sell) {
		this.sell = sell;
	}

	/**
	 * Set the share quantity
	 * 
	 * @param shareQuantity
	 *            the share quantity
	 */
	public void setShareQuantity(Double shareQuantity) {
		this.shareQuantity = shareQuantity;
	}

	/**
	 * Set the share value
	 * 
	 * @param shareValue
	 *            the share value
	 */
	public void setShareValue(Double shareValue) {
		this.shareValue = shareValue;
	}

	/**
	 * Set the amount of taxes
	 * 
	 * @param taxe
	 *            the amount of taxes
	 */
	public void setTaxe(Double taxe) {
		this.taxe = taxe;
	}

	/**
	 * Set the user id
	 * 
	 * @param userId
	 *            the user id
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}

	/**
	 * Set the yield received
	 * 
	 * @param yield
	 *            the yield received
	 */
	public void setYield(Double yield) {
		this.yield = yield;
	}

}
