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
import fr.cph.stock.guice.GuiceInjector;
import fr.cph.stock.util.Info;
import lombok.extern.log4j.Log4j2;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

/**
 * Job that try to update DB with today's cac40 value
 *
 * @author Carl-Philipp Harmant
 * @version 1
 */
@Log4j2
public class Cac40Job implements Job {

	/**
	 * AccountBusinessImpl
	 **/
	private IndexBusiness indexBusiness;

	/**
	 * Constructor
	 */
	public Cac40Job() {
		indexBusiness = GuiceInjector.INSTANCE.getIndexBusiness();
	}

	@Override
	public final void execute(final JobExecutionContext context) {
		try {
			log.info("CAC40 Job");
			indexBusiness.updateIndex(Info.YAHOO_ID_CAC40);
		} catch (final YahooException e) {
			log.warn("Error while executing Cac40Job: {}", e.getMessage());
		} catch (final Throwable t) {
			log.error("Error while executing Cac40Job: {}", t.getMessage(), t);
		}
	}
}
