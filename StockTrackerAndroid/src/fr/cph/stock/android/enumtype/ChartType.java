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

package fr.cph.stock.android.enumtype;

public enum ChartType {
	SHARE_VALUE("SHARE_VALUE"), SECTOR("SECTOR"), CAPITALIZATION("CAPITALIZATION");

	private ChartType(String value) {
		this.value = value;
	}

	public static ChartType getEnum(String value) {
		if (value == null) {
			throw new IllegalArgumentException();
		}
		for (ChartType c : values()) {
			if (value.equalsIgnoreCase(c.getValue())) {
				return c;
			}
		}
		throw new IllegalArgumentException();
	}

	private String value;

	public String getValue() {
		return value;
	}
}
