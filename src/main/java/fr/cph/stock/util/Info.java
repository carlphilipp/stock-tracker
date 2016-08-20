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

package fr.cph.stock.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * This class get the general info from a property file
 *
 * @author Carl-Philipp Harmant
 */
public enum Info {
	;

	static {
		final Properties prop = Util.getProperties();
		NAME = prop.getProperty("name");
		ADDRESS = prop.getProperty("address");
		FOLDER = prop.getProperty("folder");
		YAHOO_ID_CAC40 = prop.getProperty("yahoocac40");
		YAHOO_ID_SP500 = prop.getProperty("yahoosp500");
		ADMINS = Collections.unmodifiableList(Arrays.asList(prop.getProperty("admins").split(";")));
		REPORT = prop.getProperty("report.ireport");
	}

	/**
	 * Name of the webapp
	 **/
	public static final String NAME;
	/**
	 * Current address of the webapp
	 **/
	public static final String ADDRESS;
	/**
	 * Current folder after the address
	 **/
	public static final String FOLDER;
	/**
	 * Yahoo id of cac40
	 **/
	public static final String YAHOO_ID_CAC40;
	/**
	 * Yahoo id of s&p500
	 **/
	public static final String YAHOO_ID_SP500;
	/**
	 * Admins
	 **/
	public static final List<String> ADMINS;
	/**
	 * Jrxml repport
	 **/
	public static final String REPORT;

}
