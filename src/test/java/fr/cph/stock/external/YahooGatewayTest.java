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

package fr.cph.stock.external;

import fr.cph.stock.entities.CurrencyData;
import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.exception.YahooException;
import org.junit.Test;

import java.util.List;

public class YahooGatewayTest {

	@Test
	public void testUpdateCurrency() throws YahooException {
		IExternalDataAccess data = new YahooExternalDataAccess();
		Currency currency = Currency.EUR;
		List<CurrencyData> currenciesData = data.getCurrencyData(currency);
		//assertEquals(4, currenciesData.size());
	}
}
