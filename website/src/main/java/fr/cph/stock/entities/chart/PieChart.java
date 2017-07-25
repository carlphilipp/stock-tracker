/**
 * Copyright 2017 Carl-Philipp Harmant
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

	/** Equities **/
	private final Map<String, Double> equities;

	/**
	 * Constructor that will construct a chart with the given equities
	 *
	 * @param equities
	 *            the equities
	 */
	public PieChart(final Map<String, Double> equities) {
		this.equities = equities;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see IChart#generate()
	 */
	@Override
	public final void generate() {
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
			drawTemp.append("{ data : d").append(i);
			drawTemp.append(", label : '").append(key).append("'}");
			titleTemp.append("'").append(key).append("'");
			dataTemp.append("var d").append(i).append(" = [[0,").append(value).append("]];");
			i++;
		}
		setData(dataTemp.toString());
		drawTemp.append("]");
		setDraw(drawTemp.toString());
		titleTemp.append("];");
		setTitle(titleTemp.toString());
	}

	/**
	 * get equities
	 *
	 * @return the equities
	 */
	public final Map<String, Double> getEquities() {
		return equities;
	}

}
