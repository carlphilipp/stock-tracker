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

import fr.cph.stock.business.ShareValueBusiness;
import fr.cph.stock.exception.YahooException;
import fr.cph.stock.guice.GuiceInjector;
import lombok.extern.log4j.Log4j2;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Job that update user's share value
 *
 * @author Carl-Philipp Harmant
 * @version 1
 */
@Log4j2
public class UserJob implements Job {

	private final ShareValueBusiness shareValueBusiness;

	/**
	 * Constructor
	 **/
	public UserJob() {
		shareValueBusiness = GuiceInjector.INSTANCE.getShareValueBusiness();
	}

	@Override
	public final void execute(final JobExecutionContext context) {
		try {
			log.info("Executing user auto update share value job");
			TimeZone timeZone = TimeZone.getTimeZone("Europe/Paris");
			Calendar cal = Calendar.getInstance(timeZone);
			shareValueBusiness.autoUpdateUserShareValue(cal);
		} catch (final YahooException e) {
			log.error("Error while executing UserJob: {}", e.getMessage());
		} catch (final Throwable t) {
			log.error("Error while executing UserJob: {}", t.getMessage(), t);
		}
	}

}
