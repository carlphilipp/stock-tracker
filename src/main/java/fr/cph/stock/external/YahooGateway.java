/**
 * Copyright 2016 Carl-Philipp Harmant
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.cph.stock.external;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.cph.stock.exception.YahooException;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * This class take care of the connexion to YahooGateway API. It uses YQL language.
 *
 * @author Carl-Philipp Harmant
 */
@Log4j2
enum YahooGateway {

	INSTANCE;

	/**
	 * Url base
	 **/
	private static final String URL_BASE = "http://query.yahooapis.com/v1/public/yql?q=";
	/**
	 * Url end
	 **/
	private static final String URL_END = "&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=cbfunc";

	/**
	 * This function build the URL.
	 *
	 * @return a url
	 * @throws YahooException the yahoo exception
	 */
	private String urlBuilder(final String yqlRequest) throws YahooException {
		try {
			return URL_BASE + URLEncoder.encode(yqlRequest, "UTF-8") + URL_END;
		} catch (final UnsupportedEncodingException e) {
			throw YahooException.encodeErrorException(e);
		}
	}

	/**
	 * THis function connect to the given address and return in a string the content of the page
	 *
	 * @param address the address
	 * @return the content of the page
	 * @throws YahooException the yahoo exception
	 */
	private String connectUrl(final String address) throws YahooException {
		log.debug("URL {}", address);
		String toReturn;
		try (final InputStreamReader in = new InputStreamReader((new URL(address).openConnection()).getInputStream(), Charset.forName("UTF8"))) {
			int c = in.read();
			final StringBuilder build = new StringBuilder();
			while (c != -1) {
				build.append((char) c);
				c = in.read();
			}
			toReturn = build.toString();
		} catch (final IOException e) {
			throw YahooException.cantConnectException(e);
		}
		return toReturn;
	}

	/**
	 * Convert the string to a json object, removing the unwanted part
	 *
	 * @param data the data to convert
	 * @return a JSONObject
	 */
	private JsonObject convertDataToJSONObject(final String data) {
		final String temp = data.substring(11, data.length());
		final String temp2 = temp.substring(0, temp.length() - 2);
		JsonParser jsonParser = new JsonParser();
		return (JsonObject) jsonParser.parse(temp2);
	}

	/**
	 * Connect to url and get the response in JSON
	 *
	 * @return a JSONObject that contains the data
	 * @throws YahooException the yahoo exception
	 */
	public final JsonObject getJSONObject(final String yqlRequest) throws YahooException {
		final String data = connectUrl(urlBuilder(yqlRequest));
		return convertDataToJSONObject(data);
	}
}
