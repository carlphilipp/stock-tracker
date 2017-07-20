/**
 * Copyright 2017 Carl-Philipp Harmant
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Component
@Singleton
public class EquityBusinessImpl implements EquityBusiness {

	@NonNull
	private CompanyBusiness companyBusiness;
	@NonNull
	private CompanyDAO companyDAO;
	@NonNull
	private EquityDAO equityDAO;
	@NonNull
	private PortfolioDAO portfolioDAO;

/*	@Inject
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
	}*/

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
