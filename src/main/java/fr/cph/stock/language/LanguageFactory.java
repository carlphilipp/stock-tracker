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
import org.apache.ibatis.io.Resources;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Build languages
 *
 * @author Carl-Philipp Harmant
 *
 */
public final class LanguageFactory {

    /** The factory **/
    private static LanguageFactory LANGUAGE_FACTORY;
    /** The result LANGUAGE_MAP **/
    private static Map<String, Map<String, String>> LANGUAGE_MAP;
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
     *             the language exception
     */
    public static LanguageFactory getInstance() {
        if (LANGUAGE_FACTORY == null) {
            LANGUAGE_FACTORY = new LanguageFactory();
            LANGUAGE_MAP = getLanguageMap();
        }
        return LANGUAGE_FACTORY;
    }

    /**
     * Get the language LANGUAGE_MAP
     *
     * @return the language LANGUAGE_MAP
     * @throws LanguageException
     *             the language exception
     */
    private static Map<String, Map<String, String>> getLanguageMap() {
        Map<String, Map<String, String>> languageMap = new HashMap<>();
        File file;
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
                languageMap.put(language.getLanguageName(), language.getLanguage());
            }
        }
        return languageMap;
    }

    /**
     * Get the current language needed
     *
     * @param language
     *            the language
     * @return a LANGUAGE_MAP
     */
    public Map<String, String> getLanguage(final String language) {
        return LANGUAGE_MAP.get(language);
    }
}
