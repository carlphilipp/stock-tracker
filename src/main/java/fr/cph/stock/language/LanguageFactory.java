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

import fr.cph.stock.exception.LanguageException;

import java.util.HashMap;
import java.util.Map;

/**
 * Build languages
 *
 * @author Carl-Philipp Harmant
 */
public final class LanguageFactory {

	private static final Object LOCK = new Object();
	/**
	 * The factory
	 **/
	private static LanguageFactory LANGUAGE_FACTORY;
	/**
	 * The result LANGUAGE_MAP
	 **/
	private static Map<String, Map<String, String>> LANGUAGE_MAP;

	/**
	 * Constructor to lock the construction of the object
	 */
	private LanguageFactory() {
	}

	/**
	 * Access to the factory from outside
	 *
	 * @return a LanguageFactory
	 * @throws LanguageException the language exception
	 */
	public static LanguageFactory getInstance() {
		if (LANGUAGE_FACTORY == null) {
			synchronized (LOCK) {
				if (LANGUAGE_FACTORY == null) {
					LANGUAGE_FACTORY = new LanguageFactory();
					LANGUAGE_MAP = getLanguageMap();
				}
			}
		}
		return LANGUAGE_FACTORY;
	}

	/**
	 * Get the language LANGUAGE_MAP
	 *
	 * @return the language LANGUAGE_MAP
	 * @throws LanguageException the language exception
	 */
	private static Map<String, Map<String, String>> getLanguageMap() {
		final Map<String, Map<String, String>> languageMap = new HashMap<>();
		createLanguage("English", languageMap);
		createLanguage("Francais", languageMap);
		return languageMap;
	}

	private static void createLanguage(final String languageName, final Map<String, Map<String, String>> languageMap) {
		final Language language = new Language("language/" + languageName + ".xml");
		languageMap.put(languageName, language.getLanguage());
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
