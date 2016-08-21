package fr.cph.stock.guice;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import fr.cph.stock.business.*;
import fr.cph.stock.business.impl.*;
import fr.cph.stock.dao.*;
import fr.cph.stock.external.IExternalDataAccess;
import fr.cph.stock.external.YahooExternalDataAccess;

class GuiceModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(IExternalDataAccess.class).to(YahooExternalDataAccess.class);

		bind(DAO.class).annotatedWith(Names.named("Account")).to(AccountDAO.class);
		bind(DAO.class).annotatedWith(Names.named("Company")).to(CompanyDAO.class);
		bind(DAO.class).annotatedWith(Names.named("Currency")).to(CurrencyDAO.class);
		bind(DAO.class).annotatedWith(Names.named("Equity")).to(EquityDAO.class);
		bind(DAO.class).annotatedWith(Names.named("Follow")).to(FollowDAO.class);
		bind(DAO.class).annotatedWith(Names.named("Portfolio")).to(PortfolioDAO.class);

		bind(AccountBusiness.class).to(AccountBusinessImpl.class);
		bind(CompanyBusiness.class).to(CompanyBusinessImpl.class);
		bind(CurrencyBusiness.class).to(CurrencyBusinessImpl.class);
		bind(EquityBusiness.class).to(EquityBusinessImpl.class);
		bind(FollowBusiness.class).to(FollowBusinessImpl.class);
	}
}
