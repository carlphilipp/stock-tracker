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

import java.util.Calendar;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import fr.cph.stock.business.Business;
import fr.cph.stock.business.IBusiness;
import fr.cph.stock.exception.YahooException;

/**
 * Job that update user's share value
 * 
 * @author Carl-Philipp Harmant
 * @version 1
 */
public class UserJob implements Job {

	/** Logger **/
	private static final Logger LOG = Logger.getLogger(UserJob.class);
	/** **/
	private IBusiness business;

	/** Constructor **/
	public UserJob() {
		business = Business.getInstance();
	}

	@Override
	public final void execute(final JobExecutionContext context) {
		try {
			LOG.info("User job");
			TimeZone timeZone = TimeZone.getTimeZone("Europe/Paris");
			Calendar cal = Calendar.getInstance(timeZone);
			business.autoUpdateUserShareValue(cal);
		} catch (YahooException e) {
			LOG.error("Error while executing UserJob: " + e.getMessage());
		} catch (Throwable t) {
			LOG.error("Error while executing UserJob: " + t.getMessage(), t);
		}
	}

}
