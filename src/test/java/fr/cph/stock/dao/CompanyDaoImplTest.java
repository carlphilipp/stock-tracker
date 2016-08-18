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

package fr.cph.stock.dao;

import fr.cph.stock.entities.Company;
import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.enumtype.Market;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CompanyDaoImplTest {

	@Test
	public void testCRUDCompany(){
		CompanyDAO dao = CompanyDAO.INSTANCE;

		String uuid = UUID.randomUUID().toString().substring(0, 5);

		String yahooId = uuid;
		String name = "Total SA";
		Market market = Market.PARIS;
		Currency currency = Currency.EUR;
		String industry = "Petrol super cool";
		String sector = "Petrol & gaz";
		double quote = 39.90;
		double yield = 6;
		Company company = new Company();
		company.setCurrency(currency);
		company.setIndustry(industry);
		company.setMarket(market);
		company.setName(name);
		company.setQuote(quote);
		company.setSector(sector);
		company.setYahooId(yahooId);
		company.setYield(yield);
		company.setRealTime(true);
		company.setFund(false);
		dao.insert(company);

		company = dao.selectWithYahooId(yahooId);
		assertEquals(yahooId, company.getYahooId());
		assertEquals(name, company.getName());
		assertEquals(market, company.getMarket());
		assertEquals(currency, company.getCurrency());
		assertEquals(industry, company.getIndustry());
		assertEquals(sector, company.getSector());
		assertEquals(quote, company.getQuote(), 0.00001);
		assertEquals(yield, company.getYield(), 0.00001);
		assertEquals(true, company.getRealTime());
		assertEquals(false, company.getFund());

		company.setCurrency(Currency.USD);
		dao.update(company);

		company = dao.selectWithYahooId(yahooId);
		assertEquals(Currency.USD, company.getCurrency());

		dao.delete(company);
		company = dao.selectWithYahooId(yahooId);
		assertNull(company);
	}

}
