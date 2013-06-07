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

package fr.cph.stock.android.listener;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import fr.cph.stock.android.activity.ChartActivity;
import fr.cph.stock.android.activity.MainActivity;
import fr.cph.stock.android.entity.Portfolio;
import fr.cph.stock.android.enumtype.ChartType;

public class ChartListener implements OnClickListener{
	
	private Portfolio portfolio;
	private MainActivity activity;
	private ChartType chartType;
	
	public ChartListener(MainActivity activity, Portfolio portfolio, ChartType chartType){
		this.activity = activity;
		this.portfolio = portfolio;
		this.chartType = chartType;
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(activity.getApplicationContext(), ChartActivity.class);
		intent.putExtra("portfolio", portfolio);
		intent.putExtra("chartType", chartType.getValue());
		activity.startActivityForResult(intent, MainActivity.CHART_REQUEST);
		activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}

}
