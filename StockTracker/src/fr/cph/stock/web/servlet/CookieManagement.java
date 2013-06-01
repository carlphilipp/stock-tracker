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

package fr.cph.stock.web.servlet;

import java.util.List;

import javax.servlet.http.Cookie;

public class CookieManagement {

	public static String getCookieLanguage(List<Cookie> cookies) {
		String language = null;
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("language")) {
				language = cookie.getValue();
				break;
			}
		}
		if (language == null) {
			language = "English";
		}
		return language;
	}

	public static boolean containsCookie(List<Cookie> cookies, String cookieName) {
		boolean res = false;
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(cookieName)) {
					res = true;
					break;
				}
			}
		}
		return res;
	}
}
