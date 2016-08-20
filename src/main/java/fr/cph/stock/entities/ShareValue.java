/**
 * Copyright 2016 Carl-Philipp Harmant
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

package fr.cph.stock.entities;

import net.sf.json.JSONObject;

import java.util.Date;

import static fr.cph.stock.util.Constants.ACCOUNT;

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
	public final Account getAccount() {
		return account;
	}

	/**
	 * Get the amount bought
	 *
	 * @return the amount buy
	 */
	public final Double getBuy() {
		return buy;
	}

	/**
	 * Get commentary
	 *
	 * @return the commentary
	 */
	public final String getCommentary() {
		return commentary;
	}

	/**
	 * Get date
	 *
	 * @return the date
	 */
	public final Date getDate() {
		if (date != null) {
			return (Date) date.clone();
		} else {
			return null;
		}
	}

	/**
	 * Get details
	 *
	 * @return the details
	 */
	public final String getDetails() {
		return details;
	}

	/**
	 * Get id
	 *
	 * @return the id
	 */
	public final int getId() {
		return id;
	}

	/**
	 * Get the jsonObject view of this class
	 *
	 * @return the jsonObject
	 */
	public final JSONObject getJSONObject() {
		JSONObject json = new JSONObject();
		json.put("date", date);
		json.put(ACCOUNT, account.getName());
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
	public final Double getLiquidities() {
		String dets = getDetails();
		String updated = null;
		if (dets != null) {
			int begin = dets.indexOf("<tr><td colspan=3><b>Liquidity:</b> ");
			dets = dets.substring(begin);
			String pattern = "<tr><td colspan=3><b>Liquidity:</b> (\\-?[0-9]+\\.?[0-9]*) \\(.*";
			updated = dets.replaceAll(pattern, "$1");
		}
		return updated == null ? null : Double.valueOf(updated);
	}

	/**
	 * Get liquidity movement
	 *
	 * @return the liquidity movement
	 */
	public final Double getLiquidityMovement() {
		return liquidityMovement;
	}

	/**
	 * Get monthly yield
	 *
	 * @return the monthly yield value
	 */
	public final Double getMonthlyYield() {
		return monthlyYield;
	}

	/**
	 * Get portfolio value
	 *
	 * @return the portfolio value
	 */
	public final Double getPortfolioValue() {
		return portfolioValue;
	}

	/**
	 * Get the amount sold
	 *
	 * @return the amount sold
	 */
	public final Double getSell() {
		return sell;
	}

	/**
	 * Get share quantity
	 *
	 * @return the share quantity
	 */
	public final Double getShareQuantity() {
		return shareQuantity;
	}

	/**
	 * Get share value
	 *
	 * @return the share value
	 */
	public final Double getShareValue() {
		return shareValue;
	}

	/**
	 * Get the amount of taxe
	 *
	 * @return the amount of taxe
	 */
	public final Double getTaxe() {
		return taxe;
	}

	/**
	 * Get user id
	 *
	 * @return the user id
	 */
	public final int getUserId() {
		return userId;
	}

	/**
	 * Get the received yield
	 *
	 * @return the received yield
	 */
	public final Double getYield() {
		return yield;
	}

	/**
	 * Set account
	 *
	 * @param account
	 *            the account
	 */
	public final void setAccount(final Account account) {
		this.account = account;
	}

	/**
	 * Set the amount bought
	 *
	 * @param buy
	 *            the amount bought
	 */
	public final void setBuy(final Double buy) {
		this.buy = buy;
	}

	/**
	 * Set commentary
	 *
	 * @param commentary
	 *            the commentary
	 */
	public final void setCommentary(final String commentary) {
		this.commentary = commentary;
	}

	/**
	 * The date
	 *
	 * @param date
	 *            the date
	 */
	public final void setDate(final Date date) {
		this.date = (Date) date.clone();
	}

	/**
	 * The details
	 *
	 * @param details
	 *            the details
	 */
	public final void setDetails(final String details) {
		this.details = details;
	}

	/**
	 * Set the id
	 *
	 * @param id
	 *            the id
	 */
	public final void setId(final int id) {
		this.id = id;
	}

	/**
	 * Set the liquidity movement
	 *
	 * @param liquidityMovement
	 *            the liquidity movement
	 */
	public final void setLiquidityMovement(final Double liquidityMovement) {
		this.liquidityMovement = liquidityMovement;
	}

	/**
	 * Set the monthly yield
	 *
	 * @param monthlyYield
	 *            the monthly yield
	 */
	public final void setMonthlyYield(final Double monthlyYield) {
		this.monthlyYield = monthlyYield;
	}

	/**
	 * Set the portfolio value
	 *
	 * @param portfolioValue
	 *            the portfolio value
	 */
	public final void setPortfolioValue(final Double portfolioValue) {
		this.portfolioValue = portfolioValue;
	}

	/**
	 * Set the amount sold
	 *
	 * @param sell
	 *            the amount sold
	 */
	public final void setSell(final Double sell) {
		this.sell = sell;
	}

	/**
	 * Set the share quantity
	 *
	 * @param shareQuantity
	 *            the share quantity
	 */
	public final void setShareQuantity(final Double shareQuantity) {
		this.shareQuantity = shareQuantity;
	}

	/**
	 * Set the share value
	 *
	 * @param shareValue
	 *            the share value
	 */
	public final void setShareValue(final Double shareValue) {
		this.shareValue = shareValue;
	}

	/**
	 * Set the amount of taxes
	 *
	 * @param taxe
	 *            the amount of taxes
	 */
	public final void setTaxe(final Double taxe) {
		this.taxe = taxe;
	}

	/**
	 * Set the user id
	 *
	 * @param userId
	 *            the user id
	 */
	public final void setUserId(final int userId) {
		this.userId = userId;
	}

	/**
	 * Set the yield received
	 *
	 * @param yield
	 *            the yield received
	 */
	public final void setYield(final Double yield) {
		this.yield = yield;
	}
}
