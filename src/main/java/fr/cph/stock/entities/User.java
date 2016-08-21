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

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;

import static fr.cph.stock.util.Constants.LOGIN;
import static fr.cph.stock.util.Constants.PASSWORD;

/**
 * This class represents a user
 *
 * @author Carl-Philipp Harmant
 */
public class User implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -7736017495914032958L;

	/**
	 * Id
	 **/
	private int id;
	/**
	 * Login
	 **/
	private String login;
	/**
	 * Password
	 **/
	private String password;
	/**
	 * Email
	 **/
	private String email;
	/**
	 * Locale
	 **/
	private String locale;
	/**
	 * Timezone
	 **/
	private String timeZone;
	/**
	 * Time of update
	 **/
	private Integer updateHourTime;
	/**
	 * Send mail if fail at updating ?
	 **/
	private Boolean updateSendMail;
	/**
	 * Date pattern
	 **/
	private String datePattern;
	/**
	 * Date pattern without hours and minutes
	 **/
	private String datePatternWithoutHourMin;
	/**
	 * Allow to login
	 **/
	private Boolean allow;
	/**
	 * Last update
	 **/
	private Date lastUpdate;

	/**
	 * Constructor
	 */
	public User() {
	}

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

	/**
	 * Get the id
	 *
	 * @return the id
	 */
	public final int getId() {
		return id;
	}

	/**
	 * Set the id
	 *
	 * @param id the id
	 */
	public final void setId(final int id) {
		this.id = id;
	}

	/**
	 * Get the login
	 *
	 * @return the login
	 */
	public final String getLogin() {
		return login;
	}

	/**
	 * Set the login
	 *
	 * @param login the login
	 */
	public final void setLogin(final String login) {
		this.login = login;
	}

	/**
	 * Get the password
	 *
	 * @return the password
	 */
	public final String getPassword() {
		return password;
	}

	/**
	 * Set password
	 *
	 * @param password the password
	 */
	public final void setPassword(final String password) {
		this.password = password;
	}

	/**
	 * Get email
	 *
	 * @return the email
	 */
	public final String getEmail() {
		return email;
	}

	/**
	 * Set email
	 *
	 * @param email the email
	 */
	public final void setEmail(final String email) {
		this.email = email;
	}

	/**
	 * Get locale
	 *
	 * @return the locale
	 */
	public final String getLocale() {
		return locale;
	}

	/**
	 * Set locale
	 *
	 * @param locale the locale
	 */
	public final void setLocale(final Locale locale) {
		this.locale = locale.toString();
	}

	/**
	 * Set locale with a string
	 *
	 * @param locale the locale
	 */
	public final void setLocale(final String locale) {
		this.locale = locale;
	}

	/**
	 * Get last updapte
	 *
	 * @return the last update
	 */
	public final Date getLastUpdate() {
		if (lastUpdate != null) {
			return (Date) lastUpdate.clone();
		} else {
			return null;
		}
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
	 * Get timezone
	 *
	 * @return the timezone
	 */
	public final String getTimeZone() {
		return timeZone;
	}

	/**
	 * Set timezone
	 *
	 * @param timeZone the timezone
	 */
	public final void setTimeZone(final String timeZone) {
		this.timeZone = timeZone;
	}

	/**
	 * Get date pattern
	 *
	 * @return the date pattern
	 */
	public final String getDatePattern() {
		return datePattern;
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
	 * Get date pattern without hour and min
	 *
	 * @return the date pattern
	 */
	public final String getDatePatternWithoutHourMin() {
		return datePatternWithoutHourMin;
	}

	/**
	 * Set date pattern without hour min
	 *
	 * @param datePatternWithoutHourMin the date pattern
	 */
	public final void setDatePatternWithoutHourMin(final String datePatternWithoutHourMin) {
		this.datePatternWithoutHourMin = datePatternWithoutHourMin;
	}

	/**
	 * Get allow
	 *
	 * @return if the user is allowed or not to enter the website
	 */
	public final Boolean getAllow() {
		return allow;
	}

	/**
	 * Set allow
	 *
	 * @param allow the boolean that will tell if the user is allowed to enter the website
	 */
	public final void setAllow(final Boolean allow) {
		this.allow = allow;
	}

	/**
	 * Get update hour time
	 *
	 * @return the hour time
	 */
	public final Integer getUpdateHourTime() {
		return updateHourTime;
	}

	/**
	 * Set update hour time
	 *
	 * @param updateHourTime the hour time
	 */
	public final void setUpdateHourTime(final Integer updateHourTime) {
		this.updateHourTime = updateHourTime;
	}

	/**
	 * Get if an email must be send when update did not work
	 *
	 * @return true or false
	 */
	public final Boolean getUpdateSendMail() {
		return updateSendMail;
	}

	/**
	 * Set if an email must be send when update did not work
	 *
	 * @param updateSendMail true or false
	 */
	public final void setUpdateSendMail(final Boolean updateSendMail) {
		this.updateSendMail = updateSendMail;
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
		JsonObject json = new JsonObject();
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
