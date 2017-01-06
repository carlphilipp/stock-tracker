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
import fr.cph.stock.dao.CompanyDAO;
import fr.cph.stock.dao.DAO;
import fr.cph.stock.entities.Company;
import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.enumtype.Market;
import fr.cph.stock.exception.NotFoundException;
import fr.cph.stock.exception.YahooException;
import fr.cph.stock.exception.YahooUnknownTickerException;
import fr.cph.stock.external.ExternalDataAccess;
import fr.cph.stock.util.Info;
import fr.cph.stock.util.Mail;
import fr.cph.stock.util.Util;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
@Singleton
public class CompanyBusinessImpl implements CompanyBusiness {

	private static final int MAX_UPDATE_COMPANY = 15;
	private static final int PAUSE = 1000;

	@NonNull
	private ExternalDataAccess yahoo;
	@NonNull
	private CompanyDAO companyDAO;

	@Inject
	public CompanyBusinessImpl(@Named("Company") final DAO dao, final ExternalDataAccess yahoo) {
		this.companyDAO = (CompanyDAO) dao;
		this.yahoo = yahoo;
	}

	@Override
	public void updateCompaniesNotRealTime() {
		final List<Company> companies = companyDAO.selectAllCompany(false);
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -7);
		try {
			companies.forEach(company -> yahoo.getCompanyDataHistory(company.getYahooId(), cal.getTime(), null)
				.findFirst()
				.ifPresent(c -> {
					company.setQuote(c.getQuote());
					companyDAO.update(company);
				}));
		} catch (final YahooException e) {
			log.warn("Company update not real time error: {}", e.getMessage());
		}
	}

	@Override
	public void deleteCompany(final Company company) {
		companyDAO.delete(company);
	}

	@Override
	public final String addOrUpdateCompaniesLimitedRequest(final List<String> companiesYahooIdRealTime) throws YahooException {
		final StringBuilder sb = new StringBuilder();
		if (companiesYahooIdRealTime.size() <= MAX_UPDATE_COMPANY) {
			try {
				addOrUpdateCompanies(companiesYahooIdRealTime);
			} catch (final YahooUnknownTickerException e) {
				sb.append(e.getMessage()).append(" ");
			}
		} else {
			int from = 0;
			int to = MAX_UPDATE_COMPANY;
			boolean isOk = true;
			while (isOk) {
				if (to > companiesYahooIdRealTime.size()) {
					to = companiesYahooIdRealTime.size();
				}
				try {
					addOrUpdateCompanies(companiesYahooIdRealTime.subList(from, to));
					Util.makeAPause(PAUSE);
				} catch (final YahooUnknownTickerException e) {
					sb.append(e.getMessage()).append(" ");
				}
				if (to == companiesYahooIdRealTime.size()) {
					isOk = false;
				}
				from = to;
				to = to + MAX_UPDATE_COMPANY;
			}
		}
		return sb.toString();
	}

	@Override
	public final Optional<Company> createManualCompany(final String name, final String industry, final String sector, final Currency currency, final double quote) {
		final String uuid = UUID.randomUUID().toString();
		final Company company = Company.builder()
			.yahooId(uuid)
			.name(name)
			.currency(currency)
			.industry(industry)
			.quote(quote)
			.sector(sector)
			.manual(true)
			.realTime(false)
			.fund(false).build();
		companyDAO.insert(company);
		return companyDAO.selectWithYahooId(uuid);
	}

	@Override
	public void updateCompanyManual(final Integer companyId, final Double newQuote) {
		final Company company = companyDAO.select(companyId).orElseThrow(() -> new NotFoundException(companyId));
		company.setQuote(newQuote);
		companyDAO.update(company);
	}

	@Override
	public boolean updateAllCompanies() {
		final List<Company> companies = companyDAO.selectAllCompany(true);
		final List<String> yahooIdList = companies.stream()
			.filter(Company::getRealTime)
			.map(Company::getYahooId)
			.collect(Collectors.toList());
		boolean updateSuccess = true;
		if (yahooIdList.size() <= MAX_UPDATE_COMPANY) {
			try {
				addOrUpdateCompanies(yahooIdList);
			} catch (final YahooUnknownTickerException e) {
				log.warn(e.getMessage());
				Mail.sendMail("[Error] " + Info.NAME, e.getMessage(), Info.ADMINS.toArray(new String[Info.ADMINS.size()]), null);
			} catch (final YahooException e) {
				updateSuccess = false;
				log.warn("All companies update failed: {}", e.getMessage());
			}
		} else {
			int from = 0;
			int to = MAX_UPDATE_COMPANY;
			boolean isOk = true;
			while (isOk) {
				if (to > yahooIdList.size()) {
					to = yahooIdList.size();
				}
				try {
					addOrUpdateCompanies(yahooIdList.subList(from, to));
					Util.makeAPause(PAUSE);
				} catch (final YahooUnknownTickerException e) {
					log.warn(e.getMessage());
					Mail.sendMail("[Error] " + Info.NAME, e.getMessage(), Info.ADMINS.toArray(new String[Info.ADMINS.size()]), null);
				} catch (final YahooException e) {
					updateSuccess = false;
					isOk = false;
					log.warn("All companies update failed: {} | Issue trying to update at limit [{},{}]", e.getMessage(), from, to);
				}
				if (to == yahooIdList.size()) {
					isOk = false;
				}
				from = to;
				to = to + MAX_UPDATE_COMPANY;
			}
		}
		return updateSuccess;
	}

	@Override
	public Optional<Company> addOrUpdateCompany(final String ticker) throws YahooException {
		final List<String> tickers = Collections.singletonList(ticker);
		final Company companyYahoo = yahoo.getCompaniesData(tickers).findFirst().orElseThrow(RuntimeException::new);
		Optional<Company> companyInDB = companyDAO.selectWithYahooId(companyYahoo.getYahooId());
		if (companyInDB.isPresent()) {
			companyInDB.get().setQuote(companyYahoo.getQuote());
			companyInDB.get().setYield(companyYahoo.getYield());
			companyInDB.get().setName(companyYahoo.getName());
			companyInDB.get().setCurrency(Market.getCurrency(companyYahoo.getMarket()));
			companyInDB.get().setMarketCapitalization(companyYahoo.getMarketCapitalization());
			companyInDB.get().setMarket(companyYahoo.getMarket());
			companyInDB.get().setYearHigh(companyYahoo.getYearHigh());
			companyInDB.get().setYearLow(companyYahoo.getYearLow());
			companyInDB.get().setYesterdayClose(companyYahoo.getYesterdayClose());
			companyDAO.update(companyInDB.get());
		} else {
			companyDAO.insert(companyYahoo);
			companyInDB = companyDAO.selectWithYahooId(companyYahoo.getYahooId());
		}
		return companyInDB;
	}

	@Override
	public final void cleanDB() {
		final List<Integer> companies = companyDAO.selectAllUnusedCompanyIds();
		companies.forEach(id -> companyDAO.delete(Company.builder().id(id).build()));
	}

	void addOrUpdateCompanies(final List<String> tickers) throws YahooException {
		log.debug("Updating tickers: {}", tickers);
		final Stream<Company> companies = yahoo.getCompaniesData(tickers);
		companies.forEach(company -> {
			Optional<Company> companyInDB = companyDAO.selectWithYahooId(company.getYahooId());
			if (companyInDB.isPresent()) {
				companyInDB.get().setQuote(company.getQuote());
				companyInDB.get().setYield(company.getYield());
				companyInDB.get().setName(company.getName());
				companyInDB.get().setCurrency(company.getCurrency());
				companyInDB.get().setMarketCapitalization(company.getMarketCapitalization());
				companyInDB.get().setMarket(company.getMarket());
				companyInDB.get().setYearHigh(company.getYearHigh());
				companyInDB.get().setYearLow(company.getYearLow());
				companyInDB.get().setYesterdayClose(company.getYesterdayClose());
				companyInDB.get().setChangeInPercent(company.getChangeInPercent());
				companyDAO.update(companyInDB.get());
			} else {
				companyDAO.insert(company);
			}
		});
	}
}
