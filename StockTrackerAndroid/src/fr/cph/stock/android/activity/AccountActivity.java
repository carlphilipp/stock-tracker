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

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Tracker;

import fr.cph.stock.android.R;
import fr.cph.stock.android.StockTrackerApp;
import fr.cph.stock.android.entity.Account;
import fr.cph.stock.android.entity.Portfolio;
import fr.cph.stock.android.enumtype.UrlType;
import fr.cph.stock.android.task.MainTask;
import fr.cph.stock.android.util.Util;

/**
 * This class represents the account activity
 * 
 * @author Carl-Philipp Harmant
 * 
 */
public class AccountActivity extends Activity implements IStockTrackerActivity {

	/** Tag **/
	private static final String TAG = "AccountActivity";

	private Tracker tracker;

	/** Portfolio **/
	private Portfolio portfolio;

	/** Graphical component **/
	private MenuItem menuItem;
	private TextView errorView;
	private TextView totalValueView;
	private TextView totalGainView;
	private TextView totalPlusMinusValueView;
	private TextView lastUpateView;
	private TextView liquidityView;
	private TextView yieldYearView;
	private TextView shareValueView;
	private TextView gainView;
	private TextView perfView;
	private TextView yieldView;
	private TextView taxesView;
	private List<TextView> textViews;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "Account Activity onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_activity);

		Bundle b = getIntent().getExtras();
		portfolio = b.getParcelable("portfolio");

		errorView = (TextView) findViewById(R.id.errorMessage);
		totalValueView = (TextView) findViewById(R.id.totalValue);
		totalGainView = (TextView) findViewById(R.id.totalGain);
		totalPlusMinusValueView = (TextView) findViewById(R.id.totalPlusMinusValue);
		lastUpateView = (TextView) findViewById(R.id.lastUpdate);
		liquidityView = (TextView) findViewById(R.id.liquidity);
		yieldYearView = (TextView) findViewById(R.id.yieldYear);
		shareValueView = (TextView) findViewById(R.id.shareValue);
		gainView = (TextView) findViewById(R.id.gain2);
		perfView = (TextView) findViewById(R.id.perf);
		yieldView = (TextView) findViewById(R.id.yieldPerf);
		taxesView = (TextView) findViewById(R.id.taxes);

