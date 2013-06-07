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

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;

public class MyHttpClient extends DefaultHttpClient {

	Context context;

	public MyHttpClient() {

		HttpParams httpParameters = new BasicHttpParams();
		int timeoutConnection = 6000;

		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		int timeoutSocket = 6000;

		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		this.setParams(httpParameters);

	}

	public MyHttpClient(Context context) {
		this();
		this.context = context;
	}

	@Override
	protected ClientConnectionManager createClientConnectionManager() {
		try {
			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			registry.register(new Scheme("https", new TrustAllSSLSocketFactory(), 443));
			return new SingleClientConnManager(getParams(), registry);
		} catch (Exception e) {
			return null;
		}

	}
}