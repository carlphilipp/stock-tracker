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

	/**
	 * Ip
	 **/
	private String ip;
	// private static String port = "3306";
	/**
	 * Database
	 **/
	private String database;
	/**
	 * Database user
	 **/
	private String user;
	/**
	 * Database password
	 **/
	private String pass;
	/**
	 * Path
	 **/
	private static final String PATH = "stock";
	/**
	 * Extension of the file
	 **/
	private static final String SQLEXT = ".sql";
	/**
	 * Compression of the file
	 **/
	private static final String TARGZEXT = ".tar.gz";
	/**
	 * Date
	 **/
	private String date;

	/**
	 * Constructor
	 *
	 * @param date the date
	 */
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
		return getCurrentNameFile() + SQLEXT;
	}

	/**
	 * Get current file name with compression
	 *
	 * @return the current file name with compression
	 */
	public final String getCurrentTarGzNameFile() {
		return getCurrentNameFile() + TARGZEXT;
	}

	/**
	 * Get the dump database and export to to local
	 *
	 * @throws Exception the exception
	 */
	public final void export() throws Exception {
		final String dumpCommand = "mysqldump " + database + " -h " + ip + " -u " + user + " -p" + pass;
		final Runtime rt = Runtime.getRuntime();
		PrintStream ps;
		final Process child = rt.exec(dumpCommand);
		try {
			ps = new PrintStream(date + "-" + PATH + SQLEXT, "UTF-8");
		} catch (final FileNotFoundException fileEx) {
			final File file = new File("");
			throw new FileNotFoundException(fileEx.getMessage() + " / " + file.getPath());
		}
		try (final InputStream in = child.getInputStream();
			 final InputStream err = child.getErrorStream()) {
			int ch;
			while ((ch = in.read()) != -1) {
				ps.write(ch);
			}
			while ((ch = err.read()) != -1) {
				log.error(ch);
			}
		}
	}
}
