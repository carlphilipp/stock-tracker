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

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import static fr.cph.stock.util.Constants.ACCOUNT;

/**
 * This class represents an share value
 *
 * @author Carl-Philipp Harmant
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ShareValue {

	private Account account;
	private int id;
	private int userId;
	private Date date;
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

	/**
	 * Get the jsonObject view of this class
	 *
	 * @return the jsonObject
	 */
	final JsonObject getJSONObject() {
		final JsonObject json = new JsonObject();
		json.addProperty("date", date.toString());
		json.addProperty(ACCOUNT, account.getName());
		json.addProperty("commentary", commentary);
		json.addProperty("shareValue", shareValue);
		json.addProperty("portfolioValue", portfolioValue);
		json.addProperty("shareQuantity", shareQuantity);
		json.addProperty("monthlyYield", monthlyYield);
		return json;
	}

	/**
	 * Get liquidites
	 *
	 * @return the liquidites
	 */
	final Double getLiquidities() {
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
}
