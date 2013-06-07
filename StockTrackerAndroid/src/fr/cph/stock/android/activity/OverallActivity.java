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
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import fr.cph.stock.android.R;
import fr.cph.stock.android.StockTrackerApp;
import fr.cph.stock.android.adapter.ShareValueAdapter;
import fr.cph.stock.android.entity.Account;
import fr.cph.stock.android.entity.Portfolio;
import fr.cph.stock.android.entity.ShareValue;
import fr.cph.stock.android.enumtype.UrlType;
import fr.cph.stock.android.task.MainTask;

public class OverallActivity extends ListActivity implements IStockTrackerActivity {

	private static final String TAG = "OverallActivity";

	private MenuItem menuItem;
	private MenuItem refreshItem;
	private List<ShareValue> shareValues;
	private ShareValueAdapter ada;
	private TextView errorView;
	private Portfolio portfolio;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.overall_activity);
		errorView = (TextView) findViewById(R.id.errorMessage);
		Bundle b = getIntent().getExtras();
		portfolio = b.getParcelable("portfolio");
		shareValues = portfolio.getShareValues();
		ada = new ShareValueAdapter(shareValues, getApplicationContext());
		setListAdapter(ada);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.overall, menu);
		refreshItem = menu.getItem(2);
		return true;
	}

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
			mainTask = new MainTask(this, UrlType.RELOAD, null);
			mainTask.execute((Void) null);
			return true;
		case R.id.action_update:
			showPanelUpdateHistory();
			return true;
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void showPanelUpdateHistory() {
		final Dialog alert = new Dialog(this);
		alert.setTitle("Update history");
		alert.setContentView(R.layout.history_dialog);

		final Spinner checked = (Spinner) alert.findViewById(R.id.accountList);
		List<String> list = new ArrayList<String>();
		for (Account acc : portfolio.getAccounts()) {
			list.add(acc.getName());
		}
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		checked.setAdapter(dataAdapter);

		Button dialogButton = (Button) alert.findViewById(R.id.dialogButtonOK);
		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				refreshItem.setActionView(R.layout.progressbar);
				refreshItem.expandActionView();
				Account account = portfolio.getAccounts().get(checked.getSelectedItemPosition());
				EditText liquidityView = (EditText) alert.findViewById(R.id.liquidityMov);
				EditText yieldView = (EditText) alert.findViewById(R.id.yield);
				EditText buyView = (EditText) alert.findViewById(R.id.buy);
				EditText sellView = (EditText) alert.findViewById(R.id.sell);
				EditText taxeView = (EditText) alert.findViewById(R.id.taxe);
				EditText commentaryView = (EditText) alert.findViewById(R.id.commentaryEditText);

				String params = null;
				params = "?accountId=" + account.getId() + "&liquidity=" + liquidityView.getText() + "&yield=" + yieldView.getText()
						+ "&buy=" + buyView.getText() + "&sell=" + sellView.getText() + "&taxe=" + taxeView.getText()
						+ "&commentary=" + commentaryView.getText().toString().replaceAll(" ", "%20");
				MainTask mainTask = new MainTask(OverallActivity.this, UrlType.UPDATEHISTORY, params);
				mainTask.execute((Void) null);
				alert.dismiss();
			}
		});
		dialogButton = (Button) alert.findViewById(R.id.dialogButtonCancel);
		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				alert.dismiss();
			}
		});
		alert.show();
	}

	@Override
	public void reloadData(Portfolio portfolio) {
		shareValues.clear();
		shareValues.addAll(portfolio.getShareValues());
		ada.notifyDataSetChanged();
		refreshItem.collapseActionView();
		refreshItem.setActionView(null);
		Intent resultIntent = new Intent();
		resultIntent.putExtra("portfolio", portfolio);
		setResult(Activity.RESULT_OK, resultIntent);
		StockTrackerApp app = (StockTrackerApp) getApplication();
		app.toast();
	}

	@Override
	public void displayError(JSONObject json) {
		Log.i(TAG, json.toString());
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
			refreshItem.collapseActionView();
			refreshItem.setActionView(null);
		}
	}

	@Override
	public void logOut() {
		((StockTrackerApp) getApplication()).logOut(this);
	}
}
