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

package fr.cph.stock.android;

import org.json.JSONObject;

import fr.cph.stock.android.activity.BaseActivity;
import fr.cph.stock.android.activity.ErrorActivity;
import fr.cph.stock.android.activity.LoginActivity;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

/**
 * Default app
 * 
 * @author Carl-Philipp Harmant
 * 
 */
public class StockTrackerApp extends Application {

	/**
	 * 
	 * @param activity
	 */
	public void logOut(Activity activity) {
		SharedPreferences settings = getSharedPreferences(BaseActivity.PREFS_NAME, 0);
		String login = settings.getString("login", null);
		String password = settings.getString("password", null);
		if (login != null) {
			settings.edit().remove("login").commit();
		}
		if (password != null) {
			settings.edit().remove("password").commit();
		}
		activity.setResult(100);
		activity.finish();
		Intent intent = new Intent(this, LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	/**
	 * 
	 * @param json
	 * @return
	 */
	public boolean isSessionError(JSONObject json) {
		boolean result = false;
		String error = json.optString("error");
		if (error.equals("No active session") || error.equals("User session not found")) {
			result = true;
		}
		return result;
	}

	/**
	 * 
	 * @param currentActivity
	 * @param json
	 */
	public void loadErrorActivity(Activity currentActivity, JSONObject json) {
		Intent intent = new Intent(this, ErrorActivity.class);
		intent.putExtra("data", json.toString());
		SharedPreferences settings = getSharedPreferences(BaseActivity.PREFS_NAME, 0);
		String login = settings.getString("login", null);
		String password = settings.getString("password", null);
		intent.putExtra("login", login);
		intent.putExtra("password", password);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		currentActivity.finish();
	}

	/**
	 * 
	 */
	public void toast() {
		Context context = getApplicationContext();
		CharSequence text = "Updated !";
		int duration = Toast.LENGTH_LONG;
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}
}
