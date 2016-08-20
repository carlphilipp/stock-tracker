/**
 * Copyright 2013 Carl-Philipp Harmant
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.cph.stock.dao;

import fr.cph.stock.dao.mybatis.SessionManager;
import fr.cph.stock.entities.Account;
import fr.cph.stock.entities.Equity;
import fr.cph.stock.entities.Portfolio;
import fr.cph.stock.entities.ShareValue;
import org.apache.ibatis.session.SqlSession;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class implements DAO functions and add some more. It access to the Portfolio in DB.
 *
 * @author Carl-Philipp Harmant
 *
 */
public enum PortfolioDAO implements DAO<Portfolio> {

	INSTANCE;

	private static final String INSERT = "PortfolioDao.insertOnePortfolio";
	private static final String SELECT = "PortfolioDao.selectOnePortfolio";
	private static final String UPDATE = "PortfolioDao.updateOnePortfolio";
	private static final String DELETE = "PortfolioDao.deleteOnePortfolio";
	private static final String SELECT_WITH_ID = "PortfolioDao.selectPortfolioWithId";
	private static final String SELECT_EQUITY = "PortfolioDao.selectEquityFromPortfolio";
	private static final String ACCOUNT_SELECT = "AccountDao.selectAllAccountWithUserId";
	private static final String SHARE_VALUE_SELECT = "ShareValue.selectAllValue";
	private static final String SHARE_VALUE_SELECT_FROM = "ShareValue.selectShareValueFrom";
	private static final String SHARE_VALUE_SELECT_TO = "ShareValue.selectShareValueFromTo";

	private SessionManager sessionManager = SessionManager.INSTANCE;

	@Override
	public final void insert(final Portfolio portfolio) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(true)) {
			session.insert(INSERT, portfolio);
		}
	}

	@Override
	public final Portfolio select(final int id) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(false)) {
			return session.selectOne(SELECT, id);
		}
	}

	/**
	 * Get portfolio with user id
	 *
	 * @param userId
	 *            the user id
	 * @return a Portfolio
	 */
	public final Portfolio selectPortfolioWithId(final int userId) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(false)) {
			return session.selectOne(SELECT_WITH_ID, userId);
		}
	}

	@Override
	public final void update(final Portfolio portfolio) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(true)) {
			session.update(UPDATE, portfolio);
		}
	}

	@Override
	public final void delete(final Portfolio portfolio) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(true)) {
			session.delete(DELETE, portfolio);
		}
	}

	/**
	 * Get portfolio, loaded with its equities
	 *
	 * @param userId
	 *            the user id
	 * @param from
	 *            the from date
	 * @param to
	 *            the to date
	 * @return a portfolio
	 */
	public final Portfolio selectPortfolioFromUserIdWithEquities(final int userId, final Date from, final Date to) {
		Portfolio portfolio;
		try (final SqlSession session = sessionManager.getSqlSessionFactory(false)) {
			portfolio = session.selectOne(SELECT_WITH_ID, userId);
			if (portfolio != null) {
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
			}
		}
		return portfolio;
	}
}
