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

import fr.cph.stock.config.AppProperties;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * This class dump the mysql database
 *
 * @author Carl-Philipp Harmant
 */
@Scope("prototype")
@Component
@Log4j2
public class MySQLDumper {

	private final String ip;
	private final String database;
	private final String user;
	private final String password;

	@Autowired
	public MySQLDumper(final AppProperties appProperties) {
		this.ip = appProperties.getDb().getIp();
		this.database = appProperties.getDb().getName();
		this.user = appProperties.getDb().getUser();
		this.password = appProperties.getDb().getPassword();
	}

	/**
	 * Get the dump database and export to to local
	 *
	 * @throws Exception the exception
	 */
	public final void export(final String fileName) throws Exception {
		final String dumpCommand = "mysqldump " + database + " -h " + ip + " -u " + user + " -p" + password;
		log.info("Executing mysqldump");
		final Runtime rt = Runtime.getRuntime();
		PrintStream ps;
		final Process child = rt.exec(dumpCommand);
		try {
			ps = new PrintStream(fileName, "UTF-8");
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
		log.info("File exported to '{}'", fileName);
	}
}
