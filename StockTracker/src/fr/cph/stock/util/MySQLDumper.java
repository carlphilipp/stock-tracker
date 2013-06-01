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

public class MySQLDumper {

	private static final Logger log = Logger.getLogger(MySQLDumper.class);

	private static String ip;
	// private static String port = "3306";
	private static String database;
	private static String user;
	private static String pass;
	private static final String path = "stock";
	private static final String sqlExt = ".sql";
	private static final String tarGzExt = ".tar.gz";
	public String date;

	public MySQLDumper(String date) {
		this.date = date;
		Properties prop = Util.getProperties("app.properties");
		ip = prop.getProperty("db.ip");
		database = prop.getProperty("db.name");
		user = prop.getProperty("db.user");
		pass = prop.getProperty("db.password");
	}

	private String getCurrentNameFile() {
		return date + "-" + path;
	}

	public String getCurrentSqlNameFile() {
		return getCurrentNameFile() + sqlExt;
	}

	public String getCurrentTarGzNameFile() {
		return getCurrentNameFile() + tarGzExt;
	}

	public void export() throws Exception {
		String dumpCommand = "mysqldump " + database + " -h " + ip + " -u " + user + " -p" + pass;
		Runtime rt = Runtime.getRuntime();
		PrintStream ps;
		Process child = rt.exec(dumpCommand);
		try {
			ps = new PrintStream(date + "-" + path + sqlExt);
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
				log.error(ch);
			}
			ps.close();
		} catch (Exception exc) {
			throw new Exception(exc.getMessage(), exc);
		}
	}

}