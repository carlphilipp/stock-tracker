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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * This class dump the mysql database
 * 
 * @author Carl-Philipp Harmant
 * 
 */
public class MySQLDumper {

	/** Logger **/
	private static final Logger LOG = Logger.getLogger(MySQLDumper.class);
	/** Ip **/
	private String ip;
	// private static String port = "3306";
	/** Database **/
	private String database;
	/** Database user **/
	private String user;
	/** Database password **/
	private String pass;
	/** Path **/
	private static final String PATH = "stock";
	/** Extension of the file **/
	private static final String SQLEXT = ".sql";
	/** Compression of the file **/
	private static final String TARGZEXT = ".tar.gz";
	/** Date **/
	private String date;

	/**
	 * Constructor
	 * 
	 * @param d
	 *            the date
	 */
	public MySQLDumper(final String d) {
		this.date = d;
		Properties prop = Util.getProperties("app.properties");
		ip = prop.getProperty("db.ip");
		database = prop.getProperty("db.name");
		user = prop.getProperty("db.user");
		pass = prop.getProperty("db.password");
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
	 * @throws Exception
	 *             the exception
	 */
	public final void export() throws Exception {
		String dumpCommand = "mysqldump " + database + " -h " + ip + " -u " + user + " -p" + pass;
		Runtime rt = Runtime.getRuntime();
		PrintStream ps;
		Process child = rt.exec(dumpCommand);
		try {
			ps = new PrintStream(date + "-" + PATH + SQLEXT, "UTF-8");
		} catch (FileNotFoundException fileEx) {
			File file = new File("");
			throw new FileNotFoundException(fileEx.getMessage() + " / " + file.getPath());
		}
		try {
			InputStream in = child.getInputStream();
			int ch;
			while ((ch = in.read()) != -1) {
				ps.write(ch);
			}
			InputStream err = child.getErrorStream();
			while ((ch = err.read()) != -1) {
				LOG.error(ch);
			}
			ps.close();
		} catch (Exception exc) {
			throw new Exception(exc.getMessage(), exc);
		}
	}

}