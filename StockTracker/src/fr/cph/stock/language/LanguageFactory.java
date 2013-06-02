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

package fr.cph.stock.language;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.io.Resources;

import fr.cph.stock.exception.LanguageException;

/**
 * Build languages
 * 
 * @author Carl-Philipp Harmant
 * 
 */
public class LanguageFactory {

	/** The factory **/
	protected static LanguageFactory facto;
	/** The result map **/
	protected static Map<String, Map<String, String>> map;
	/** The file system **/
	private static FileSystem system = FileSystems.getDefault();

	/**
	 * Constructor to lock the construction of the object
	 */
	private LanguageFactory() {

	}

	/**
	 * Access to the factory from outside
	 * 
	 * @return a LanguageFactory
	 * @throws LanguageException
	 */
	public static LanguageFactory getInstance() throws LanguageException {
		if (facto == null) {
			facto = new LanguageFactory();
			map = getLanguageMap();
		}
		return facto;
	}

	/**
	 * Get the language map
	 * 
	 * @return the language map
	 * @throws LanguageException
	 */
	private static Map<String, Map<String, String>> getLanguageMap() throws LanguageException {
		Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
		File file = null;
		try {
			file = Resources.getResourceAsFile("fr" + system.getSeparator() + "cph" + system.getSeparator() + "stock"
					+ system.getSeparator() + "language" + system.getSeparator() + "xml");
		} catch (IOException e) {
			throw new LanguageException(e.getMessage(), e);
		}
		if (file.isDirectory()) {
			List<File> files = Arrays.asList(file.listFiles());
			for (File f : files) {
				String absolutPath = f.getAbsolutePath();
				String path = absolutPath.substring(
						absolutPath.indexOf("fr" + system.getSeparator() + "cph" + system.getSeparator() + "stock"
								+ system.getSeparator() + "language" + system.getSeparator() + "xml" + system.getSeparator()),
						absolutPath.length());
				Language language = new Language(path);
				map.put(language.getLanguageName(), language.getLanguage());
			}
		}
		return map;
	}

	/**
	 * Get the current language needed
	 * 
	 * @param language
	 *            the language
	 * @return a map
	 */
	public Map<String, String> getLanguage(String language) {
		return map.get(language);
	}

}
