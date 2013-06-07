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

package fr.cph.stock.android.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import fr.cph.stock.android.R;
import fr.cph.stock.android.entity.Equity;

public class EquityAdapter extends BaseAdapter {

	private List<Equity> data;
	private Context context;

	public EquityAdapter(List<Equity> data, Context rootView) {
		this.data = data;
		this.context = rootView;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.list_item_equity, null);
		}
		TextView nameView = (TextView) v.findViewById(R.id.name);
		TextView unitCostPriceView = (TextView) v.findViewById(R.id.unitCostPriceValue);
		TextView valueView = (TextView) v.findViewById(R.id.value);
		TextView plusMinusValueView = (TextView) v.findViewById(R.id.plusMinusValue);
		TextView quantityView = (TextView) v.findViewById(R.id.quantityValue);
		TextView yieldYearView = (TextView) v.findViewById(R.id.yieldYear);
		TextView quoteView = (TextView) v.findViewById(R.id.quoteValue);
		TextView gainView = (TextView) v.findViewById(R.id.gain);
		TextView todayView = (TextView) v.findViewById(R.id.today);

		final Equity equity = data.get(position);
		nameView.setText(equity.getName());
		unitCostPriceView.setText(equity.getUnitCostPrice());
		valueView.setText(equity.getValue());
		plusMinusValueView.setText(equity.getPlusMinusValue());
		if (equity.isUp()) {
			plusMinusValueView.setTextColor(Color.rgb(0, 160, 0));
		} else {
			plusMinusValueView.setTextColor(Color.rgb(160, 0, 0));
		}
		quantityView.setText(equity.getQuantity());
		yieldYearView.setText(equity.getYieldUnitCostPrice());
		quoteView.setText(equity.getQuote());
		gainView.setText(equity.getPlusMinusUnitCostPriceValue());
		todayView.setText(equity.getVariation());
		if (equity.isUpVariation()) {
			todayView.setTextColor(Color.rgb(0, 160, 0));
		} else {
			todayView.setTextColor(Color.rgb(160, 0, 0));
		}
		return v;
	}
}
