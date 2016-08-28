package fr.cph.stock.business.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import fr.cph.stock.business.CompanyBusiness;
import fr.cph.stock.business.FollowBusiness;
import fr.cph.stock.dao.CompanyDAO;
import fr.cph.stock.dao.DAO;
import fr.cph.stock.dao.FollowDAO;
import fr.cph.stock.entities.Company;
import fr.cph.stock.entities.Follow;
import fr.cph.stock.entities.User;
import fr.cph.stock.exception.NotFoundException;
import fr.cph.stock.exception.YahooException;

import java.util.List;
import java.util.Optional;

@Singleton
public class FollowBusinessImpl implements FollowBusiness {

	private CompanyBusiness companyBusiness;
	private FollowDAO followDAO;
	private CompanyDAO companyDAO;

	@Inject
	public FollowBusinessImpl(final CompanyBusiness companyBusiness, @Named("Follow") final DAO followDAO, @Named("Company") final DAO companyDAO) {
		this.companyBusiness = companyBusiness;
		this.followDAO = (FollowDAO) followDAO;
		this.companyDAO = (CompanyDAO) companyDAO;
	}

	@Override
	public final void addFollow(final User user, final String ticker, final Double lower, final Double higher) throws YahooException {
		final Company company = companyBusiness.addOrUpdateCompany(ticker).orElseThrow(() -> new NotFoundException(ticker));
		final Optional<Follow> followOptional = followDAO.selectOneFollow(user.getId(), company.getId());
		if (followOptional.isPresent()) {
			followOptional.get().setLowerLimit(lower);
			followOptional.get().setHigherLimit(higher);
			followDAO.update(followOptional.get());
		} else {
			final Follow follow = new Follow();
			follow.setCompany(company);
			follow.setCompanyId(company.getId());
			follow.setUserId(user.getId());
			follow.setLowerLimit(lower);
			follow.setHigherLimit(higher);
			followDAO.insert(follow);
		}
	}

	@Override
	public final void updateFollow(final User user, final String ticker, final Double lower, final Double higher) {
		final Company company = companyDAO.selectWithYahooId(ticker).orElseThrow(() -> new NotFoundException(ticker));
		final Follow foll = followDAO.selectOneFollow(user.getId(), company.getId()).orElseThrow(() -> new NotFoundException(user.getId()));
		foll.setLowerLimit(lower);
		foll.setHigherLimit(higher);
		followDAO.update(foll);
	}

	@Override
	public final void deleteFollow(final int id) {
		final Follow follow = new Follow();
		follow.setId(id);
		followDAO.delete(follow);
	}

	@Override
	public final List<Follow> getListFollow(final int userId) {
		return followDAO.selectListFollow(userId);
	}
}
