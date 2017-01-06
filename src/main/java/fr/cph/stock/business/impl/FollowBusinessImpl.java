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

	@Inject
	private CompanyBusiness companyBusiness;
	private FollowDAO followDAO;
	private CompanyDAO companyDAO;

	@Inject
	public void setFollowDAO(@Named("Follow") final DAO followDAO) {
		this.followDAO = (FollowDAO) followDAO;
	}

	@Inject
	public void setCompanyDAO(@Named("Company") final DAO companyDAO) {
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
			final Follow follow = Follow.builder()
				.company(company)
				.companyId(company.getId())
				.userId(user.getId())
				.lowerLimit(lower)
				.higherLimit(higher)
				.build();
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
		followDAO.delete(Follow.builder().id(id).build());
	}

	@Override
	public final List<Follow> getListFollow(final int userId) {
		return followDAO.selectListFollow(userId);
	}
}
