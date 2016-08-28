package fr.cph.stock.business.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import fr.cph.stock.business.CompanyBusiness;
import fr.cph.stock.business.EquityBusiness;
import fr.cph.stock.dao.CompanyDAO;
import fr.cph.stock.dao.DAO;
import fr.cph.stock.dao.EquityDAO;
import fr.cph.stock.dao.PortfolioDAO;
import fr.cph.stock.entities.Company;
import fr.cph.stock.entities.Equity;
import fr.cph.stock.entities.Portfolio;
import fr.cph.stock.exception.EquityException;
import fr.cph.stock.exception.NotFoundException;
import fr.cph.stock.exception.YahooException;

import java.util.Optional;

@Singleton
public class EquityBusinessImpl implements EquityBusiness {

	@Inject
	private CompanyBusiness companyBusiness;
	private CompanyDAO companyDAO;
	private EquityDAO equityDAO;
	private PortfolioDAO portfolioDAO;

	@Inject
	public void setCurrencyDAO(@Named("Company") final DAO dao) {
		companyDAO = (CompanyDAO) dao;
	}

	@Inject
	public void setEquityDAO(@Named("Equity") final DAO dao) {
		equityDAO = (EquityDAO) dao;
	}

	@Inject
	public void setPortfolioDAO(@Named("Portfolio") final DAO dao) {
		portfolioDAO = (PortfolioDAO) dao;
	}

	@Override
	public final void createEquity(final int userId, final String ticker, final Equity equity) throws EquityException, YahooException {
		final Company company = companyBusiness.addOrUpdateCompany(ticker).orElseThrow(() -> new NotFoundException(ticker));
		final Portfolio portfolio = portfolioDAO.selectPortfolioFromUserIdWithEquities(userId).orElseThrow(() -> new NotFoundException(userId));
		createEquity(portfolio, company, equity);
	}

	@Override
	public final void createManualEquity(final int userId, final Company company, final Equity equity) throws EquityException {
		final Portfolio portfolio = portfolioDAO.selectPortfolioFromUserIdWithEquities(userId).orElseThrow(() -> new NotFoundException(userId));
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
			equityDAO.insert(equity);
		}
	}

	@Override
	public final void updateEquity(final int userId, final String ticker, final Equity equity) throws YahooException {
		final Company company = companyDAO.selectWithYahooId(ticker).orElseThrow(() -> new NotFoundException(ticker));
		final Portfolio portfolio = portfolioDAO.selectPortfolioFromUserIdWithEquities(userId).orElseThrow(() -> new NotFoundException(userId));

		final Optional<Equity> found = portfolio.getEquities().stream()
			.filter(e -> e.getCompanyId() == company.getId())
			.findAny();
		equity.setCompanyId(company.getId());
		equity.setPortfolioId(portfolio.getId());
		if (found.isPresent()) {
			equity.setId(found.get().getId());
			equityDAO.update(equity);
		} else {
			equityDAO.insert(equity);
		}
	}

	@Override
	public final void deleteEquity(final Equity equity) {
		equityDAO.delete(equity);
	}
}
