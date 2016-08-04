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

package fr.cph.stock.external;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import fr.cph.stock.business.Business;
import fr.cph.stock.business.IBusiness;
import fr.cph.stock.entities.CurrencyData;
import fr.cph.stock.entities.Portfolio;
import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.exception.YahooException;
import fr.cph.stock.util.Info;

public class YahooTest {

	@Test
	public void testUpdateCurrency() throws YahooException {
		IExternalDataAccess data = new YahooExternalDataAccess();
		Currency currency = Currency.EUR;
		List<CurrencyData> currenciesData = data.getCurrencyData(currency);
		//assertEquals(4, currenciesData.size());
	}
}
