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

package fr.cph.stock.cron;

import fr.cph.stock.dropbox.DropBox;
import fr.cph.stock.util.MySQLDumper;
import fr.cph.stock.util.Util;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.io.File;

/**
 * Job that save DB to dropbox
 *
 * @author Carl-Philipp Harmant
 * @version 1
 */
public class MysqlDumpJob implements Job {

	/** Logger **/
	private static final Logger LOG = Logger.getLogger(MysqlDumpJob.class);

	@Override
	public final void execute(final JobExecutionContext context) {
		File tarGzFile = null;
		File sqlFile = null;
		try {
			final MySQLDumper mysql = new MySQLDumper(Util.getCurrentDateInFormat("dd-MM-yyyy"));
			mysql.export();

			final String sqlPath = mysql.getCurrentSqlNameFile();
			sqlFile = new File(sqlPath);
			final String tarGzPath = mysql.getCurrentTarGzNameFile();
			tarGzFile = new File(tarGzPath);

			Util.createTarGz(sqlPath, tarGzPath);

			final DropBox dropBox = new DropBox();
			dropBox.deleteOldFileIfNeeded(tarGzFile);
			dropBox.uploadFile(tarGzFile);
		} catch (final Throwable t) {
			LOG.error("Error while executing MysqlDumpJob: " + t.getMessage(), t);
		} finally {
			if (tarGzFile != null && tarGzFile.exists()) {
				boolean del = tarGzFile.delete();
				if (!del) {
					LOG.error("Error while deleting the tar file: " + tarGzFile.getAbsolutePath());
				}
			}
			if (sqlFile != null && sqlFile.exists()) {
				boolean del = sqlFile.delete();
				if (!del) {
					LOG.error("Error while deleting the tar file: " + sqlFile.getAbsolutePath());
				}
			}
		}
	}
}
