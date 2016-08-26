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

import lombok.extern.log4j.Log4j2;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.TimeZone;

import static fr.cph.stock.util.Constants.CURRENCY;

/**
 * Job class that use Quartz API. It starts the different jobs needed in the application.
 *
 * @author Carl-Philipp Harmant
 * @version 1
 */
@Log4j2
public class Job {

	/**
	 * Constructor
	 **/
	public Job() {
		log.info("Running Job class");
	}

	/**
	 * Run
	 *
	 * @throws SchedulerException the exception
	 */
	public final void run() throws SchedulerException {
		final Scheduler sched = new StdSchedulerFactory().getScheduler();

		final JobDetail jobCurrency = JobBuilder.newJob(CurrencyJob.class).withIdentity("jobCurrency", CURRENCY).build();
		final Trigger triggerCurrency = TriggerBuilder
			.newTrigger()
			.withIdentity("triggerCurrency", CURRENCY)
			.withSchedule(
				//CronScheduleBuilder.cronSchedule("0 55 * ? * MON-FRI").inTimeZone(TimeZone.getTimeZone("Europe/Paris")))
				CronScheduleBuilder.cronSchedule("0 55 * ? * *").inTimeZone(TimeZone.getTimeZone("Europe/Paris")))
			.build();
		sched.scheduleJob(jobCurrency, triggerCurrency);

		final JobDetail jobCac40 = JobBuilder.newJob(Cac40Job.class).withIdentity("jobCac40", "index").build();
		final Trigger triggerCac40 = TriggerBuilder
			.newTrigger()
			.withIdentity("triggerCac40", "index")
			.withSchedule(
				//CronScheduleBuilder.cronSchedule("0 15 18 ? * MON-FRI").inTimeZone(TimeZone.getTimeZone("Europe/Paris")))
				CronScheduleBuilder.cronSchedule("0 15 18 ? * *").inTimeZone(TimeZone.getTimeZone("Europe/Paris")))
			.build();
		sched.scheduleJob(jobCac40, triggerCac40);

		final JobDetail jobCac40Check = JobBuilder.newJob(Cac40CheckJob.class).withIdentity("jobCac40Check", "index").build();
		final Trigger triggerCac40Check = TriggerBuilder
			.newTrigger()
			.withIdentity("triggerCac40Check", "index")
			.withSchedule(
						/*CronScheduleBuilder.cronSchedule("0 30 18-23 ? * MON-FRI").inTimeZone(
								TimeZone.getTimeZone("Europe/Paris"))).build();*/
				CronScheduleBuilder.cronSchedule("0 30 18-23 ? * *").inTimeZone(
					TimeZone.getTimeZone("Europe/Paris"))).build();
		sched.scheduleJob(jobCac40Check, triggerCac40Check);

		final JobDetail jobSP500 = JobBuilder.newJob(SP500Job.class).withIdentity("jobSP500", "index").build();
		final Trigger triggerSP500 = TriggerBuilder
			.newTrigger()
			.withIdentity("triggerSP500", "index")
			.withSchedule(
						/*CronScheduleBuilder.cronSchedule("0 10 17 ? * MON-FRI").inTimeZone(
								TimeZone.getTimeZone("America/New_York"))).build();*/
				CronScheduleBuilder.cronSchedule("0 10 17 ? * *").inTimeZone(
					TimeZone.getTimeZone("America/New_York"))).build();
		sched.scheduleJob(jobSP500, triggerSP500);

		final JobDetail jobSP500Check = JobBuilder.newJob(SP500CheckJob.class).withIdentity("jobSP500Check", "index").build();
		final Trigger triggerSP500Check = TriggerBuilder
			.newTrigger()
			.withIdentity("triggerSP500Check", "index")
			.withSchedule(
						/*CronScheduleBuilder.cronSchedule("0 15 18-23 ? * MON-FRI").inTimeZone(
								TimeZone.getTimeZone("America/New_York"))).build();*/
				CronScheduleBuilder.cronSchedule("0 15 18-23 ? * *").inTimeZone(
					TimeZone.getTimeZone("America/New_York"))).build();
		sched.scheduleJob(jobSP500Check, triggerSP500Check);

		final JobDetail jobCompanyNotRealTime = JobBuilder.newJob(CompanyNotRealTimeJob.class)
			.withIdentity("jobCompanyNotRealTime", "company").build();
		final Trigger triggerCompanyNotRealTime = TriggerBuilder
			.newTrigger()
			.withIdentity("triggerCompanyNotRealTime", "company")
			.withSchedule(CronScheduleBuilder.cronSchedule("0 30 1-6 ? * *").inTimeZone(TimeZone.getTimeZone("Europe/Paris")))
			.build();
		sched.scheduleJob(jobCompanyNotRealTime, triggerCompanyNotRealTime);

		final JobDetail jobClean = JobBuilder.newJob(CleanJob.class).withIdentity("jobClean", "company").build();
		final Trigger triggerClean = TriggerBuilder
			.newTrigger()
			.withIdentity("triggerClean", "company")
			.withSchedule(
				//CronScheduleBuilder.cronSchedule("0 30 0 ? * MON-FRI").inTimeZone(TimeZone.getTimeZone("Europe/Paris")))
				CronScheduleBuilder.cronSchedule("0 30 0 ? * *").inTimeZone(TimeZone.getTimeZone("Europe/Paris")))
			.build();
		sched.scheduleJob(jobClean, triggerClean);

		final JobDetail jobUser = JobBuilder.newJob(UserJob.class).withIdentity("jobUser", "user").build();
		final Trigger triggerUser = TriggerBuilder
			.newTrigger()
			.withIdentity("triggerUser", "user")
			.withSchedule(
				//CronScheduleBuilder.cronSchedule("0 0 * ? * MON-FRI").inTimeZone(TimeZone.getTimeZone("Europe/Paris")))
				CronScheduleBuilder.cronSchedule("0 0 * ? * *").inTimeZone(TimeZone.getTimeZone("Europe/Paris")))
			.build();
		sched.scheduleJob(jobUser, triggerUser);

		final JobDetail jobMysql = JobBuilder.newJob(MysqlDumpJob.class).withIdentity("jobMysql", "system").build();
		final Trigger triggerMysql = TriggerBuilder.newTrigger().withIdentity("triggerMysql", "system")
			// .withSchedule(CronScheduleBuilder.cronSchedule("0 24 14 ? * *")
			.withSchedule(CronScheduleBuilder.cronSchedule("0 30 5 ? * *").inTimeZone(TimeZone.getTimeZone("Europe/Paris")))
			.build();
		sched.scheduleJob(jobMysql, triggerMysql);

		sched.start();

/*		Seconds
		Minutes
		Hours
		Day-of-Month
		Month
		Day-of-Week
		Year (optional field)*/
	}
}
