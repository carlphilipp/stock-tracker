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

import java.util.Map;
import java.util.Map.Entry;

/**
 * This class represents an pie chart
 * 
 * @author Carl-Philipp Harmant
 * 
 */
public class PieChart extends AChart {

	/** Equties **/
	private Map<String, Double> equities;

	/**
	 * Constructor that will construct a chart with the given equities
	 * 
	 * @param equities
	 */
	public PieChart(Map<String, Double> equities) {
		this.equities = equities;
	}

	/* (non-Javadoc)
	 * @see fr.cph.stock.entities.chart.IChart#generate()
	 */
	@Override
	public void generate() {
		StringBuilder dataTemp = new StringBuilder();
		StringBuilder titleTemp = new StringBuilder();
		StringBuilder drawTemp = new StringBuilder();
		drawTemp.append("[");
		titleTemp.append("var title = [");
		int i = 1;
		for (Entry<String, Double> e : equities.entrySet()) {
			String key = e.getKey();
			Double value = e.getValue();
			if (i != 1) {
				drawTemp.append(",");
				titleTemp.append(",");
			}
			drawTemp.append("{ data : d" + i);
			drawTemp.append(", label : '" + key + "'}");
			titleTemp.append("'" + key + "'");
			dataTemp.append("var d" + i + " = [[0," + value + "]];");
			i++;
		}
		data = dataTemp.toString();
		drawTemp.append("]");
		draw = drawTemp.toString();
		titleTemp.append("];");
		title = titleTemp.toString();
	}

}
