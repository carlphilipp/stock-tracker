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

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import fr.cph.stock.business.Business;
import fr.cph.stock.business.IBusiness;

public class CompanyNotRealTimeJob implements Job {

	private static final Logger log = Logger.getLogger(CompanyNotRealTimeJob.class);
	private IBusiness business;

	public CompanyNotRealTimeJob() {
		business = new Business();
	}

	@Override
	public void execute(JobExecutionContext context) {
		try {
			business.updateCompaniesNotRealTime();
		} catch (Throwable t) {
			log.error("Error while executing CompanyNotRealTimeJob: " + t.getMessage(), t);
		}
	}

}
