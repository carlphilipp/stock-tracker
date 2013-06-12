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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import fr.cph.stock.android.R;
import fr.cph.stock.android.entity.Portfolio;
import fr.cph.stock.android.enumtype.UrlType;
import fr.cph.stock.android.task.MainTask;

/**
 * This class represents the base activity of the app
 * 
 * @author Carl-Philipp Harmant
 * 
 */
public class BaseActivity extends Activity {

	/** Tag **/
	private static final String TAG = "Base";
	/** Preference name in Android **/
	public static final String PREFS_NAME = "StockTracker";
	/** Login view **/
	private View mLoginStatusView;
	/** Login **/
	private String login;
	/** Password **/
	private String password;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "BaseActivity onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading);
		mLoginStatusView = findViewById(R.id.login_status);
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

		if (settings.contains("login") && settings.contains("password")) {
			showProgress(true, null);
			login = settings.getString("login", null);
			password = settings.getString("password", null);
			UrlType urlAuth = UrlType.AUTH;
			String params = "?login=" + login + "&password=" + password;
			MainTask derp = new MainTask(this, urlAuth, params);
			derp.execute((Void) null);
		} else {
			showProgress(false, null);
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			finish();
		}

	}

	/**
	 * Show progress bar
	 * 
	 * @param show
	 *            show the bar or not
	 * @param errorMessage
	 *            the error message
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show, String errorMessage) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
				}
			});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
		}
	}

	/**
	 * Load home
	 * 
	 * @param portfolio
	 *            the portfolio
	 */
	public void loadHome(Portfolio portfolio) {
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("portfolio", portfolio);
		showProgress(false, null);
		finish();
		startActivity(intent);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}

	/**
	 * Display error
	 * 
	 * @param jsonObject
	 *            the json object
	 */
	public void displayError(JSONObject jsonObject) {
		Intent intent = new Intent(this, ErrorActivity.class);
		intent.putExtra("data", jsonObject.toString());
		intent.putExtra("login", login);
		intent.putExtra("password", password);
		startActivity(intent);
		finish();
	}
}
