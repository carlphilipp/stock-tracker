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
 * This class implements IDAO functions and add some more. It access to the Portfolio in DB.
 * 
 * @author Carl-Philipp Harmant
 * 
 */
public class PortfolioDAO extends AbstractDAO<Portfolio> {

	@Override
	public final void insert(final Portfolio portfolio) {
		final SqlSession session = getSqlSessionFactory();
		try {
			session.insert("PortfolioDao.insertOnePortfolio", portfolio);
			session.commit();
		} finally {
			session.close();
		}
	}

	@Override
	public final Portfolio select(final int id) {
		final SqlSession session = getSqlSessionFactory();
		try {
			return session.selectOne("PortfolioDao.selectOnePortfolio", id);
		} finally {
			session.close();
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
		final SqlSession session = getSqlSessionFactory();
		try {
			return session.selectOne("PortfolioDao.selectPortfolioWithId", userId);
		} finally {
			session.close();
		}
	}

	@Override
	public final void update(final Portfolio portfolio) {
		final SqlSession session = getSqlSessionFactory();
		try {
			session.update("PortfolioDao.updateOnePortfolio", portfolio);
			session.commit();
		} finally {
			session.close();
		}
	}

	@Override
	public final void delete(final Portfolio portfolio) {
		final SqlSession session = getSqlSessionFactory();
		try {
			session.delete("PortfolioDao.deleteOnePortfolio", portfolio);
			session.commit();
		} finally {
			session.close();
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
		final SqlSession session = getSqlSessionFactory();
		Portfolio portfolio = null;
		try {
			portfolio = session.selectOne("PortfolioDao.selectPortfolioWithId", userId);
			if (portfolio != null) {
				final List<Equity> equities = session.selectList("PortfolioDao.selectEquityFromPortfolio", portfolio.getId());
				portfolio.setEquities(equities);
				final List<Account> accounts = session.selectList("AccountDAO.selectAllAccountWithUserId", userId);
				portfolio.setAccounts(accounts);
				if (from == null) {
					final List<ShareValue> shares = session.selectList("ShareValue.selectAllValue", userId);
					portfolio.setShareValues(shares);
				} else {
					final Map<String, Object> map = new HashMap<>();
					map.put("userId", userId);
					map.put("from", from);
					if (to == null) {
						final List<ShareValue> shares = session.selectList("ShareValue.selectShareValueFrom", map);
						portfolio.setShareValues(shares);
					} else {
						map.put("to", to);
						final List<ShareValue> shares = session.selectList("ShareValue.selectShareValueFromTo", map);
						portfolio.setShareValues(shares);
					}

				}
			}
		} finally {
			session.close();
		}
		return portfolio;
	}
}
