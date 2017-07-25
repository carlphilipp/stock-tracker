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

package fr.cph.stock.repository;

import fr.cph.stock.entities.Account;
import fr.cph.stock.entities.Equity;
import fr.cph.stock.entities.Portfolio;
import fr.cph.stock.entities.ShareValue;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * This class implements DAO functions and add some more. It access to the Portfolio in DB.
 *
 * @author Carl-Philipp Harmant
 */
@RequiredArgsConstructor
@Component
public class PortfolioRepository implements DAO<Portfolio> {

	private static final String INSERT = "fr.cph.stock.repository.PortfolioRepository.insertOnePortfolio";
	private static final String SELECT = "fr.cph.stock.repository.PortfolioRepository.selectOnePortfolio";
	private static final String UPDATE = "fr.cph.stock.repository.PortfolioRepository.updateOnePortfolio";
	private static final String DELETE = "fr.cph.stock.repository.PortfolioRepository.deleteOnePortfolio";
	private static final String SELECT_WITH_ID = "fr.cph.stock.repository.PortfolioRepository.selectPortfolioWithId";
	private static final String SELECT_EQUITY = "fr.cph.stock.repository.PortfolioRepository.selectEquityFromPortfolio";
	private static final String ACCOUNT_SELECT = "fr.cph.stock.repository.AccountRepository.selectAllAccountWithUserId";
	private static final String SHARE_VALUE_SELECT = "fr.cph.stock.repository.ShareValueRepository.selectAllValue";
	private static final String SHARE_VALUE_SELECT_FROM = "fr.cph.stock.repository.ShareValueRepository.selectShareValueFrom";
	private static final String SHARE_VALUE_SELECT_TO = "fr.cph.stock.repository.ShareValueRepository.selectShareValueFromTo";

	@NonNull
	private final SqlSession session;

	@Override
	public final void insert(final Portfolio portfolio) {
		session.insert(INSERT, portfolio);
	}

	@Override
	public final Optional<Portfolio> select(final int id) {
		return Optional.ofNullable(session.selectOne(SELECT, id));
	}

	@Override
	public final void update(final Portfolio portfolio) {
		session.update(UPDATE, portfolio);
	}

	@Override
	public final void delete(final Portfolio portfolio) {
		session.delete(DELETE, portfolio);
	}

	public final Optional<Portfolio> selectPortfolioFromUserIdWithEquities(final int userId, final Date from, final Date to) {
		Optional<Portfolio> portfolioOptional;
		portfolioOptional = Optional.ofNullable(session.selectOne(SELECT_WITH_ID, userId));
		portfolioOptional.ifPresent(portfolio -> {
			final List<Equity> equities = session.selectList(SELECT_EQUITY, portfolio.getId());
			portfolio.setEquities(equities);
			final List<Account> accounts = session.selectList(ACCOUNT_SELECT, userId);
			portfolio.setAccounts(accounts);
			if (from == null) {
				final List<ShareValue> shares = session.selectList(SHARE_VALUE_SELECT, userId);
				portfolio.setShareValues(shares);
			} else {
				final Map<String, Object> map = new HashMap<>();
				map.put("userId", userId);
				map.put("from", from);
				if (to == null) {
					final List<ShareValue> shares = session.selectList(SHARE_VALUE_SELECT_FROM, map);
					portfolio.setShareValues(shares);
				} else {
					map.put("to", to);
					final List<ShareValue> shares = session.selectList(SHARE_VALUE_SELECT_TO, map);
					portfolio.setShareValues(shares);
				}
			}
		});
		return portfolioOptional;
	}

	public final Optional<Portfolio> selectPortfolioFromUserIdWithEquities(final int userId) {
		return selectPortfolioFromUserIdWithEquities(userId, null, null);
	}
}
