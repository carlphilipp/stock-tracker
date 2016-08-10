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
import org.apache.ibatis.session.SqlSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class implements IDAO functions and add some more. It access to the Account in DB.
 *
 * @author Carl-Philipp Harmant
 *
 */
public class AccountDAO extends AbstractDAO<Account> {

	@Override
	public final void insert(final Account account) {
		final SqlSession session = getSqlSessionFactory();
		try {
			session.insert("AccountDao.insertOneAccount", account);
			session.commit();
		} finally {
			session.close();
		}
	}

	@Override
	public final Account select(final int id) {
		final SqlSession session = getSqlSessionFactory();
		try {
			return session.selectOne("AccountDao.selectOneAccount", id);
		} finally {
			session.close();
		}
	}

	@Override
	public final void update(final Account account) {
		final SqlSession session = getSqlSessionFactory();
		try {
			session.update("AccountDao.updateOneAccount", account);
			session.commit();
		} finally {
			session.close();
		}

	}

	@Override
	public final void delete(final Account account) {
		final SqlSession session = getSqlSessionFactory();
		try {
			session.delete("AccountDao.deleteOneAccount", account);
			session.commit();
		} finally {
			session.close();
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
		final SqlSession session = getSqlSessionFactory();
		try {
			return session.selectList("AccountDao.selectAllAccountWithUserId", userId);
		} finally {
			session.close();
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
		final SqlSession session = getSqlSessionFactory();
		final Map<String, Object> map = new HashMap<>();
		try {
			map.put("userId", userId);
			map.put("name", name);
			return session.selectOne("AccountDao.selectOneAccountWithName", map);
		} finally {
			session.close();
		}
	}
}
