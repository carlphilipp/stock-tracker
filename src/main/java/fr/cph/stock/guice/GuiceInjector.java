package fr.cph.stock.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import fr.cph.stock.business.*;
import fr.cph.stock.business.impl.ShareValueBusinessImpl;

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

	public FollowBusiness getFollowBusiness() {
		return injector.getInstance(FollowBusiness.class);
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
}
