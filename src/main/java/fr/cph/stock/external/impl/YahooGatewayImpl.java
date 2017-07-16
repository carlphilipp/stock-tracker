/**
 * Copyright 2017 Carl-Philipp Harmant
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

package fr.cph.stock.external.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import fr.cph.stock.exception.YahooException;
import fr.cph.stock.external.YahooGateway;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * This class take care of the connexion to YahooGatewayImpl API. It uses YQL language.
 *
 * @author Carl-Philipp Harmant
 */
@Log4j2
@Singleton
public class YahooGatewayImpl implements YahooGateway {

	private static final String URL_BASE = "http://query.yahooapis.com/v1/public/yql?q=";
	private static final String URL_END = "&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=cbfunc";
	private static final String ENCODING = "UTF-8";

	@NonNull
	private final Gson gson;

	@Inject
	public YahooGatewayImpl(final Gson gson) {
		this.gson = gson;
	}

	/**
	 * This function build the URL.
	 *
	 * @return a url
	 * @throws YahooException the yahoo exception
	 */
	private String urlBuilder(final String yqlRequest) throws YahooException {
		try {
			return URL_BASE + URLEncoder.encode(yqlRequest, ENCODING) + URL_END;
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
	private String get(final String address) throws YahooException {
		log.debug("Request: {}", address);
		String toReturn;
		try (final InputStreamReader in = new InputStreamReader((new URL(address).openConnection()).getInputStream(), Charset.forName(ENCODING))) {
			toReturn = IOUtils.toString(in);
		} catch (final IOException e) {
			throw YahooException.cantConnectException(e);
		}
		log.debug("Response: {}", toReturn);
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
	@Override
	public final JsonObject getJSONObject(final String yqlRequest) throws YahooException {
		final String data = get(urlBuilder(yqlRequest));
		return convertDataToJSONObject(data);
	}

	@Override
	public final <T> T getObject(final String yqlQuery, final Class<T> clazz) throws YahooException {
		final String data = get(urlBuilder(yqlQuery));
		final String substring = data.substring(11, data.length());
		final String substring2 = substring.substring(0, substring.length() - 2);
		try {
			return gson.fromJson(substring2, clazz);
		} catch (final JsonSyntaxException jsonSyntaxException) {
			throw new YahooException("Error while parsing json: " + data, jsonSyntaxException);
		}
	}
}
