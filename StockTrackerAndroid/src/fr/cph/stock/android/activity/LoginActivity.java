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
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import fr.cph.stock.android.R;
import fr.cph.stock.android.entity.Portfolio;
import fr.cph.stock.android.enumtype.UrlType;
import fr.cph.stock.android.task.MainTask;
import fr.cph.stock.android.web.Md5;

/**
 * Activity which displays a login screen to the user, offering registration as well.
 */
public class LoginActivity extends Activity {

	private static final String TAG = "LoginActivity";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	// private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mLogin;
	private String mPassword;

	// UI references.
	private EditText mLoginView;
	private EditText mPasswordView;
	private CheckBox checkBox;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	private TextView errorView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "LoginActivity onCreate");
		super.onCreate(savedInstanceState);
		// Remove title bar
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login_activity);

		// Set up the login form.
		mLoginView = (EditText) findViewById(R.id.email);
		errorView = (TextView) findViewById(R.id.login_error);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
				if (id == R.id.login || id == EditorInfo.IME_NULL) {
					attemptLogin();
					return true;
				}
				return false;
			}
		});

		checkBox = (CheckBox) findViewById(R.id.checkbox);
		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				attemptLogin();
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// to add minu top right
		// getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	/**
	 * Attempts to sign in or register the account specified by the login form. If there are form errors
	 * (invalid email, missing fields, etc.), the errors are presented and no actual login attempt is
	 * made.
	 */
	public void attemptLogin() {
		// if (mAuthTask != null) {
		// return;
		// }

		// Reset errors.
		mLoginView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mLogin = mLoginView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mLogin)) {
			mLoginView.setError(getString(R.string.error_field_required));
			focusView = mLoginView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true, null);
			Md5 md5 = new Md5(mPassword);
			UrlType urlAuth = UrlType.AUTH;
			String params = "?login=" + mLogin + "&password=" + md5.getHexInString();
			MainTask derp = new MainTask(this, urlAuth, params);
			derp.execute((Void) null);
		}
	}

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

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
				}
			});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
		if (!show) {
			errorView.setText(errorMessage);
			errorView.setTextColor(Color.rgb(160, 0, 0));
		}
	}

	public void onCheckboxClicked(View view) {

	}

	public void loadHome(Portfolio portfolio) {
		//String result = json.optString("error");
//		if (result.equals("")) {
			finish();
			if (checkBox.isChecked()) {
				saveCredentials();
			}
			Intent intent = new Intent(this, MainActivity.class);
			intent.putExtra("portfolio", portfolio);
			startActivity(intent);
//		} else {
//			showProgress(false, result);
//		}
	}

	private void saveCredentials() {
		SharedPreferences settings = getSharedPreferences(BaseActivity.PREFS_NAME, 0);
		settings.edit().putString("login", mLogin).commit();
		Md5 md5 = new Md5(mPassword);
		settings.edit().putString("password", md5.getHexInString()).commit();

	}
	
	public void displayError(JSONObject json){
		showProgress(false, json.optString("error"));
	}
}
