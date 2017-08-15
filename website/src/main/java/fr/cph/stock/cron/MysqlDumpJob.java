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

package fr.cph.stock.cron;

import fr.cph.stock.dropbox.DropBox;
import fr.cph.stock.util.MySQLDumper;
import fr.cph.stock.util.Util;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * Job that save DB to dropbox
 *
 * @author Carl-Philipp Harmant
 * @version 1
 */
@Profile("prod")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
@Log4j2
public class MysqlDumpJob {

	private static final String PATH = "stock-tracker";
	private static final String SQL_EXT = ".sql";
	private static final String TARGZ_EXT = ".tar.gz";

	@NonNull
	private MySQLDumper mySQLDumper;

	@NonNull
	private DropBox dropBox;

	@Scheduled(cron = "0 30 3 ? * *", zone = "America/Chicago")
	public void execute() {
		File tarGzFile = null;
		File sqlFile = null;
		try {
			log.info("Exporting MYSQL db");
			final String currentFileBase = currentFileBase();
			mySQLDumper.export(currentFileBase);

			sqlFile = new File(currentFileBase);
			final String tarGzPath = currentFileBase + TARGZ_EXT;
			tarGzFile = new File(tarGzPath);

			Util.createTarGz(currentFileBase, tarGzPath);

			log.info("Delete old file in Dropbox if needed");
			dropBox.deleteOldFileIfNeeded(tarGzFile);

			log.info("Upload new dump in Dropbox");
			dropBox.uploadFile(tarGzFile);

			log.info("Done!");
		} catch (final Throwable t) {
			log.error("Error while executing MysqlDumpJob for file [{}], {}", tarGzFile == null ? null : tarGzFile.getName(), t.getMessage(), t);
		} finally {
			if (tarGzFile != null && tarGzFile.exists()) {
				boolean del = tarGzFile.delete();
				if (!del) {
					log.error("Error while deleting the tar file: {}", tarGzFile.getAbsolutePath());
				}
			}
			if (sqlFile != null && sqlFile.exists()) {
				boolean del = sqlFile.delete();
				if (!del) {
					log.error("Error while deleting the tar file: {}", sqlFile.getAbsolutePath());
				}
			}
		}
	}

	private String currentFileBase() {
		return Util.getCurrentDateInFormat("dd-MM-yyyy") + "-" + PATH + SQL_EXT;
	}
}
