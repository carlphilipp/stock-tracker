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

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import fr.cph.stock.android.R;
import fr.cph.stock.android.activity.MainActivity;
import fr.cph.stock.android.entity.Portfolio;
import fr.cph.stock.android.enumtype.ChartType;
import fr.cph.stock.android.listener.ChartListener;

public class MainListAdapter extends BaseAdapter {

	private MainActivity activity;
	private Portfolio portfolio;

	public MainListAdapter(MainActivity activity, Portfolio portfolio) {
		this.activity = activity;
		this.portfolio = portfolio;
	}

	@Override
	public boolean isEnabled(int position) {
		if (position == 3) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public int getCount() {
		return 4;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		TextView textView;
		switch (position) {
		case 0:
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.main_list_item_cell1, null);
			}
			textView = (TextView) v.findViewById(R.id.portfolio_value_main);
			textView.setText(portfolio.getTotalValue());
			textView = (TextView) v.findViewById(R.id.liquidity_value_main);
			textView.setText(portfolio.getLiquidity());

			break;
		case 1:
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.main_list_item_cell2, null);
			}
			textView = (TextView) v.findViewById(R.id.current_performance_value);
			textView.setText(portfolio.getTotalGain());
			if (portfolio.isUp()) {
				textView.setTextColor(Color.rgb(0, 160, 0));
			} else {
				textView.setTextColor(Color.rgb(160, 0, 0));
			}

			textView = (TextView) v.findViewById(R.id.today_performance_value);
			textView.setText(portfolio.getTotalVariation());
			if (portfolio.isTodayUp()) {
				textView.setTextColor(Color.rgb(0, 160, 0));
			} else {
				textView.setTextColor(Color.rgb(160, 0, 0));
			}
			//
			// textView = (TextView) v.findViewById(R.id.equity_list);
			// StringBuilder sb = new StringBuilder();
			// int i = 0;
			// for (Equity e : portfolio.getEquities()) {
			// if (i > 4) {
			// sb.append("... + " + (portfolio.getEquities().size() - 5) + " others.");
			// break;
			// } else {
			// if (i != 0) {
			// sb.append(", ");
			// }
			// }
			// sb.append(e.getName());
			// i++;
			// }
			// textView.setText(sb.toString());
			break;
		case 2:
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.main_list_item_cell3, null);
			}
			textView = (TextView) v.findViewById(R.id.performance_value);
			textView.setText(portfolio.getShareValues().get(0).getShareValue());
			if (portfolio.getShareValues().get(0).isUp()) {
				textView.setTextColor(Color.rgb(0, 160, 0));
			} else {
				textView.setTextColor(Color.rgb(160, 0, 0));
			}
			textView = (TextView) v.findViewById(R.id.last_updated_value);
			textView.setText(portfolio.getShareValues().get(0).getDate());
			break;
		case 3:
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.main_list_item_cell4, null);
			}
			ImageButton shareValueView = (ImageButton) v.findViewById(R.id.shareValueChart);
			ChartListener chartShareValueListener = new ChartListener(activity, portfolio, ChartType.SHARE_VALUE);
			shareValueView.setOnClickListener(chartShareValueListener);

			ImageView sectorChartView = (ImageView) v.findViewById(R.id.sectorChart);
			ChartListener chartSectorListener = new ChartListener(activity, portfolio, ChartType.SECTOR);
			sectorChartView.setOnClickListener(chartSectorListener);

			ImageView capChartView = (ImageView) v.findViewById(R.id.capChart);
			ChartListener chartCapListener = new ChartListener(activity, portfolio, ChartType.CAPITALIZATION);
			capChartView.setOnClickListener(chartCapListener);
			break;
		}
		return v;
	}

	public void update(Portfolio portfolio) {
		this.portfolio = portfolio;
		this.notifyDataSetChanged();
	}
}
