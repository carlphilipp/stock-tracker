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

import org.json.JSONException;
import org.json.JSONObject;

import com.google.analytics.tracking.android.EasyTracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import fr.cph.stock.android.R;
import fr.cph.stock.android.entity.Portfolio;
import fr.cph.stock.android.listener.ErrorButtonOnClickListener;

public class ErrorActivity extends Activity {
	private static final String TAG = "ErrorActivity";

	private TextView error;
	private String login;
	private String password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.error);
		String msg = getIntent().getExtras().getString("data");
		login = getIntent().getExtras().getString("login");
		password = getIntent().getExtras().getString("password");
		try {
			JSONObject json = new JSONObject(msg);
			error = (TextView) findViewById(R.id.error_message);
			error.setText(json.optString("error"));
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		Button button = (Button) findViewById(R.id.retry_button);
		button.setOnClickListener(new ErrorButtonOnClickListener(this, login, password));
		EasyTracker.getInstance().setContext(this);
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

	public void displayError(JSONObject json) {
		error.setText(json.optString("error"));
	}

	public void loadHome(Portfolio portfolio) {
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("portfolio", portfolio);
		finish();
		startActivity(intent);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}

}
