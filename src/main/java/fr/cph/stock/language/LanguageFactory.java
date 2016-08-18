/**
 * Copyright 2013 Carl-Philipp Harmant
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

package fr.cph.stock.language;

import java.util.HashMap;
import java.util.Map;

/**
 * Build languages
 *
 * @author Carl-Philipp Harmant
 */
public enum LanguageFactory {

	INSTANCE;

	/**
	 * The result LANGUAGE_MAP
	 **/
	private static Map<String, Map<String, String>> LANGUAGE_MAP;

	static {
		LANGUAGE_MAP = new HashMap<>();
		createLanguage("English");
		createLanguage("Francais");
	}

	private static void createLanguage(final String languageName) {
		final Language language = new Language("language/" + languageName + ".xml");
		LANGUAGE_MAP.put(languageName, language.getLanguage());
	}

	/**
	 * Get the current language needed
	 *
	 * @param language the language
	 * @return a LANGUAGE_MAP
	 */
	public Map<String, String> getLanguage(final String language) {
		return LANGUAGE_MAP.get(language);
	}
}
