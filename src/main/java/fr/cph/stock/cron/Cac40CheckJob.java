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

import fr.cph.stock.business.IndexBusiness;
import fr.cph.stock.exception.YahooException;
import fr.cph.stock.util.Info;
import lombok.extern.log4j.Log4j2;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.util.TimeZone;

/**
 * Job that check if cac40 today's value has been added to DB. If it's not the case, it will update it if possible
 *
 * @author Carl-Philipp Harmant
 * @version 1
 */
@Log4j2
public class Cac40CheckJob implements Job {

	/**
	 * AccountBusinessImpl
	 **/
	private IndexBusiness indexBusiness;

	/**
	 * Constructor
	 */
	public Cac40CheckJob() {
	}

	@Override
	public final void execute(final JobExecutionContext context) {
		try {
			log.debug("Cac40 Check job running");
			final TimeZone timeZone = TimeZone.getTimeZone("Europe/Paris");
			indexBusiness.checkUpdateIndex(Info.YAHOO_ID_CAC40, timeZone);
		} catch (final YahooException e) {
			log.warn("Error while executing Cac40CheckJob: {}", e.getMessage());
		} catch (final Throwable t) {
			log.error("Error while executing Cac40CheckJob: {}", t.getMessage(), t);
		}
	}
}
