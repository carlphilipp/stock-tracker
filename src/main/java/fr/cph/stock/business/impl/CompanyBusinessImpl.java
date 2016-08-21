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
import fr.cph.stock.exception.YahooException;
import fr.cph.stock.exception.YahooUnknownTickerException;
import fr.cph.stock.external.IExternalDataAccess;
import fr.cph.stock.util.Info;
import fr.cph.stock.util.Mail;
import fr.cph.stock.util.Util;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class CompanyBusinessImpl implements CompanyBusiness {

	private static final Logger LOG = Logger.getLogger(CompanyBusinessImpl.class);
	private static final int MAX_UPDATE_COMPANY = 15;
	private static final int PAUSE = 1000;

	private final CompanyDAO companyDAO;
	private final IExternalDataAccess yahoo;

	@Inject
	public CompanyBusinessImpl(final IExternalDataAccess yahoo, @Named("Company") final DAO dao) {
		this.yahoo = yahoo;
		companyDAO = (CompanyDAO) dao;
	}

	@Override
	public final List<Company> addOrUpdateCompanies(final List<String> tickers) throws YahooException {
		LOG.debug("Updating: " + tickers);
		final List<Company> companies = yahoo.getCompaniesData(tickers);
		final List<Company> companiesResult = new ArrayList<>();
		for (Company companyYahoo : companies) {
			Company companyInDB = companyDAO.selectWithYahooId(companyYahoo.getYahooId());
			if (companyInDB == null) {
				companyYahoo = yahoo.getCompanyInfo(companyYahoo);
				companyDAO.insert(companyYahoo);
				companyInDB = companyDAO.selectWithYahooId(companyYahoo.getYahooId());
			} else {
				companyInDB.setQuote(companyYahoo.getQuote());
				companyInDB.setYield(companyYahoo.getYield());
				companyInDB.setName(companyYahoo.getName());
				// companyInDB.setCurrency(Market.getCurrency(companyYahoo.getMarket()));
				companyInDB.setCurrency(companyYahoo.getCurrency());
				companyInDB.setMarketCapitalization(companyYahoo.getMarketCapitalization());
				companyInDB.setMarket(companyYahoo.getMarket());
				companyInDB.setYearHigh(companyYahoo.getYearHigh());
				companyInDB.setYearLow(companyYahoo.getYearLow());
				companyInDB.setYesterdayClose(companyYahoo.getYesterdayClose());
				companyInDB.setChangeInPercent(companyYahoo.getChangeInPercent());
				companyDAO.update(companyInDB);
			}
			companiesResult.add(companyInDB);
		}
		return companiesResult;
	}

	@Override
	public final void updateCompaniesNotRealTime() {
		final List<Company> companies = companyDAO.selectAllCompany(false);
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -7);
		try {
			for (final Company company : companies) {
				final List<Company> data = yahoo.getCompanyDataHistory(company.getYahooId(), cal.getTime(), null);
				if (data.size() != 0) {
					final Company temp = data.get(0);
					company.setQuote(temp.getQuote());
					companyDAO.update(company);
				}
			}
		} catch (final YahooException e) {
			LOG.warn("Company update not real time error: " + e.getMessage());
		}
	}

	@Override
	public final void deleteCompany(final Company company) {
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
	public final Company createManualCompany(final String name, final String industry, final String sector, final Currency currency, final double quote) {
		final Company company = new Company();
		final String uuid = UUID.randomUUID().toString();
		company.setYahooId(uuid);
		company.setName(name);
		company.setCurrency(currency);
		company.setIndustry(industry);
		company.setQuote(quote);
		company.setSector(sector);
		company.setManual(true);
		company.setRealTime(false);
		company.setFund(false);
		companyDAO.insert(company);
		return companyDAO.selectWithYahooId(uuid);
	}

	@Override
	public void updateCompanyManual(final Integer companyId, final Double newQuote) {
		final Company company = companyDAO.select(companyId);
		company.setQuote(newQuote);
		companyDAO.update(company);
	}

	public boolean updateAllCompanies() {
		final List<Company> companies = companyDAO.selectAllCompany(true);
		final List<String> yahooIdList = companies.stream()
			.filter(Company::getRealTime)
			.map(Company::getYahooId)
			.collect(Collectors.toList());
		boolean canUpdate = true;
		if (yahooIdList.size() <= MAX_UPDATE_COMPANY) {
			try {
				addOrUpdateCompanies(yahooIdList);
			} catch (final YahooUnknownTickerException e) {
				LOG.warn(e.getMessage());
				Mail.sendMail("[Error] " + Info.NAME, e.getMessage(), Info.ADMINS.toArray(new String[Info.ADMINS.size()]), null);
			} catch (final YahooException e) {
				canUpdate = false;
				LOG.warn("All companies update failed: " + e.getMessage());
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
					LOG.warn(e.getMessage());
					Mail.sendMail("[Error] " + Info.NAME, e.getMessage(), Info.ADMINS.toArray(new String[Info.ADMINS.size()]), null);
				} catch (final YahooException e) {
					canUpdate = false;
					isOk = false;
					LOG.warn("All companies update failed: " + e.getMessage() + " | Issue trying to update at limit [" + from + ", " + to + "]");
				}
				if (to == yahooIdList.size()) {
					isOk = false;
				}
				from = to;
				to = to + MAX_UPDATE_COMPANY;
			}
		}
		return canUpdate;
	}

	@Override
	public Company addOrUpdateCompany(final String ticker) throws YahooException {
		final List<String> tickers = Collections.singletonList(ticker);
		Company companyYahoo = yahoo.getCompaniesData(tickers).get(0);
		Company companyInDB = companyDAO.selectWithYahooId(companyYahoo.getYahooId());
		if (companyInDB == null) {
			companyYahoo = yahoo.getCompanyInfo(companyYahoo);
			companyDAO.insert(companyYahoo);
			companyInDB = companyDAO.selectWithYahooId(companyYahoo.getYahooId());
		} else {
			companyInDB.setQuote(companyYahoo.getQuote());
			companyInDB.setYield(companyYahoo.getYield());
			companyInDB.setName(companyYahoo.getName());
			companyInDB.setCurrency(Market.getCurrency(companyYahoo.getMarket()));
			companyInDB.setMarketCapitalization(companyYahoo.getMarketCapitalization());
			companyInDB.setMarket(companyYahoo.getMarket());
			companyInDB.setYearHigh(companyYahoo.getYearHigh());
			companyInDB.setYearLow(companyYahoo.getYearLow());
			companyInDB.setYesterdayClose(companyYahoo.getYesterdayClose());
			companyDAO.update(companyInDB);
		}
		return companyInDB;
	}

	@Override
	public final void cleanDB() {
		final List<Integer> companies = companyDAO.selectAllUnusedCompanyIds();
		companies.forEach(id -> {
			final Company company = new Company();
			company.setId(id);
			companyDAO.delete(company);
		});
	}
}
