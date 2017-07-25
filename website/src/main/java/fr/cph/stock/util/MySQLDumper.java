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

package fr.cph.stock.util;

import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Properties;

/**
 * This class dump the mysql database
 *
 * @author Carl-Philipp Harmant
 */
@Log4j2
public class MySQLDumper {

	private final String ip;
	// private static String port = "3306";
	private final String database;
	private final String user;
	private final String pass;
	private static final String PATH = "fr/cph/stock";
	private static final String SQL_EXT = ".sql";
	private static final String TARGZ_EXT = ".tar.gz";
	private final String date;

	public MySQLDumper(final String date) {
		this.date = date;
		final Properties prop = Util.getProperties();
		this.ip = prop.getProperty("db.ip");
		this.database = prop.getProperty("db.name");
		this.user = prop.getProperty("db.user");
		this.pass = prop.getProperty("db.password");
	}

	/**
	 * Get current file name
	 *
	 * @return the file name
	 */
	private String getCurrentNameFile() {
		return date + "-" + PATH;
	}

	/**
	 * Get current file name with extension
	 *
	 * @return the file name with extension
	 */
	public final String getCurrentSqlNameFile() {
		return getCurrentNameFile() + SQL_EXT;
	}

	/**
	 * Get current file name with compression
	 *
	 * @return the current file name with compression
	 */
	public final String getCurrentTarGzNameFile() {
		return getCurrentNameFile() + TARGZ_EXT;
	}

	/**
	 * Get the dump database and export to to local
	 *
	 * @throws Exception the exception
	 */
	public final void export() throws Exception {
		final String dumpCommand = "mysqldump " + database + " -h " + ip + " -u " + user + " -p" + pass;
		log.info("Executing '{}'", dumpCommand);
		final Runtime rt = Runtime.getRuntime();
		PrintStream ps;
		final Process child = rt.exec(dumpCommand);
		try {
			ps = new PrintStream(date + "-" + PATH + SQL_EXT, "UTF-8");
		} catch (final FileNotFoundException fileEx) {
			final File file = new File("");
			throw new FileNotFoundException(fileEx.getMessage() + " / " + file.getPath());
		}
		try (final InputStream in = child.getInputStream()) {
			int ch;
			while ((ch = in.read()) != -1) {
				ps.write(ch);
			}
		} catch (Throwable t) {
			log.error("Failed at exporting MYSQL db {}", t.getMessage(), t);
		}
		log.info("File exported to '{}'", getCurrentSqlNameFile());
	}
}
