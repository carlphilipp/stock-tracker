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

import fr.cph.stock.service.IndexService;
import fr.cph.stock.util.Constants;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Job that update the DB with today's cac40 value
 *
 * @author Carl-Philipp Harmant
 * @version 1
 */
@Profile("prod")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
@Log4j2
public class Cac40Job {

	@NonNull
	private final IndexService indexService;

	@Scheduled(cron = "0 15 18 ? * MON-FRI", zone = "Europe/Paris")
	void execute() {
		try {
			log.info("Running CAC40 Job");
			indexService.updateIndex(Constants.CAC_40);
		} catch (final Throwable t) {
			log.error("Error while executing Cac40Job: {}", t.getMessage(), t);
		}
	}
}
