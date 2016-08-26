/**
 * Copyright 2016 Carl-Philipp Harmant
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

import fr.cph.stock.business.CompanyBusiness;
import lombok.extern.log4j.Log4j2;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

/**
 * Job that update companies that don't have any real time data.
 *
 * @author Carl-Philipp Harmant
 * @version 1
 */
@Log4j2
public class CompanyNotRealTimeJob implements Job {

	private CompanyBusiness business;

	/**
	 * Constructor
	 **/
	public CompanyNotRealTimeJob() {
		business = null;
	}

	@Override
	public final void execute(final JobExecutionContext context) {
		try {
			business.updateCompaniesNotRealTime();
		} catch (final Throwable t) {
			log.error("Error while executing CompanyNotRealTimeJob: {}", t.getMessage(), t);
		}
	}
}
