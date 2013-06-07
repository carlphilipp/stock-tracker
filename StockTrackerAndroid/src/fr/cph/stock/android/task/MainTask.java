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

package fr.cph.stock.android.task;

import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;
import fr.cph.stock.android.entity.EntityBuilder;
import fr.cph.stock.android.entity.Portfolio;
import fr.cph.stock.android.enumtype.UrlType;
import fr.cph.stock.android.exception.AppException;
import fr.cph.stock.android.web.Connect;

public class MainTask extends AsyncTask<Void, Void, Boolean> {

	private static final String TAG = "MainTask";

	private Object object;
	private UrlType url;
	private String params;
	private JSONObject json;
	private String error;

	public MainTask(Object object, UrlType url, String params) {
		this.object = object;
		this.url = url;
		if (params == null) {
			this.params = "";
		} else {
			this.params = params;
		}
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		boolean toReturn = true;
		Connect connect = Connect.getInstance();
		connect.setRequest(url.getUrl() + this.params);
		try {
			json = connect.getJSONObject();
		} catch (AppException e) {
			Log.w(TAG, e.getMessage());
			this.error = e.getMessage();
			toReturn = false;
		}
		if (json != null) {
			String errorMessage = json.optString("error");
			if (!errorMessage.equals("")) {
				this.error = errorMessage;
				toReturn = false;
			}
		}
		return toReturn;
	}

	@Override
	protected void onPostExecute(final Boolean success) {
		try {
			Class<?> classe = object.getClass();
			Class<?>[] param;
			Portfolio portfolio;
			EntityBuilder entityBuilder;
			if (success) {
				switch (url) {
				case LOGOUT:
					Log.i(TAG, "logout: " + classe.getName());
					classe.getMethod("logOut").invoke(object);
					break;
				case UPDATEHISTORY:
					param = new Class[1];
					param[0] = Portfolio.class;
					entityBuilder = new EntityBuilder(json);
					portfolio = entityBuilder.getPortfolio();
					classe.getMethod("reloadData", param).invoke(object, portfolio);
					break;
				case AUTH:
					param = new Class[1];
					param[0] = Portfolio.class;
					entityBuilder = new EntityBuilder(json);
					portfolio = entityBuilder.getPortfolio();
					classe.getMethod("loadHome", param).invoke(object, portfolio);
					break;
				case RELOAD:
					param = new Class[1];
					param[0] = Portfolio.class;
					entityBuilder = new EntityBuilder(json);
					portfolio = entityBuilder.getPortfolio();
					classe.getMethod("reloadData", param).invoke(object, portfolio);
					break;
				}
			} else {
				param = new Class[1];
				param[0] = JSONObject.class;
				JSONObject derp = new JSONObject();
				derp.accumulate("error", error);
				classe.getMethod("displayError", param).invoke(object, derp);
			}
		} catch (Exception e) {
			Log.e(TAG, "", e);
		}
		super.onPostExecute(success);
	}
}
