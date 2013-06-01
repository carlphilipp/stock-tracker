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

package fr.cph.stock.entities.chart;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import fr.cph.stock.util.Util;

public class TimeValueChart extends AChart {

	private Date date;
	private Map<Date, Double> portfolioValue;
	private Map<Date, Double> liquidity;

	public TimeValueChart(Map<Date, Double> portfolioValue, Map<Date, Double> liquidity, Date date) {
		this.portfolioValue = portfolioValue;
		this.liquidity = liquidity;
		this.date = date;
	}

	@Override
	public void generate() {
		StringBuilder dataTemp = new StringBuilder();
		StringBuilder drawTemp = new StringBuilder();
		dataTemp.append("d1 = [");
		drawTemp.append("[{data: d1, label: 'My portfolio value'}");
		int i = 1;
		for (Entry<Date, Double> e : portfolioValue.entrySet()) {
			if (i != 1) {
				dataTemp.append(",");
			}
			dataTemp.append("[" + e.getKey().getTime() + "," + e.getValue() + "]");
			i++;
		}
		dataTemp.append("]");
		dataTemp.append(",");
		dataTemp.append("\nd2 = [");
		int j = 1;
		for (Entry<Date, Double> e : liquidity.entrySet()) {
			if (j != 1) {
				dataTemp.append(",");
			}
			dataTemp.append("[" + e.getKey().getTime() + "," + e.getValue() + "]");
			j++;
		}
		dataTemp.append("]");
		drawTemp.append(",");
		drawTemp.append("{data: d2, label: 'My liquidities'}");
		drawTemp.append("]");
		data = dataTemp.toString();
		draw = drawTemp.toString();
		generateColors();
	}
	
	private void generateColors() {
		List<String> colorsListRes = new ArrayList<String>();
		List<String> colorsList = new ArrayList<String>();
		colorsList.add("#3e933d");
		colorsList.add("#190525");
		colorsList.add("#6a0efc");
		colorsList.add("#FF0000");
		colorsList.add("#FFCC00");
		colorsListRes = Util.getRandomColors(2, colorsList, colorsListRes);
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (String s : colorsListRes) {
			sb.append("'" + s + "'");
			if (i != colorsList.size()) {
				sb.append(",");
			}
		}
		colors = sb.toString();
	}

	public Date getDate() {
		return date;
	}

}