		RelativeLayout accLayout = (RelativeLayout) findViewById(R.id.accountsLayout);
		TextView recent = new TextView(getApplicationContext());
		textViews = new ArrayList<TextView>();
		int id = 1;
		int nameID = 100;
		int viewId1 = 500;
		int currencyId = 1000;
		for (int i = 0; i < portfolio.getAccounts().size(); i++) {
			Account account = portfolio.getAccounts().get(i);
			TextView currentAccountNameTextView = new TextView(getApplicationContext());
			currentAccountNameTextView.setText(account.getName());
			currentAccountNameTextView.setTextColor(Color.GRAY);
			currentAccountNameTextView.setId(nameID);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) LayoutParams.WRAP_CONTENT,
					(int) LayoutParams.WRAP_CONTENT);
			if (i != 0) {
				params.addRule(RelativeLayout.BELOW, recent.getId());
			}
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			currentAccountNameTextView.setLayoutParams(params);
			accLayout.addView(currentAccountNameTextView, params);
			textViews.add(currentAccountNameTextView);

			View viewPoint1 = new View(getApplicationContext());
			viewPoint1.setId(viewId1);
			viewPoint1.setBackgroundColor(getResources().getColor(R.color.grey_light));
			params = new RelativeLayout.LayoutParams((int) LayoutParams.MATCH_PARENT, 2);
			params.addRule(RelativeLayout.RIGHT_OF, nameID);
			params.addRule(RelativeLayout.LEFT_OF, currencyId);
			params.setMargins(0, Util.convertDpToPxl(15, getApplicationContext()), 0, 0);
			if (i != 0) {
				params.addRule(RelativeLayout.BELOW, recent.getId());
			}
			viewPoint1.setLayoutParams(params);
			accLayout.addView(viewPoint1, params);

			TextView currentCurrencyTextView = new TextView(getApplicationContext());
			currentCurrencyTextView.setText(account.getCurrency());
			currentCurrencyTextView.setTextColor(Color.GRAY);
			currentCurrencyTextView.setId(currencyId);
			params = new RelativeLayout.LayoutParams((int) LayoutParams.WRAP_CONTENT, (int) LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.CENTER_HORIZONTAL);
			if (i != 0) {
				params.addRule(RelativeLayout.BELOW, recent.getId());
			}
			currentCurrencyTextView.setLayoutParams(params);
			accLayout.addView(currentCurrencyTextView, params);
			textViews.add(currentCurrencyTextView);

			View viewPoint2 = new View(getApplicationContext());
			viewPoint2.setBackgroundColor(getResources().getColor(R.color.grey_light));
			params = new RelativeLayout.LayoutParams((int) LayoutParams.MATCH_PARENT, 2);
			params.addRule(RelativeLayout.RIGHT_OF, currencyId);
			params.addRule(RelativeLayout.LEFT_OF, id);
			params.setMargins(0, Util.convertDpToPxl(15, getApplicationContext()), 0, 0);
			if (i != 0) {
				params.addRule(RelativeLayout.BELOW, recent.getId());
			}
			viewPoint2.setLayoutParams(params);
			accLayout.addView(viewPoint2, params);

			TextView currentTextView = new TextView(getApplicationContext());
			currentTextView.setText(account.getLiquidity());
			currentTextView.setTextColor(Color.GRAY);
			currentTextView.setId(id);
			params = new RelativeLayout.LayoutParams((int) LayoutParams.WRAP_CONTENT, (int) LayoutParams.WRAP_CONTENT);
			if (i != 0) {
				params.addRule(RelativeLayout.BELOW, recent.getId());
			}
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			currentTextView.setLayoutParams(params);
			recent = currentTextView;
			accLayout.addView(currentTextView, params);
			textViews.add(currentTextView);

			id++;
			nameID++;
			viewId1++;
			currencyId++;
		}
		buildUi(false);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.cph.stock.android.activity.IStockTrackerActivity#reloadData(fr.cph.stock.android.entity.Portfolio)
	 */
	@Override
	public void reloadData(Portfolio portfolio) {
		menuItem.collapseActionView();
		menuItem.setActionView(null);
		this.portfolio = portfolio;
		Intent resultIntent = new Intent();
		resultIntent.putExtra("portfolio", portfolio);
		setResult(Activity.RESULT_OK, resultIntent);
		buildUi(true);
		StockTrackerApp app = (StockTrackerApp) getApplication();
		app.toast();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.cph.stock.android.activity.IStockTrackerActivity#displayError(org.json.JSONObject)
	 */
	@Override
	public void displayError(JSONObject json) {
		boolean sessionError = ((StockTrackerApp) getApplication()).isSessionError(json);
		if (sessionError) {
			((StockTrackerApp) getApplication()).loadErrorActivity(this, json);
		} else {
			errorView.setText(json.optString("error"));
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) LayoutParams.WRAP_CONTENT,
					(int) LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.BELOW, errorView.getId());
			params.addRule(RelativeLayout.CENTER_HORIZONTAL);
			totalValueView.setLayoutParams(params);
			menuItem.collapseActionView();
			menuItem.setActionView(null);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.cph.stock.android.activity.IStockTrackerActivity#logOut()
	 */
	@Override
	public void logOut() {
		((StockTrackerApp) getApplication()).logOut(this);
	}

	/**
	 * Build UI
	 * 
	 * @param withAccounts
	 */
	private void buildUi(boolean withAccounts) {
		totalValueView.setText(portfolio.getTotalValue());
		totalGainView.setText(portfolio.getTotalGain());
		if (portfolio.isUp()) {
			totalGainView.setTextColor(Color.rgb(0, 160, 0));
		} else {
			totalGainView.setTextColor(Color.rgb(160, 0, 0));
		}
		totalPlusMinusValueView.setText(" (" + portfolio.getTotalPlusMinusValue() + ")");
		if (portfolio.isUp()) {
			totalPlusMinusValueView.setTextColor(Color.rgb(0, 160, 0));
		} else {
			totalPlusMinusValueView.setTextColor(Color.rgb(160, 0, 0));
		}
		lastUpateView.setText(portfolio.getLastUpdate());
		liquidityView.setText(portfolio.getLiquidity());
		yieldYearView.setText(portfolio.getYieldYear());
		shareValueView.setText(portfolio.getShareValues().get(0).getShareValue());
		gainView.setText(portfolio.getGainPerformance());
		perfView.setText(portfolio.getPerformancePerformance());
		yieldView.setText(portfolio.getYieldPerformance());
		taxesView.setText(portfolio.getTaxesPerformace());
		if (withAccounts) {
			int j = 0;
			int size = portfolio.getAccounts().size();
			for (int i = 0; i < size; i++) {
				Account account = portfolio.getAccounts().get(i);

				TextView currentAccountNameTextView = textViews.get(j++);
				currentAccountNameTextView.setText(account.getName());

				TextView currentTextView = textViews.get(j++);
				currentTextView.setText(account.getLiquidity());

				TextView currentCurrencyTextView = textViews.get(j++);
				currentCurrencyTextView.setText(account.getCurrency());
			}
		}
	}

}
