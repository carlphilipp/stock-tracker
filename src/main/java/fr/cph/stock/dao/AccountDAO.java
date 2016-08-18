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
import org.apache.ibatis.session.SqlSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class implements DAO functions and add some more. It access to the Account in DB.
 *
 * @author Carl-Philipp Harmant
 *
 */
public class AccountDAO implements DAO<Account> {

	private SessionManager sessionManager = SessionManager.INSTANCE;

	@Override
	public final void insert(final Account account) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(true)) {
			session.insert("AccountDao.insertOneAccount", account);
		}
	}

	@Override
	public final Account select(final int id) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(false)) {
			return session.selectOne("AccountDao.selectOneAccount", id);
		}
	}

	@Override
	public final void update(final Account account) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(true)) {
			session.update("AccountDao.updateOneAccount", account);
		}

	}

	@Override
	public final void delete(final Account account) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(true)) {
			session.delete("AccountDao.deleteOneAccount", account);
		}
	}

	/**
	 * Get all account for the user
	 *
	 * @param userId
	 *            the user id
	 * @return a list of account
	 */
	public final List<Account> selectAllAccountWithUserId(final int userId) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(false)) {
			return session.selectList("AccountDao.selectAllAccountWithUserId", userId);
		}
	}

	/**
	 * Get an account
	 *
	 * @param userId
	 *            the user id
	 * @param name
	 *            the name of the account
	 * @return an account
	 */
	public final Account selectOneAccountWithName(final int userId, final String name) {
		final Map<String, Object> map = new HashMap<>();
		try (final SqlSession session = sessionManager.getSqlSessionFactory(false)) {
			map.put("userId", userId);
			map.put("name", name);
			return session.selectOne("AccountDao.selectOneAccountWithName", map);
		}
	}
}
