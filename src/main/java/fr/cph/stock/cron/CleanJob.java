/**
 * Copyright 2013 Carl-Philipp Harmant
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
import fr.cph.stock.business.impl.CompanyBusinessImpl;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

/**
 * Job that clean companies unused in DB
 *
 * @author Carl-Philipp Harmant
 * @version 1
 */
public class CleanJob implements Job {

	private static final Logger LOG = Logger.getLogger(CleanJob.class);
	private CompanyBusiness companyBusiness;

	/** Constructor  **/
	public CleanJob() {
		companyBusiness = CompanyBusinessImpl.INSTANCE;
	}

	@Override
	public final void execute(final JobExecutionContext context) {
		try {
			companyBusiness.cleanDB();
		} catch (final Throwable t) {
			LOG.error("Error while executing CleanJob: " + t.getMessage(), t);
		}
	}
}
