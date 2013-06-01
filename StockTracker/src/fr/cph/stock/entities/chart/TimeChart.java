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

import fr.cph.stock.entities.Index;
import fr.cph.stock.util.Info;
import fr.cph.stock.util.Util;

public class TimeChart extends AChart {

	private Date date;
	private Map<Date, Double> shareValue;
	private Map<String, List<Index>> indexes;

	public TimeChart(Map<Date, Double> shareValue, Map<String, List<Index>> indexes, Date date) {
		this.shareValue = shareValue;
		this.indexes = indexes;
		this.date = date;
	}

	@Override
	public void generate() {
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
			dataTemp.append("\nd" + j + " = [");
			String indexName = null;
			if (indexesTemp.get(0).getYahooId().equals(Info.YAHOOID_CAC40)) {
				indexName = "CAC 40";
			} else if (indexesTemp.get(0).getYahooId().equals(Info.YAHOOID_SP500)) {
				indexName = "S&P 500";
			}
			drawTemp.append("{data: d" + j + ", label: '" + indexName + "'}");
			int k = 0;
			for (Index indexTemp : indexesTemp) {
				if (k != 0) {
					dataTemp.append(",");
				}
				dataTemp.append("[" + indexTemp.getDate() .getTime() + "," + indexTemp.getShareValue() + "]");
				k++;
			}
			dataTemp.append("]");
			j++;
		}
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
		colorsListRes = Util.getRandomColors(3, colorsList, colorsListRes);
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
