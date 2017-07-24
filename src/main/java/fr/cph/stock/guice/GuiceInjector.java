package fr.cph.stock.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import fr.cph.stock.business.*;
import fr.cph.stock.business.impl.ShareValueBusinessImpl;
import fr.cph.stock.dropbox.DropBox;
import fr.cph.stock.security.SecurityService;

public enum GuiceInjector {

	INSTANCE;

	private final Injector injector;

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

	public IndexBusiness getIndexBusiness() {
		return injector.getInstance(IndexBusiness.class);
	}

	public ShareValueBusiness getShareValueBusiness() {
		return injector.getInstance(ShareValueBusinessImpl.class);
	}

	public UserBusiness getUserBusiness() {
		return injector.getInstance(UserBusiness.class);
	}

	public SecurityService getSecurityService() {
		return injector.getInstance(SecurityService.class);
	}

	public DropBox getDropBox() {
		return injector.getInstance(DropBox.class);
	}
}
