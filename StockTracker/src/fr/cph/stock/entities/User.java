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
import java.util.Locale;

import net.sf.json.JSONObject;

/**
 * This class represents a user
 * 
 * @author Carl-Philipp Harmant
 * 
 */
public class User {

	/** Id **/
	private int id;
	/** Login **/
	private String login;
	/** Password **/
	private String password;
	/** Email **/
	private String email;
	/** Locale **/
	private String locale;
	/** Timezone **/
	private String timeZone;
	/** Time of update **/
	private Integer updateHourTime;
	/** Send mail if fail at updating ? **/
	private Boolean updateSendMail;
	/** Date pattern **/
	private String datePattern;
	/** Date pattern without hours and minutes **/
	private String datePatternWithoutHourMin;
	/** Allow to login **/
	private Boolean allow;
	/** Last update **/
	private Date lastUpdate;

	/**
	 * Constructor
	 */
	public User() {
	}

	/**
	 * Constructor
	 * 
	 * @param login
	 *            the login
	 * @param password
	 *            the password
	 */
	public User(String login, String password) {
		this.login = login;
		this.password = password;
	}

	/**
	 * Get the id
	 * 
	 * @return the id
	 */
	public int getId() {
		return id;
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
	 * Get the login
	 * 
	 * @return the login
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * Set the login
	 * 
	 * @param login
	 *            the login
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * Get the password
	 * 
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Set password
	 * 
	 * @param password
	 *            the password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Get email
	 * 
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Set email
	 * 
	 * @param email
	 *            the email
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Get locale
	 * 
	 * @return the locale
	 */
	public String getLocale() {
		return locale;
	}

	/**
	 * Set locale
	 * 
	 * @param locale
	 *            the locale
	 */
	public void setLocale(Locale locale) {
		this.locale = locale.toString();
	}

	/**
	 * Set locale with a string
	 * 
	 * @param locale
	 *            the locale
	 */
	public void setLocale(String locale) {
		this.locale = locale;
	}

	/**
	 * Get last updapte
	 * 
	 * @return the last update
	 */
	public Date getLastUpdate() {
		return lastUpdate;
	}

	/**
	 * Set last update
	 * 
	 * @param lastUpdate
	 *            the last update
	 */
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	/**
	 * Get timezone
	 * 
	 * @return the timezone
	 */
	public String getTimeZone() {
		return timeZone;
	}

	/**
	 * Set timezone
	 * 
	 * @param timeZone
	 *            the timezone
	 */
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	/**
	 * Get date pattern
	 * 
	 * @return the date pattern
	 */
	public String getDatePattern() {
		return datePattern;
	}

	/**
	 * Set date pattern
	 * 
	 * @param datePattern
	 *            the date pattern
	 */
	public void setDatePattern(String datePattern) {
		this.datePattern = datePattern;
		setDatePatternWithoutHourMin(datePattern.substring(0, datePattern.indexOf(' ')));
	}

	/**
	 * Get date pattern without hour and min
	 * 
	 * @return the date pattern
	 */
	public String getDatePatternWithoutHourMin() {
		return datePatternWithoutHourMin;
	}

	/**
	 * Set date pattern without hour min
	 * 
	 * @param datePatternWithoutHourMin
	 *            the date pattern
	 */
	public void setDatePatternWithoutHourMin(String datePatternWithoutHourMin) {
		this.datePatternWithoutHourMin = datePatternWithoutHourMin;
	}

	/**
	 * Get allow
	 * 
	 * @return if the user is allowed or not to enter the website
	 */
	public Boolean getAllow() {
		return allow;
	}

	/**
	 * Set allow
	 * 
	 * @param allow
	 *            the boolean that will tell if the user is allowed to enter the website
	 */
	public void setAllow(Boolean allow) {
		this.allow = allow;
	}

	/**
	 * Get update hour time
	 * 
	 * @return the hour time
	 */
	public Integer getUpdateHourTime() {
		return updateHourTime;
	}

	/**
	 * Set update hour time
	 * 
	 * @param updateHourTime
	 *            the hour time
	 */
	public void setUpdateHourTime(Integer updateHourTime) {
		this.updateHourTime = updateHourTime;
	}

	/**
	 * Get if an email must be send when update did not work
	 * 
	 * @return true or false
	 */
	public Boolean getUpdateSendMail() {
		return updateSendMail;
	}

	/**
	 * Set if an email must be send when update did not work
	 * 
	 * @param updateSendMail
	 *            true or false
	 */
	public void setUpdateSendMail(Boolean updateSendMail) {
		this.updateSendMail = updateSendMail;
	}
	
	public String getLanguage(){
		return this.locale.substring(0, this.locale.indexOf('_'));
	}
	
	public String getCountry(){
		return this.locale.substring(this.locale.indexOf('_') + 1, this.locale.length());
	}

	/**
	 * Get JSONObject view of the current user
	 * 
	 * @return a JSONObject
	 */
	public JSONObject getJSONObject() {
		JSONObject json = new JSONObject();
		json.put("id", getId());
		json.put("login", getLogin());
		json.put("password", getPassword());
		json.put("email", getEmail());
		json.put("locale", getLocale());
		json.put("timeZone", getTimeZone());
		json.put("updateHourTime", getUpdateHourTime());
		json.put("updateSendMail", getUpdateSendMail());
		json.put("datePattern", getDatePattern());
		json.put("datePatternWithoutHourMin", getDatePatternWithoutHourMin());
		json.put("allow", getAllow());
		json.put("lastUpdate", getLastUpdate());
		return json;
	}

}
