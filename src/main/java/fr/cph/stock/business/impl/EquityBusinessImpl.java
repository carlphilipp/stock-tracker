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

import java.io.UnsupportedEncodingException;
import java.util.Optional;

@Singleton
public class EquityBusinessImpl implements EquityBusiness {

	private final CompanyBusiness companyBusiness;
	private final CompanyDAO companyDAO;
	private final EquityDAO equityDAO;
	private final PortfolioDAO portfolioDAO;


	@Inject
	public EquityBusinessImpl(final CompanyBusiness companyBusiness,
							  @Named("Company") final DAO companyDAO,
							  @Named("Equity") final DAO equityDAO,
							  @Named("Portfolio") final DAO portfolioDAO) {
		this.companyBusiness = companyBusiness;
		this.companyDAO = (CompanyDAO) companyDAO;
		this.equityDAO = (EquityDAO) equityDAO;
		this.portfolioDAO = (PortfolioDAO) portfolioDAO;
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
	public final void updateEquity(final int userId, final String ticker, final Equity equity) throws UnsupportedEncodingException, YahooException {
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
