package fr.cph.stock.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import fr.cph.stock.business.AccountBusiness;
import fr.cph.stock.business.CompanyBusiness;
import fr.cph.stock.business.CurrencyBusiness;
import fr.cph.stock.business.EquityBusiness;

public enum GuiceInjector {

	INSTANCE;

	private Injector injector;

	GuiceInjector() {
		injector = Guice.createInjector(new GuiceModule());
	}

	public CompanyBusiness getCompanyBusiness() {
		return injector.getInstance(CompanyBusiness.class);
	}

	public AccountBusiness getAccountBusiness() {
		return injector.getInstance(AccountBusiness.class);
	}

	public CurrencyBusiness getCurrencyBusiness() {
		return injector.getInstance(CurrencyBusiness.class);
	}

	public EquityBusiness getEquityBusiness() {
		return injector.getInstance(EquityBusiness.class);
	}
}
