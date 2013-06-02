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
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

/**
 * Job class that use Quartz API. It starts the different jobs needed in the application.
 * 
 * @author Carl-Philipp Harmant
 * @version 1
 */
public class Job {

	private static final Logger log = Logger.getLogger(Job.class);

	public Job() {
		log.info("Running Job class");
	}

	public void run() throws SchedulerException {
		Scheduler sched = new StdSchedulerFactory().getScheduler();

		JobDetail jobCurrency = JobBuilder.newJob(CurrencyJob.class).withIdentity("jobCurrency", "currency").build();
		Trigger triggerCurrency = TriggerBuilder
				.newTrigger()
				.withIdentity("triggerCurrency", "currency")
				.withSchedule(
						CronScheduleBuilder.cronSchedule("0 55 * ? * MON-FRI").inTimeZone(TimeZone.getTimeZone("Europe/Paris")))
				.build();
		sched.scheduleJob(jobCurrency, triggerCurrency);

		JobDetail jobCac40 = JobBuilder.newJob(Cac40Job.class).withIdentity("jobCac40", "index").build();
		Trigger triggerCac40 = TriggerBuilder
				.newTrigger()
				.withIdentity("triggerCac40", "index")
				.withSchedule(
						CronScheduleBuilder.cronSchedule("0 15 18 ? * MON-FRI").inTimeZone(TimeZone.getTimeZone("Europe/Paris")))
				.build();
		sched.scheduleJob(jobCac40, triggerCac40);

		JobDetail jobCac40Check = JobBuilder.newJob(Cac40CheckJob.class).withIdentity("jobCac40Check", "index").build();
		Trigger triggerCac40Check = TriggerBuilder
				.newTrigger()
				.withIdentity("triggerCac40Check", "index")
				.withSchedule(
						CronScheduleBuilder.cronSchedule("0 30 18-23 ? * MON-FRI").inTimeZone(
								TimeZone.getTimeZone("Europe/Paris"))).build();
		sched.scheduleJob(jobCac40Check, triggerCac40Check);

		JobDetail jobSP500 = JobBuilder.newJob(SP500Job.class).withIdentity("jobSP500", "index").build();
		Trigger triggerSP500 = TriggerBuilder
				.newTrigger()
				.withIdentity("triggerSP500", "index")
				.withSchedule(
						CronScheduleBuilder.cronSchedule("0 10 17 ? * MON-FRI").inTimeZone(
								TimeZone.getTimeZone("America/New_York"))).build();
		sched.scheduleJob(jobSP500, triggerSP500);

		JobDetail jobSP500Check = JobBuilder.newJob(SP500CheckJob.class).withIdentity("jobSP500Check", "index").build();
		Trigger triggerSP500Check = TriggerBuilder
				.newTrigger()
				.withIdentity("triggerSP500Check", "index")
				.withSchedule(
						CronScheduleBuilder.cronSchedule("0 15 18-23 ? * MON-FRI").inTimeZone(
								TimeZone.getTimeZone("America/New_York"))).build();
		sched.scheduleJob(jobSP500Check, triggerSP500Check);

		JobDetail jobCompanyNotRealTime = JobBuilder.newJob(CompanyNotRealTimeJob.class)
				.withIdentity("jobCompanyNotRealTime", "company").build();
		Trigger triggerCompanyNotRealTime = TriggerBuilder
				.newTrigger()
				.withIdentity("triggerCompanyNotRealTime", "company")
				.withSchedule(CronScheduleBuilder.cronSchedule("0 30 1-6 ? * *").inTimeZone(TimeZone.getTimeZone("Europe/Paris")))
				.build();
		sched.scheduleJob(jobCompanyNotRealTime, triggerCompanyNotRealTime);

		JobDetail jobClean = JobBuilder.newJob(CleanJob.class).withIdentity("jobClean", "company").build();
		Trigger triggerClean = TriggerBuilder
				.newTrigger()
				.withIdentity("triggerClean", "company")
				.withSchedule(
						CronScheduleBuilder.cronSchedule("0 30 0 ? * MON-FRI").inTimeZone(TimeZone.getTimeZone("Europe/Paris")))
				.build();
		sched.scheduleJob(jobClean, triggerClean);

		JobDetail jobUser = JobBuilder.newJob(UserJob.class).withIdentity("jobUser", "user").build();
		Trigger triggerUser = TriggerBuilder
				.newTrigger()
				.withIdentity("triggerUser", "user")
				.withSchedule(
						CronScheduleBuilder.cronSchedule("0 0 * ? * MON-FRI").inTimeZone(TimeZone.getTimeZone("Europe/Paris")))
				.build();
		sched.scheduleJob(jobUser, triggerUser);

		JobDetail jobMysql = JobBuilder.newJob(MysqlDumpJob.class).withIdentity("jobMysql", "system").build();
		Trigger triggerMysql = TriggerBuilder.newTrigger().withIdentity("triggerMysql", "system")
				// .withSchedule(CronScheduleBuilder.cronSchedule("0 24 14 ? * *")
				.withSchedule(CronScheduleBuilder.cronSchedule("0 30 5 ? * *").inTimeZone(TimeZone.getTimeZone("Europe/Paris")))
				.build();
		sched.scheduleJob(jobMysql, triggerMysql);

		sched.start();
	}
}
