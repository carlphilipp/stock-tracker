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

import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import fr.cph.stock.android.R;
import fr.cph.stock.android.StockTrackerApp;
import fr.cph.stock.android.adapter.MainListAdapter;
import fr.cph.stock.android.entity.Portfolio;
import fr.cph.stock.android.enumtype.UrlType;
import fr.cph.stock.android.listener.ErrorMainOnClickListener;
import fr.cph.stock.android.task.MainTask;

public class MainActivity extends Activity implements IStockTrackerActivity {

	private static final String TAG = "MainActivity";

	private MenuItem menuItem;

	public static final int ACCOUNT_REQUEST = 1;
	public static final int EQUITY_REQUEST = 2;
	public static final int OVERALL_REQUEST = 3;
	public static final int CHART_REQUEST = 4;

	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

	private MainListAdapter ada;
	private Portfolio portfolio;
	private TextView errorView;
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "MainActivity onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Bundle b = getIntent().getExtras();
		portfolio = b.getParcelable("portfolio");

		ada = new MainListAdapter(this, portfolio);
		listView = (ListView) findViewById(R.id.mainList);
		listView.setAdapter(ada);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Intent intent;
				switch (position) {
				case 0:
					intent = new Intent(getBaseContext(), AccountActivity.class);
					intent.putExtra("portfolio", portfolio);
					startActivityForResult(intent, MainActivity.ACCOUNT_REQUEST);
					overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
					break;
				case 1:
					intent = new Intent(getBaseContext(), EquityActivity.class);
					intent.putExtra("portfolio", portfolio);
					startActivityForResult(intent, MainActivity.EQUITY_REQUEST);
					overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
					break;
				case 2:
					intent = new Intent(getBaseContext(), OverallActivity.class);
					intent.putExtra("portfolio", portfolio);
					startActivityForResult(intent, MainActivity.OVERALL_REQUEST);
					overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
					break;
				}
			}

		});

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);

		errorView = (TextView) findViewById(R.id.errorMessage);
		errorView.setOnClickListener(new ErrorMainOnClickListener(listView, errorView));
	}

	@Override
	protected void onRestart() {
		super.onResume();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		switch (resultCode) {
		case 100:
			finish();
			break;
		case Activity.RESULT_OK:
			Bundle b = data.getExtras();
			portfolio = b.getParcelable("portfolio");
			ada.update(portfolio);
			break;
		}
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
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
			mainTask = new MainTask(this, UrlType.RELOAD, null);
			mainTask.execute((Void) null);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void reloadData(Portfolio portfolio) {
		this.portfolio = portfolio;
		menuItem.collapseActionView();
		menuItem.setActionView(null);
		ada.update(this.portfolio);
		StockTrackerApp app = (StockTrackerApp) getApplication();
		app.toast();
	}

	@Override
	public void displayError(JSONObject json) {
		boolean sessionError = ((StockTrackerApp) getApplication()).isSessionError(json);
		if (sessionError) {
			((StockTrackerApp) getApplication()).loadErrorActivity(this, json);
		} else {
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) LayoutParams.MATCH_PARENT,
					(int) LayoutParams.MATCH_PARENT);
			params.addRule(RelativeLayout.BELOW, errorView.getId());
			listView.setLayoutParams(params);
			errorView.setText(json.optString("error"));
			menuItem.collapseActionView();
			menuItem.setActionView(null);
		}
	}

	@Override
	public void logOut() {
		((StockTrackerApp) getApplication()).logOut(this);
	}

}
