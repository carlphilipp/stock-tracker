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

import fr.cph.stock.entities.Index;
import fr.cph.stock.util.Constants;
import fr.cph.stock.util.Util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This class represents an time chart
 *
 * @author Carl-Philipp Harmant
 */
public class TimeChart extends AChart {

	/**
	 * Starting date of the chart
	 **/
	private final Date date;
	/**
	 * Share value info
	 **/
	private final Map<Date, Double> shareValue;
	/**
	 * Indexes
	 **/
	private final Map<String, List<Index>> indexes;

	/**
	 * Constructor that constructs a time chart
	 *
	 * @param shareValue the share values
	 * @param indexes    the indexes
	 * @param date       the starting date
	 */
	public TimeChart(final Map<Date, Double> shareValue, final Map<String, List<Index>> indexes, final Date date) {
		this.shareValue = shareValue;
		this.indexes = indexes;
		this.date = (Date) date.clone();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see IChart#generate()
	 */
	@Override
	public final void generate() {
		StringBuilder dataTemp = new StringBuilder();
		StringBuilder drawTemp = new StringBuilder();
		dataTemp.append("d1 = [");
		drawTemp.append("[{data: d1, label: 'My share value'}");
		int i = 1;
		for (Entry<Date, Double> e : shareValue.entrySet()) {
			if (i != 1) {
				dataTemp.append(",");
			}
			dataTemp.append("[" + e.getKey().getTime() + "," + e.getValue() + "]");
			i++;
		}
		dataTemp.append("]");
		int j = 2;
		for (Entry<String, List<Index>> e : indexes.entrySet()) {
			dataTemp.append(",");
			drawTemp.append(",");
			List<Index> indexesTemp = e.getValue();
			dataTemp.append("\nd").append(j).append(" = [");
			String indexName = null;
			if (indexesTemp.get(0).getYahooId().equals(Constants.CAC_40)) {
				indexName = "CAC 40";
			} else if (indexesTemp.get(0).getYahooId().equals(Constants.SP_500)) {
				indexName = "S&P 500";
			}
			drawTemp.append("{data: d").append(j).append(", label: '").append(indexName).append("'}");
			int k = 0;
			for (Index indexTemp : indexesTemp) {
				if (k != 0) {
					dataTemp.append(",");
				}
				dataTemp.append("[").append(indexTemp.getDate().getTime()).append(",").append(indexTemp.getShareValue()).append("]");
				k++;
			}
			dataTemp.append("]");
			j++;
		}
		drawTemp.append("]");
		setData(dataTemp.toString());
		setDraw(drawTemp.toString());
		generateColors();
	}

	/**
	 * Generate colors
	 */
	private void generateColors() {
		final List<String> colorsList = new ArrayList<>();
		colorsList.add("#3e933d");
		colorsList.add("#190525");
		colorsList.add("#6a0efc");
		colorsList.add("#FF0000");
		colorsList.add("#FFCC00");
		final List<String> colorsListRes = Util.getRandomColors(3, colorsList);
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (String s : colorsListRes) {
			sb.append("'").append(s).append("'");
			if (i != colorsList.size()) {
				sb.append(",");
			}
		}
		setColors(sb.toString());
	}

	/**
	 * Getter
	 *
	 * @return the date
	 */
	public final Date getDate() {
		if (date != null) {
			return (Date) date.clone();
		} else {
			return null;
		}
	}

	/**
	 * Getter
	 *
	 * @return the share values
	 */
	public final Map<Date, Double> getShareValue() {
		return shareValue;
	}

	/**
	 * Getter
	 *
	 * @return the map of indexes
	 */
	public final Map<String, List<Index>> getIndexes() {
		return indexes;
	}
}
