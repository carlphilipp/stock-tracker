package fr.cph.stock.business.impl;

import fr.cph.stock.business.FollowBusiness;
import fr.cph.stock.dao.CompanyDAO;
import fr.cph.stock.dao.FollowDAO;
import fr.cph.stock.entities.Company;
import fr.cph.stock.entities.Follow;
import fr.cph.stock.entities.User;
import fr.cph.stock.exception.YahooException;

import java.util.List;

public enum FollowBusinessImpl implements FollowBusiness {

	INSTANCE;

	private CompanyBusinessImpl companyBusiness;
	private FollowDAO followDAO;
	private CompanyDAO companyDAO;

	FollowBusinessImpl() {
		companyBusiness = CompanyBusinessImpl.INSTANCE;
		followDAO = new FollowDAO();
		companyDAO = new CompanyDAO();
	}

	@Override
	public final void addFollow(final User user, final String ticker, final Double lower, final Double higher) throws YahooException {
		Company company = companyBusiness.addOrUpdateCompany(ticker);
		Follow foll = followDAO.selectOneFollow(user.getId(), company.getId());
		if (foll == null) {
			Follow follow = new Follow();
			follow.setCompany(company);
			follow.setCompanyId(company.getId());
			follow.setUserId(user.getId());
			follow.setLowerLimit(lower);
			follow.setHigherLimit(higher);
			followDAO.insert(follow);
		} else {
			foll.setLowerLimit(lower);
			foll.setHigherLimit(higher);
			followDAO.update(foll);
		}
	}

	@Override
	public final void updateFollow(final User user, final String ticker, final Double lower, final Double higher) {
		Company company = companyDAO.selectWithYahooId(ticker);
		Follow foll = followDAO.selectOneFollow(user.getId(), company.getId());
		foll.setLowerLimit(lower);
		foll.setHigherLimit(higher);
		followDAO.update(foll);
	}

	@Override
	public final void deleteFollow(final int id) {
		Follow follow = new Follow();
		follow.setId(id);
		followDAO.delete(follow);
	}

	@Override
	public final List<Follow> getListFollow(final int userId) {
		return followDAO.selectListFollow(userId);
	}
}
