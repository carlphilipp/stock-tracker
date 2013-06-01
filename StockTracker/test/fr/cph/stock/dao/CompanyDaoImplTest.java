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

import java.util.UUID;

import junit.framework.Assert;

import org.junit.Test;

import fr.cph.stock.entities.Company;
import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.enumtype.Market;

public class CompanyDaoImplTest {

	@Test
	public void testCRUDCompany(){
		CompanyDaoImpl dao = new CompanyDaoImpl();
		
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
		dao.insert(company);
		
		company = dao.selectWithYahooId(yahooId);
		Assert.assertEquals(yahooId, company.getYahooId());
		Assert.assertEquals(name, company.getName());
		Assert.assertEquals(market, company.getMarket());
		Assert.assertEquals(currency, company.getCurrency());
		Assert.assertEquals(industry, company.getIndustry());
		Assert.assertEquals(sector, company.getSector());
		Assert.assertEquals(quote, company.getQuote());
		Assert.assertEquals(yield, company.getYield());
		
		company.setCurrency(Currency.USD);
		dao.update(company);
		
		company = dao.selectWithYahooId(yahooId);
		Assert.assertEquals(Currency.USD, company.getCurrency());
		
		dao.delete(company);
		company = dao.selectWithYahooId(yahooId);
		Assert.assertNull(company);
	}

}
