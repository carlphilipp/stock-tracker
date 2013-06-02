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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import com.dropbox.client2.exception.DropboxException;

import fr.cph.stock.dropbox.DropBox;
import fr.cph.stock.util.MySQLDumper;
import fr.cph.stock.util.Util;

/**
 * Job that save DB to dropbox
 * 
 * @author Carl-Philipp Harmant
 * @version 1
 */
public class MysqlDumpJob implements Job {

	private static final Logger log = Logger.getLogger(MysqlDumpJob.class);

	@Override
	public void execute(JobExecutionContext context) {
		File tarGzFile = null;
		File sqlFile = null;
		try {
			MySQLDumper mysql = new MySQLDumper(Util.getCurrentDateInFormat("dd-MM-yyyy"));
			mysql.export();

			String sqlPath = mysql.getCurrentSqlNameFile();
			sqlFile = new File(sqlPath);
			String tarGzPath = mysql.getCurrentTarGzNameFile();
			tarGzFile = new File(tarGzPath);

			Util.createTarGz(sqlPath, tarGzPath);

			DropBox dropBox = new DropBox();
			dropBox.deleteOldFileIfNeeded(tarGzFile);
			dropBox.uploadFile(tarGzFile);

		} catch (FileNotFoundException fe) {
			log.error("Error while executing MysqlDumpJob: " + fe.getMessage(), fe);
		} catch (DropboxException | IOException e) {
			log.error("Error while executing MysqlDumpJob: " + e.getMessage(), e);
		} catch (Throwable t) {
			log.error("Error while executing MysqlDumpJob: " + t.getMessage(), t);
		} finally {
			if(tarGzFile != null){
				if(tarGzFile.exists()){
					tarGzFile.delete();
				}
			}
			if(sqlFile != null){
				if(sqlFile.exists()){
					sqlFile.delete();
				}
			}
		}
	}

	public static void main(String[] args) {
		MysqlDumpJob mysql = new MysqlDumpJob();
		mysql.execute(null);
	}
}
