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
import fr.cph.stock.enumtype.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static fr.cph.stock.util.Constants.CURRENCY;
import static fr.cph.stock.util.Constants.LIQUIDITY;

/**
 * This class represents the account of the user
 *
 * @author Carl-Philipp Harmant
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {

	private int id;
	private int userId;
	private String name;
	private Currency currency;
	private Double liquidity;
	/**
	 * is it allowed to delete it from db ?
	 **/
	private Boolean del;
	private Double parity;

	/**
	 * Get a JSONObject of the current object
	 *
	 * @return a json object
	 */
	final JsonObject getJSONObject() {
		final JsonObject json = new JsonObject();
		json.addProperty("id", id);
		json.addProperty("name", name);
		json.addProperty(CURRENCY, currency.getCode());
		json.addProperty(LIQUIDITY, liquidity);
		return json;
	}
}
