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

import fr.cph.stock.business.impl.BusinessImpl;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import fr.cph.stock.business.Business;
import fr.cph.stock.exception.YahooException;
import fr.cph.stock.util.Info;

/**
 * Job that try to update DB with today's s&p500 value
 *
 * @author Carl-Philipp Harmant
 * @version 1
 */
public class SP500Job implements Job {

	/** Logger **/
	private static final Logger LOG = Logger.getLogger(SP500Job.class);
	/** **/
	private Business business;

	/**
	 * Constructor
	 */
	public SP500Job() {
		business = BusinessImpl.getInstance();
	}

	@Override
	public final void execute(final JobExecutionContext context) {
		try {
			LOG.info("S&P500 Job");
			business.updateIndex(Info.YAHOOID_SP500);
		} catch (YahooException e) {
			LOG.warn("Error while executing SP500Job: " + e.getMessage());
		} catch (Throwable t) {
			LOG.error("Error while executing SP500Job: " + t.getMessage(), t);
		}
	}
}
