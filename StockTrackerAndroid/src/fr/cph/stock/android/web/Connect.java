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

package fr.cph.stock.android.web;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.nio.charset.Charset;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import fr.cph.stock.android.exception.AppException;

public class Connect {

	private static Connect instance = null;

	private CookieManager cookieManager;
	private DefaultHttpClient client;

	private static final String TAG = "Connect";

//	public static String URL_BASE = "http://192.168.2.24:8080/StockTracker/";
	public static String URL_BASE = "https://www.stocktracker.fr/";
	public static String URL_LOGIN = "?login=";
	public static String URL_PASSWORD ="&password=";

	
	private String request;

	protected Connect() {
		this.cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
		CookieHandler.setDefault(cookieManager);
		this.client = new MyHttpClient();
	}

	public static Connect getInstance() {
		if (instance == null) {
			instance = new Connect();
		}
		return instance;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	protected String urlBuilder() throws UnsupportedEncodingException {
		return URL_BASE + request;
//		return URL_BASE +URLEncoder.encode(request, "UTF-8")
	}

	protected String connectUrl(String adress) throws IOException {
		String toreturn = null;
		client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 60000);
		Log.i(TAG, "adress: " + adress);
		HttpGet get = new HttpGet(adress);
		HttpResponse getResponse = client.execute(get);
		HttpEntity responseEntity = getResponse.getEntity();

		Charset charset = Charset.forName("UTF8");
		InputStreamReader in = new InputStreamReader(responseEntity.getContent(), charset);
		int c = in.read();
		StringBuilder build = new StringBuilder();
		while (c != -1) {
			build.append((char) c);
			c = in.read();
		}
		toreturn = build.toString();
		return toreturn;
	}

	protected JSONObject convertDataToJSONObject(String data) throws JSONException {
		Log.d(TAG, "Received: " + data);
		JSONObject json = null;
		json = new JSONObject(data);
		return json;
	}

	public JSONObject getJSONObject() throws AppException {
		String data;
		JSONObject json;
		try {
			data = connectUrl(urlBuilder());
			json = convertDataToJSONObject(data);
		} catch (UnsupportedEncodingException e) {
			throw new AppException(e.getMessage(), e);
		} catch (IOException e) {
			throw new AppException(e.getMessage(), e);
		} catch (JSONException e) {
			throw new AppException(e.getMessage(), e);
		}
		return json;
	}

}
