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

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

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
	public void testGetCompanyData() throws UnsupportedEncodingException, YahooException {
//		IExternalDataAccess data = new YahooExternalDataAccess();
//		Company company = data.getCompanyData("FP.PA");
//		Assert.assertEquals("FP.PA", company.getYahooId());
//		Assert.assertEquals("Major Integrated Oil & Gas", company.getIndustry());
//		Assert.assertEquals("Basic Materials", company.getSector());
//		Assert.assertEquals("Total", company.getName());
	}

	@Test
	public void testUpdateCurrency() throws YahooException {
		IExternalDataAccess data = new YahooExternalDataAccess();
		Currency currency = Currency.EUR;
		List<CurrencyData> currenciesData = data.getCurrencyData(currency);
		Assert.assertEquals(4, currenciesData.size());
	}

	@Test
	public void testGetCompaniesData() throws YahooException {
		IBusiness business = new Business();
		Portfolio portfolio = business.getUserPortfolio(126, null, null);
		Date from = portfolio.getShareValues().get(portfolio.getShareValues().size() - 1).getDate();
		business.updateIndex(Info.YAHOOID_CAC40, from, null, false);
	}

}
