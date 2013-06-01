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

public class Follow {

	private Company company;

	private int id;
	private int companyId;
	private int userId;

	private Double lowerLimit;
	private Double higherLimit;

	// calcuated
	private Double gapLowerLimit;
	private Double gapHigherLimit;
	
	public String toString(){
		return "id: " + id + " - companyId " + companyId + " - userId " + userId + " - lowerLimit " +lowerLimit + " - higherLimit " + higherLimit;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public Double getLowerLimit() {
		return lowerLimit;
	}

	public void setLowerLimit(Double lowerLimit) {
		this.lowerLimit = lowerLimit;
	}

	public Double getHigherLimit() {
		return higherLimit;
	}

	public void setHigherLimit(Double higherLimit) {
		this.higherLimit = higherLimit;
	}

	public Double getGapLowerLimit() {
		if (gapLowerLimit == null) {
			gapLowerLimit = (company.getQuote() / getLowerLimit() - 1) * 100;
		}
		return gapLowerLimit;
	}

	public void setGapLowerLimit(Double gapLowerLimit) {
		this.gapLowerLimit = gapLowerLimit;
	}

	public Double getGapHigherLimit() {
		if (gapHigherLimit == null) {
			gapHigherLimit = (getHigherLimit() / company.getQuote() - 1) * 100;
		}
		return gapHigherLimit;
	}

	public void setGapHigherLimit(Double gapHigherLimit) {
		this.gapHigherLimit = gapHigherLimit;
	}

}
