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

import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import fr.cph.stock.business.Business;
import fr.cph.stock.business.IBusiness;
import fr.cph.stock.exception.YahooException;
import fr.cph.stock.util.Info;

/**
 * Job that check if cac40 today's value has been added to DB. If it's not the case, it will update it if possible
 * 
 * @author Carl-Philipp Harmant
 * @version 1
 */
public class Cac40CheckJob implements Job {

	/** Logger **/
	private static final Logger LOG = Logger.getLogger(Cac40CheckJob.class);
	/** Business **/
	private IBusiness business;

	/**
	 * Constructor
	 */
	public Cac40CheckJob() {
		business = Business.getInstance();
	}

	@Override
	public final void execute(final JobExecutionContext context) {
		try {
			LOG.debug("Cac40 Check job running");
			TimeZone timeZone = TimeZone.getTimeZone("Europe/Paris");
			business.checkUpdateIndex(Info.YAHOOID_CAC40, timeZone);
		} catch (YahooException e) {
			LOG.warn("Error while executing Cac40CheckJob: " + e.getMessage());
		} catch (Throwable t) {
			LOG.error("Error while executing Cac40CheckJob: " + t.getMessage(), t);
		}
	}
}
