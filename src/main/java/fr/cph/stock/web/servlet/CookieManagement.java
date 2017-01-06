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

package fr.cph.stock.web.servlet;

import lombok.NonNull;

import javax.servlet.http.Cookie;
import java.util.List;

import static fr.cph.stock.util.Constants.ENGLISH;
import static fr.cph.stock.util.Constants.LANGUAGE;

/**
 * Cookie management
 *
 * @author Carl-Philipp Harmant
 */
public enum CookieManagement {
	;

	/**
	 * Get the name of the language stored in cookies
	 *
	 * @param cookies a list of cookie
	 * @return the name of the language
	 */
	public static String getCookieLanguage(@NonNull final List<Cookie> cookies) {
		return cookies.stream()
			.filter(cookie -> cookie.getName().equals(LANGUAGE))
			.findFirst()
			.map(Cookie::getValue)
			.orElse(ENGLISH);
	}

	/**
	 * Check if a cookie is already present
	 *
	 * @param cookies    a list of cookie
	 * @param cookieName a cookie name
	 * @return true or false
	 */
	private static boolean containsCookie(@NonNull final List<Cookie> cookies, final String cookieName) {
		return cookies.stream()
			.filter(cookie -> cookie.getName().equals(cookieName))
			.findFirst()
			.map(cookie -> true)
			.orElse(false);
	}

	public static boolean notContainsCookie(@NonNull final List<Cookie> cookies, final String cookieName) {
		return !containsCookie(cookies, cookieName);
	}
}
