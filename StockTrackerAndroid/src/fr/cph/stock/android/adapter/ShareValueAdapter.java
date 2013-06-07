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
import fr.cph.stock.android.entity.ShareValue;

public class ShareValueAdapter extends BaseAdapter{
	
	private List<ShareValue> sharesValues;
	private Context context;
	
	public ShareValueAdapter(List<ShareValue> sharesValues, Context context){
		this.sharesValues =  sharesValues;
		this.context = context;
	}

	@Override
	public int getCount() {
		return sharesValues.size();
	}

	@Override
	public Object getItem(int position) {
		return sharesValues.get(position);
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
			v = vi.inflate(R.layout.list_item_share_value, null);
		}
		
		TextView dateView = (TextView) v.findViewById(R.id.date);
		TextView shareValueView = (TextView) v.findViewById(R.id.shareValue);
		shareValueView.setTextColor(Color.rgb(0, 160, 0));
		TextView commentaryView = (TextView) v.findViewById(R.id.commentary);
		TextView accountView = (TextView) v.findViewById(R.id.account);
		TextView portfolioValueView = (TextView) v.findViewById(R.id.portfolioValue);
		TextView shareQuantityView = (TextView) v.findViewById(R.id.shareQuantity);
		TextView monthlyYieldView = (TextView) v.findViewById(R.id.monthlyYield2);
		

		ShareValue shareValue = sharesValues.get(position);
		dateView.setText(shareValue.getDate());
		shareValueView.setText(shareValue.getShareValue());
		commentaryView.setText(shareValue.getCommentary());
		accountView.setText(shareValue.getAccount());	
		portfolioValueView.setText(shareValue.getPortfolioValue());
		shareQuantityView.setText(shareValue.getShareQuantity());
		monthlyYieldView.setText(shareValue.getMonthlyYield());
		return v;
	}

}
