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
import fr.cph.stock.exception.YahooException;

/**
 * Job that update currencies
 * 
 * @author Carl-Philipp Harmant
 * @version 1
 */
public class CurrencyJob implements Job {

	private static final Logger log = Logger.getLogger(CurrencyJob.class);
	private IBusiness business;

	public CurrencyJob() {
		business = new Business();
	}

	@Override
	public void execute(JobExecutionContext context) {
		try {
			business.updateAllCurrencies();
		} catch (YahooException e) {
			log.warn("Error while executing CurrencyJob: " + e.getMessage());
		} catch (Throwable t) {
			log.error("Error while executing CurrencyJob: " + t.getMessage(), t);
		}
	}
	

	public static void main(String[] args) {
		CurrencyJob m = new CurrencyJob();
		m.execute(null);
	}
}
