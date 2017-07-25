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

import fr.cph.stock.service.IndexService;
import fr.cph.stock.exception.YahooException;
import fr.cph.stock.util.AppProperty;
import fr.cph.stock.util.Constants;
import lombok.extern.log4j.Log4j2;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.util.TimeZone;

/**
 * Job that check if s&p500 today's value has been added to DB. If it's not the case, it will update it if possible
 *
 * @author Carl-Philipp Harmant
 * @version 1
 */
@Log4j2
public class SP500CheckJob implements Job {

	private IndexService indexService;

	/**
	 * Constructor
	 */
	public SP500CheckJob() {
	}

	@Override
	public final void execute(final JobExecutionContext context) {
		try {
			log.debug("SP500 Check job running");
			final TimeZone timeZone = TimeZone.getTimeZone("America/New_York");
			indexService.checkUpdateIndex(Constants.SP_500, timeZone);
		} catch (final YahooException e) {
			log.warn("Error while executing SP500CheckJob: {}", e.getMessage());
		} catch (final Throwable t) {
			log.error("Error while executing SP500CheckJob: {}", t.getMessage(), t);
		}
	}
}
