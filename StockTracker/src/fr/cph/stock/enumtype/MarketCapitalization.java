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

package fr.cph.stock.enumtype;

public enum MarketCapitalization {
	NANO_CAP("Nano-Cap"), MICRO_CAP("Micro-Cap"), SMALL_CAP("Small-Cap"), MID_CAP("Mid-Cap"), 
	LARGE_CAP("Large-Cap"), MEGA_CAP("Mega-Cap"), FOND_TRACKERS("Fonds/Tracker"),
	UNKNOWN("Unknown");
	
	private String market;
	private MarketCapitalization(String marketCap) {
		this.market = marketCap;
	}
	public String getValue(){
		return market;
	}
}
