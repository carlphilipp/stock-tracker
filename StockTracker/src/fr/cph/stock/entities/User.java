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

public class User {

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

	public User() {
	}

	public User(String login, String password) {
		this.login = login;
		this.password = password;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale.toString();
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public String getDatePattern() {
		return datePattern;
	}

	public void setDatePattern(String datePattern) {
		this.datePattern = datePattern;
		setDatePatternWithoutHourMin(datePattern.substring(0, datePattern.indexOf(' ')));
	}
	
	public String getDatePatternWithoutHourMin() {
		return datePatternWithoutHourMin;
	}

	public void setDatePatternWithoutHourMin(String datePatternWithoutHourMin) {
		this.datePatternWithoutHourMin = datePatternWithoutHourMin;
	}

	public Boolean getAllow() {
		return allow;
	}

	public void setAllow(Boolean allow) {
		this.allow = allow;
	}

	public Integer getUpdateHourTime() {
		return updateHourTime;
	}

	public void setUpdateHourTime(Integer updateHourTime) {
		this.updateHourTime = updateHourTime;
	}

	public Boolean getUpdateSendMail() {
		return updateSendMail;
	}

	public void setUpdateSendMail(Boolean updateSendMail) {
		this.updateSendMail = updateSendMail;
	}
	
	public JSONObject getJSONObject(){
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
