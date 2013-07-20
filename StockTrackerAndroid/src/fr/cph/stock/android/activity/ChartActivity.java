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

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Tracker;

import fr.cph.stock.android.R;
import fr.cph.stock.android.StockTrackerApp;
import fr.cph.stock.android.entity.Portfolio;
import fr.cph.stock.android.enumtype.ChartType;
import fr.cph.stock.android.enumtype.UrlType;
import fr.cph.stock.android.task.MainTask;
import fr.cph.stock.android.web.DebugWebChromeClient;

/**
 * This class reprents the chart activity
 * 
 * @author Carl-Philipp Harmant
 * 
 */
public class ChartActivity extends Activity implements IStockTrackerActivity {

	/** Tag **/
	private static final String TAG = "ChartActivity";

	private Tracker tracker;

	/** Graphics components **/
	private MenuItem menuItem;
	private TextView errorView;
	private ChartType chartType;
	private Portfolio portfolio;
	private WebView webView;
	private ActionBar actionBar;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chart_activity);

		Bundle b = getIntent().getExtras();
		portfolio = b.getParcelable("portfolio");
		chartType = ChartType.getEnum(b.getString("chartType"));

		errorView = (TextView) findViewById(R.id.errorMessage);
		actionBar = getActionBar();
		webView = (WebView) findViewById(R.id.webView);
		String data = getData();
		webView.setWebChromeClient(new DebugWebChromeClient());
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		// myWebView.setBackgroundColor(0x00000000);
		// myWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		webView.loadDataWithBaseURL("file:///android_asset/www/", data, "text/html", "UTF-8", null);
		webView.reload();

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

	/**
	 * 
	 * @return
	 */
	private String getData() {
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		String data = null;
		try {
			InputStream is = null;
			StringWriter writer = new StringWriter();
			switch (chartType) {
			case CAPITALIZATION:
				is = getApplicationContext().getAssets().open("www/pie.html");
				IOUtils.copy(is, writer, "UTF8");
				data = writer.toString();
				data = data.replace("#DATA#", portfolio.getChartCapData());
				data = data.replace("#TITLE#", portfolio.getChartCapTitle());
				data = data.replace("#DRAW#", portfolio.getChartCapDraw());
				data = data.replace("#COMPANIES#", portfolio.getChartCapCompanies());
				data = data.replaceAll("#WIDTH#", (int) (metrics.widthPixels / metrics.density) - 30 + "");
				data = data.replaceAll("#HEIGHT#", (int) (metrics.widthPixels / metrics.density) - 30 + "");
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				actionBar.setTitle("Capitalization Chart");
				webView.setHorizontalScrollBarEnabled(false);
				break;
			case SECTOR:
				is = getApplicationContext().getAssets().open("www/pie.html");
				IOUtils.copy(is, writer, "UTF8");
				data = writer.toString();
				data = data.replace("#DATA#", portfolio.getChartSectorData());
				data = data.replace("#TITLE#", portfolio.getChartSectorTitle());
				data = data.replace("#DRAW#", portfolio.getChartSectorDraw());
				data = data.replace("#COMPANIES#", portfolio.getChartSectorCompanies());
				data = data.replaceAll("#WIDTH#", (int) (metrics.widthPixels / metrics.density) - 30 + "");
				data = data.replaceAll("#HEIGHT#", (int) (metrics.widthPixels / metrics.density) - 30 + "");
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				actionBar.setTitle("Sector Chart");
				webView.setHorizontalScrollBarEnabled(false);
				break;
			case SHARE_VALUE:
				is = getApplicationContext().getAssets().open("www/share_value.html");
				IOUtils.copy(is, writer, "UTF8");
				data = writer.toString();
				data = data.replace("#DATA#", portfolio.getChartData());
				data = data.replace("#DRAW#", portfolio.getChartDraw());
				data = data.replace("#COLOR#", portfolio.getChartColors());
				data = data.replace("#DATE#", portfolio.getChartDate());
				data = data.replaceAll("#WIDTH#", ((int) (metrics.widthPixels / metrics.density)) - 30 + "");
				data = data.replaceAll("#HEIGHT#", (int) (metrics.heightPixels / metrics.density / 1.35) + "");
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				actionBar.setTitle("Share Value Chart");
				break;
			}
		} catch (IOException e) {
			Log.e(TAG, "", e);
		}
		return data;
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

	/**
	 * 
	 */
	public void reloadData(Portfolio portfolio) {
		menuItem.collapseActionView();
		menuItem.setActionView(null);
		Intent resultIntent = new Intent();
		resultIntent.putExtra("portfolio", portfolio);
		this.portfolio = portfolio;
		String data = getData();
		webView.loadDataWithBaseURL("file:///android_asset/www/", data, "text/html", "UTF-8", null);
		setResult(Activity.RESULT_OK, resultIntent);
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
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) LayoutParams.MATCH_PARENT,
					(int) LayoutParams.MATCH_PARENT);
			params.addRule(RelativeLayout.BELOW, errorView.getId());
			WebView webView = (WebView) findViewById(R.id.webView);
			webView.setLayoutParams(params);
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
}
