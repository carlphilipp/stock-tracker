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

import fr.cph.stock.enumtype.Currency;

public class CurrencyData {

	private int id;
	private Currency currency1;
	private Currency currency2;
	private Double value;
	private Date lastUpdate;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public Currency getCurrency1() {
		return currency1;
	}

	public void setCurrency1(Currency currency1) {
		this.currency1 = currency1;
	}

	public Currency getCurrency2() {
		return currency2;
	}

	public void setCurrency2(Currency currency2) {
		this.currency2 = currency2;
	}
	public String toString(){
		return currency1 + " " + currency2 + " " + lastUpdate;
	}
}
