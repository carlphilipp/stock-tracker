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

package fr.cph.stock.exception;

/**
 * Business class that access database and process data
 * 
 * @author Carl-Philipp Harmant
 * @version 1
 */
public class YahooException extends Exception {

	/** Serialization **/
	private static final long serialVersionUID = 1870609472239446880L;
	/** Error message **/
	public static final String ERROR = "Yahoo result null. Table is probably locked.";
	/** Error message **/
	public static final String CONNECT_ERROR = "Can't connect to yahoo website";
	/** Error message **/
	public static final String ENCODE_ERROR = "Url encoding did not work";

	/**
	 * Constructor
	 * 
	 * @param message
	 *            the error message
	 */
	public YahooException(final String message) {
		super(message);
	}

	/**
	 * Constructor
	 * 
	 * @param message
	 *            the error message
	 * @param e
	 *            the exception
	 */
	public YahooException(final String message, final Exception e) {
		super(message, e);
	}
}
