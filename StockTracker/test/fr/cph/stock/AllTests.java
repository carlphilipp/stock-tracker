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

package fr.cph.stock;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import fr.cph.stock.business.BusinessTest;
import fr.cph.stock.dao.CompanyDaoImplTest;
import fr.cph.stock.dao.EquityDaoImplTest;
import fr.cph.stock.dao.PortfolioDaoImplTest;
import fr.cph.stock.dao.UserDaoImplTest;
import fr.cph.stock.entities.CompanyTest;
import fr.cph.stock.entities.UserTest;
import fr.cph.stock.external.YahooTest;

@RunWith(Suite.class)
@SuiteClasses({ CompanyDaoImplTest.class, UserDaoImplTest.class, PortfolioDaoImplTest.class, EquityDaoImplTest.class, CompanyTest.class, UserTest.class , YahooTest.class, BusinessTest.class})
public class AllTests {

}
