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

package fr.cph.stock.entities;

import java.text.ParseException;
import java.util.Calendar;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.junit.Test;

import fr.cph.stock.exception.YahooException;
import fr.cph.stock.util.Util;

public class CompanyTest {

	private static final Logger log = Logger.getLogger(CompanyTest.class);

	@Test
	public void testCompany() throws ParseException, YahooException {
		TimeZone timeZone = TimeZone.getTimeZone("America/New_York");
		Calendar currentCal = Util.getCurrentCalendarInTimeZone(timeZone);
		log.info(currentCal);
	}

}
