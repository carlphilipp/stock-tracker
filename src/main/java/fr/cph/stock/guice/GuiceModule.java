package fr.cph.stock.guice;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import fr.cph.stock.business.*;
import fr.cph.stock.business.impl.*;
import fr.cph.stock.dao.*;
import fr.cph.stock.dropbox.DropBox;
import fr.cph.stock.dropbox.DropBoxImpl;
import fr.cph.stock.external.ExternalDataAccess;
import fr.cph.stock.external.impl.ExternalDataAccessImpl;
import fr.cph.stock.external.YahooGateway;
import fr.cph.stock.external.impl.YahooGatewayImpl;

class GuiceModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(ExternalDataAccess.class).to(ExternalDataAccessImpl.class);

		bind(DAO.class).annotatedWith(Names.named("Account")).to(AccountDAO.class);
		bind(DAO.class).annotatedWith(Names.named("Company")).to(CompanyDAO.class);
		bind(DAO.class).annotatedWith(Names.named("Currency")).to(CurrencyDAO.class);
		bind(DAO.class).annotatedWith(Names.named("Equity")).to(EquityDAO.class);
		bind(DAO.class).annotatedWith(Names.named("Follow")).to(FollowDAO.class);
		bind(DAO.class).annotatedWith(Names.named("Index")).to(IndexDAO.class);
		bind(DAO.class).annotatedWith(Names.named("Portfolio")).to(PortfolioDAO.class);
		bind(DAO.class).annotatedWith(Names.named("ShareValue")).to(ShareValueDAO.class);
		bind(DAO.class).annotatedWith(Names.named("User")).to(UserDAO.class);

		bind(AccountBusiness.class).to(AccountBusinessImpl.class);
		bind(CompanyBusiness.class).to(CompanyBusinessImpl.class);
		bind(CurrencyBusiness.class).to(CurrencyBusinessImpl.class);
		bind(EquityBusiness.class).to(EquityBusinessImpl.class);
		bind(FollowBusiness.class).to(FollowBusinessImpl.class);
		bind(IndexBusiness.class).to(IndexBusinessImpl.class);
		bind(ShareValueBusiness.class).to(ShareValueBusinessImpl.class);
		bind(UserBusiness.class).to(UserBusinessImpl.class);

		bind(DropBox.class).to(DropBoxImpl.class);

		bind(YahooGateway.class).to(YahooGatewayImpl.class);
	}
}
