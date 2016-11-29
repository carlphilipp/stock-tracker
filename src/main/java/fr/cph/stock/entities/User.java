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

import java.io.Serializable;
import java.util.Date;

import static fr.cph.stock.util.Constants.LOGIN;
import static fr.cph.stock.util.Constants.PASSWORD;

/**
 * This class represents a user
 *
 * @author Carl-Philipp Harmant
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User implements Serializable {

	private static final long serialVersionUID = -7736017495914032958L;

	private int id;
	private String login;
	private String password;
	private String email;
	private String locale;
	private String timeZone;
	private Integer updateHourTime;
	private Boolean updateSendMail;
	private String datePattern;
	private String datePatternWithoutHourMin;
	private Boolean allow;
	private Date lastUpdate;

	/**
	 * Constructor
	 *
	 * @param login    the login
	 * @param password the password
	 */
	public User(final String login, final String password) {
		this.login = login;
		this.password = password;
	}

	public final void setLocale(final String locale) {
		this.locale = locale;
	}

	/**
	 * Get last updapte
	 *
	 * @return the last update
	 */
	public final Date getLastUpdate() {
		return lastUpdate != null ? (Date) lastUpdate.clone() : null;
	}

	/**
	 * Set last update
	 *
	 * @param lastUpdate the last update
	 */
	public final void setLastUpdate(final Date lastUpdate) {
		this.lastUpdate = (Date) lastUpdate.clone();
	}


	/**
	 * Set date pattern
	 *
	 * @param datePattern the date pattern
	 */
	public final void setDatePattern(final String datePattern) {
		this.datePattern = datePattern;
		setDatePatternWithoutHourMin(datePattern.substring(0, datePattern.indexOf(' ')));
	}

	/**
	 * Getter
	 *
	 * @return a language
	 */
	public final String getLanguage() {
		return this.locale.substring(0, this.locale.indexOf('_'));
	}

	/**
	 * Getter
	 *
	 * @return a country
	 */
	public final String getCountry() {
		return this.locale.substring(this.locale.indexOf('_') + 1, this.locale.length());
	}

	/**
	 * Get JSONObject view of the current user
	 *
	 * @return a JSONObject
	 */
	public final JsonObject getJSONObject() {
		final JsonObject json = new JsonObject();
		json.addProperty("id", getId());
		json.addProperty(LOGIN, getLogin());
		json.addProperty(PASSWORD, getPassword());
		json.addProperty("email", getEmail());
		json.addProperty("locale", getLocale());
		json.addProperty("timeZone", getTimeZone());
		json.addProperty("updateHourTime", getUpdateHourTime());
		json.addProperty("updateSendMail", getUpdateSendMail());
		json.addProperty("datePattern", getDatePattern());
		json.addProperty("datePatternWithoutHourMin", getDatePatternWithoutHourMin());
		json.addProperty("allow", getAllow());
		json.addProperty("lastUpdate", getLastUpdate() != null ? getLastUpdate().toString() : "");
		return json;
	}
}
