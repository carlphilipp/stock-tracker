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

package fr.cph.stock.util;

import java.util.Properties;

/**
 * This class get the general info from a property file
 * 
 * @author Carl-Philipp Harmant
 * 
 */
public class Info {

	/** Access in a static way to the property file **/
	static {
		Properties prop = Util.getProperties("app.properties");
		NAME = prop.getProperty("name");
		ADDRESS = prop.getProperty("address");
		FOLDER = prop.getProperty("folder");
		YAHOOID_CAC40 = prop.getProperty("yahoocac40");
		YAHOOID_SP500 = prop.getProperty("yahoosp500");
		admins = prop.getProperty("admins").split(";");
	}

	/** Name of the webapp **/
	public static String NAME;
	/** Current address of the webapp **/
	public static String ADDRESS;
	/** Current folder after the address **/
	public static String FOLDER;
	/** Yahoo id of cac40 **/
	public static String YAHOOID_CAC40;
	/** Yahoo id of s&p500 **/
	public static String YAHOOID_SP500;

	public static String[] admins;

}
