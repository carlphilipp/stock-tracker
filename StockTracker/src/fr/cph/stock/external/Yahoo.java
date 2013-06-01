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

package fr.cph.stock.external;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import fr.cph.stock.exception.YahooException;

public class Yahoo {
	private static final Logger log = Logger.getLogger(Yahoo.class);

	
	private static String URL_BASE = "http://query.yahooapis.com/v1/public/yql?q=";
	private static String URL_END = "&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=cbfunc";

	private String request;

	public Yahoo(String request) {
		this.request = request;
	}

	protected String urlBuilder() throws YahooException {
		try {
			return URL_BASE + URLEncoder.encode(request, "UTF-8") + URL_END;
		} catch (UnsupportedEncodingException e) {
			throw new YahooException("Error: " + YahooException.ENCODE_ERROR, e);
		}
	}

	protected String connectUrl(String adress) throws YahooException {
		log.debug("URL " + adress);
		String toreturn = null;
		try {
			URL url = new URL(adress);
			URLConnection uc = url.openConnection();
			Charset charset = Charset.forName("UTF8");
			InputStreamReader in = new InputStreamReader(uc.getInputStream(), charset);
			int c = in.read();
			StringBuilder build = new StringBuilder();
			while (c != -1) {
				build.append((char) c);
				c = in.read();
			}
			toreturn = build.toString();
		} catch (IOException e) {
			throw new YahooException("Error: " + YahooException.CONNECT_ERROR, e);
		}
		return toreturn;
	}

	protected JSONObject convertDataToJSONObject(String data) {
		String test = data.substring(7, data.length());
		String test2 = test.substring(0, test.length() - 2);
		return JSONObject.fromObject(test2);
	}

	public JSONObject getJSONObject() throws YahooException {
		String data = connectUrl(urlBuilder());
		return convertDataToJSONObject(data);
	}

}
