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

public class Index {

	private int id;
	private String yahooId;
	private Double value;
	private Date date;
	private Double shareValue;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getYahooId() {
		return yahooId;
	}

	public void setYahooId(String yahooId) {
		this.yahooId = yahooId;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Double getShareValue() {
		return shareValue;
	}

	public void setShareValue(Double shareValue) {
		this.shareValue = shareValue;
	}
	
	public String toString(){
		return "Index value : " + shareValue;
	}

}
