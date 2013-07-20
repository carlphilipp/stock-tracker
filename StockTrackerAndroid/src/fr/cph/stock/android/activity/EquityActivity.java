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

package fr.cph.stock.android.activity;

import java.util.List;

import org.json.JSONObject;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Tracker;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import fr.cph.stock.android.R;
import fr.cph.stock.android.StockTrackerApp;
import fr.cph.stock.android.adapter.EquityAdapter;
import fr.cph.stock.android.entity.Equity;
import fr.cph.stock.android.entity.Portfolio;
import fr.cph.stock.android.enumtype.UrlType;
import fr.cph.stock.android.task.MainTask;

public class EquityActivity extends ListActivity implements IStockTrackerActivity {

	private static final String TAG = "EquityActivity";

	private Tracker tracker;

	private EquityAdapter mAdapter;
	private List<Equity> equities;
	private TextView lastUpdatedView;
	private TextView errorView;
	private MenuItem menuItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "EquityActivity onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.equity_list_activity);
		errorView = (TextView) findViewById(R.id.errorMessage);

		Bundle b = getIntent().getExtras();
		Portfolio portfolio = b.getParcelable("portfolio");

		equities = portfolio.getEquities();
		mAdapter = new EquityAdapter(equities, getApplicationContext());
		setListAdapter(mAdapter);

		lastUpdatedView = (TextView) findViewById(R.id.lastUpdated);
		lastUpdatedView.setText(portfolio.getLastUpdate());
		// Set context
		EasyTracker.getInstance().setContext(getApplicationContext());
		// Instantiate the Tracker
		tracker = EasyTracker.getTracker();
	}

	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance().activityStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStop(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		MainTask mainTask;
		switch (item.getItemId()) {
		case R.id.action_logout:
			mainTask = new MainTask(this, UrlType.LOGOUT, null);
			mainTask.execute((Void) null);
			return true;
		case R.id.refresh:
			menuItem = item;
			menuItem.setActionView(R.layout.progressbar);
			menuItem.expandActionView();
			tracker.sendEvent("Buttons Category", "Reload", "", 0L);
			mainTask = new MainTask(this, UrlType.RELOAD, null);
			mainTask.execute((Void) null);
			return true;
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void reloadData(Portfolio portfolio) {
		// StockTrackerApp app = (StockTrackerApp) getApplication();
		equities.clear();
		equities.addAll(portfolio.getEquities());
		mAdapter.notifyDataSetChanged();
		lastUpdatedView.setText(portfolio.getLastUpdate());
		menuItem.collapseActionView();
		menuItem.setActionView(null);
		Intent resultIntent = new Intent();
		resultIntent.putExtra("portfolio", portfolio);
		setResult(Activity.RESULT_OK, resultIntent);
		StockTrackerApp app = (StockTrackerApp) getApplication();
		app.toast();
	}

	@Override
	public void displayError(JSONObject json) {
		boolean sessionError = ((StockTrackerApp) getApplication()).isSessionError(json);
		if (sessionError) {
			((StockTrackerApp) getApplication()).loadErrorActivity(this, json);
		} else {
			errorView.setText(json.optString("error"));
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) LayoutParams.MATCH_PARENT,
					(int) LayoutParams.MATCH_PARENT);
			params.addRule(RelativeLayout.BELOW, errorView.getId());
			ListView listView = (ListView) findViewById(android.R.id.list);
			listView.setLayoutParams(params);
			menuItem.collapseActionView();
			menuItem.setActionView(null);
		}
	}

	@Override
	public void logOut() {
		((StockTrackerApp) getApplication()).logOut(this);
	}
}
