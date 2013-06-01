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

public class ShareValue {

	private Account account;

	private int id;
	private int userId;
	private Date date;

	// private String accountName;
	private Double liquidityMovement;
	private Double yield;
	private Double buy;
	private Double sell;
	private Double taxe;
	private Double portfolioValue;
	private Double shareQuantity;
	private Double shareValue;
	private Double monthlyYield;
	private String commentary;
	private String details;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Double getPortfolioValue() {
		return portfolioValue;
	}

	public void setPortfolioValue(Double portfolioValue) {
		this.portfolioValue = portfolioValue;
	}

	public Double getShareQuantity() {
		return shareQuantity;
	}

	public void setShareQuantity(Double shareQuantity) {
		this.shareQuantity = shareQuantity;
	}

	public Double getShareValue() {
		return shareValue;
	}

	public void setShareValue(Double shareValue) {
		this.shareValue = shareValue;
	}

	public Double getMonthlyYield() {
		return monthlyYield;
	}

	public void setMonthlyYield(Double monthlyYield) {
		this.monthlyYield = monthlyYield;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public Double getLiquidityMovement() {
		return liquidityMovement;
	}

	public void setLiquidityMovement(Double liquidityMovement) {
		this.liquidityMovement = liquidityMovement;
	}

	public Double getYield() {
		return yield;
	}

	public void setYield(Double yield) {
		this.yield = yield;
	}

	public Double getBuy() {
		return buy;
	}

	public void setBuy(Double buy) {
		this.buy = buy;
	}

	public Double getSell() {
		return sell;
	}

	public void setSell(Double sell) {
		this.sell = sell;
	}

	public String getCommentary() {
		return commentary;
	}

	public void setCommentary(String commentary) {
		this.commentary = commentary;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public Double getTaxe() {
		return taxe;
	}

	public void setTaxe(Double taxe) {
		this.taxe = taxe;
	}

	// public String getAccountName() {
	// return accountName;
	// }
	//
	// public void setAccountName(String accountName) {
	// this.accountName = accountName;
	// }

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

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

}
