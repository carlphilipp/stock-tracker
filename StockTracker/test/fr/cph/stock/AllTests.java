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
