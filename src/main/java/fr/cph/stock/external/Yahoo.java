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

/**
 * This class take care of the connexion to Yahoo API. It uses YQL language.
 * 
 * @author Carl-Philipp Harmant
 * 
 */
public class Yahoo {
	/** Logger **/
	private static final Logger LOG = Logger.getLogger(Yahoo.class);
	/** Url base **/
	private static final String URL_BASE = "http://query.yahooapis.com/v1/public/yql?q=";
	/** Url end **/
	private static final String URL_END = "&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=cbfunc";
	/** The request **/
	private String request;

	/**
	 * Constructor that build a yahoo object with the given request
	 * 
	 * @param req
	 *            the request
	 */
	public Yahoo(final String req) {
		this.request = req;
	}

	/**
	 * This function build the URL.
	 * 
	 * @return a url
	 * @throws YahooException
	 *             the yahoo exception
	 */
	protected final String urlBuilder() throws YahooException {
		try {
			return URL_BASE + URLEncoder.encode(request, "UTF-8") + URL_END;
		} catch (UnsupportedEncodingException e) {
			throw new YahooException("Error: " + YahooException.ENCODE_ERROR, e);
		}
	}

	/**
	 * THis function connect to the given address and return in a string the content of the page
	 * 
	 * @param address
	 *            the address
	 * @return the content of the page
	 * @throws YahooException
	 *             the yahoo exception
	 */
	protected final String connectUrl(final String address) throws YahooException {
		LOG.debug("URL " + address);
		String toreturn = null;
		try {
			URL url = new URL(address);
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
			in.close();
		} catch (IOException e) {
			throw new YahooException("Error: " + YahooException.CONNECT_ERROR, e);
		}
		return toreturn;
	}

	/**
	 * Convert the string to a json object, removing the unwanted part
	 * 
	 * @param data
	 *            the data to convert
	 * @return a JSONObject
	 */
	protected final JSONObject convertDataToJSONObject(final String data) {
		String temp = data.substring(11, data.length());
		String temp2 = temp.substring(0, temp.length() - 2);
		return JSONObject.fromObject(temp2);
	}

	/**
	 * Connect to url and get the response in JSON
	 * 
	 * @return a JSONObject that contains the data
	 * @throws YahooException
	 *             the yahoo exception
	 */
	public final JSONObject getJSONObject() throws YahooException {
		String data = connectUrl(urlBuilder());
		return convertDataToJSONObject(data);
	}
}