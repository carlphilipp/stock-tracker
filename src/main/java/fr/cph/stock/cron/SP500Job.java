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

import fr.cph.stock.exception.YahooException;
import fr.cph.stock.service.IndexService;
import fr.cph.stock.util.Constants;
import lombok.extern.log4j.Log4j2;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

/**
 * Job that try to update DB with today's s&p500 value
 *
 * @author Carl-Philipp Harmant
 * @version 1
 */
@Log4j2
public class SP500Job implements Job {

	private IndexService indexService;

	/**
	 * Constructor
	 */
	public SP500Job() {
	}

	@Override
	public final void execute(final JobExecutionContext context) {
		try {
			log.info("S&P500 Job");
			indexService.updateIndex(Constants.SP_500);
		} catch (final YahooException e) {
			log.warn("Error while executing SP500Job: {}", e.getMessage());
		} catch (final Throwable t) {
			log.error("Error while executing SP500Job: {}", t.getMessage(), t);
		}
	}
}
