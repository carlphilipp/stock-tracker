package fr.cph.stock.business.impl;

import fr.cph.stock.business.CompanyBusiness;
import fr.cph.stock.business.EquityBusiness;
import fr.cph.stock.dao.CompanyDAO;
import fr.cph.stock.dao.EquityDAO;
import fr.cph.stock.dao.PortfolioDAO;
import fr.cph.stock.entities.Company;
import fr.cph.stock.entities.Equity;
import fr.cph.stock.entities.Portfolio;
import fr.cph.stock.exception.EquityException;
import fr.cph.stock.exception.YahooException;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

public enum EquityBusinessImpl implements EquityBusiness {

	INSTANCE;

	private final CompanyBusiness companyBusiness;
	private final EquityDAO daoEquity;
	private final PortfolioDAO daoPortfolio;
	private final CompanyDAO daoCompany;

	EquityBusinessImpl() {
		companyBusiness = CompanyBusinessImpl.INSTANCE;
		daoEquity = new EquityDAO();
		daoPortfolio = new PortfolioDAO();
		daoCompany = new CompanyDAO();
	}

	@Override
	public final void createEquity(final int userId, final String ticker, final Equity equity) throws EquityException, YahooException {
		final Company company = companyBusiness.addOrUpdateCompany(ticker);
		final Portfolio portfolio = daoPortfolio.selectPortfolioFromUserIdWithEquities(userId, null, null);
		createEquity(portfolio, company, equity);
	}

	@Override
	public final void createManualEquity(final int userId, final Company company, final Equity equity) throws EquityException {
		final Portfolio portfolio = daoPortfolio.selectPortfolioFromUserIdWithEquities(userId, null, null);
		createEquity(portfolio, company, equity);
	}

	private void createEquity(final Portfolio portfolio, final Company company, final Equity equity) throws EquityException {
		final Optional<Equity> found = portfolio.getEquities().stream()
			.filter(e -> e.getCompanyId() == company.getId())
			.findAny();
		if (found.isPresent()) {
			throw new EquityException(company.getName() + EquityException.ENTITY_ALREADY_RECORDED);
		} else {
			equity.setCompanyId(company.getId());
			equity.setPortfolioId(portfolio.getId());
			daoEquity.insert(equity);
		}
	}

	@Override
	public final void updateEquity(final int userId, final String ticker, final Equity equity) throws UnsupportedEncodingException, YahooException {
		final Company company = daoCompany.selectWithYahooId(ticker);
		final Portfolio portfolio = daoPortfolio.selectPortfolioFromUserIdWithEquities(userId, null, null);

		final Optional<Equity> found = portfolio.getEquities().stream()
			.filter(e -> e.getCompanyId() == company.getId())
			.findAny();
		equity.setCompanyId(company.getId());
		equity.setPortfolioId(portfolio.getId());
		if (found.isPresent()) {
			equity.setid(found.get().getId());
			daoEquity.update(equity);
		} else {
			daoEquity.insert(equity);
		}
	}

	@Override
	public final void deleteEquity(final Equity equity) {
		daoEquity.delete(equity);
	}
}
