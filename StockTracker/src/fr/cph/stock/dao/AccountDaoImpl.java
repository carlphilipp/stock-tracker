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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import fr.cph.stock.entities.Account;

/**
 * This class implements IDao functions and add some more. It access to the Account in DB.
 * 
 * @author Carl-Philipp Harmant
 * 
 */
public class AccountDaoImpl extends AbstractDao<Account> {

	@Override
	public final void insert(final Account account) {
		SqlSession session = getSqlSessionFactory();
		try {
			session.insert("AccountDao.insertOneAccount", account);
			session.commit();
		} finally {
			session.close();
		}
	}

	@Override
	public final Account select(final int id) {
		SqlSession session = getSqlSessionFactory();
		Account accountResult = null;
		try {
			accountResult = session.selectOne("AccountDao.selectOneAccount", id);
		} finally {
			session.close();
		}
		return accountResult;
	}

	@Override
	public final void update(final Account account) {
		SqlSession session = getSqlSessionFactory();
		try {
			session.update("AccountDao.updateOneAccount", account);
			session.commit();
		} finally {
			session.close();
		}

	}

	@Override
	public final void delete(final Account account) {
		SqlSession session = getSqlSessionFactory();
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
		SqlSession session = getSqlSessionFactory();
		List<Account> accountResult = null;
		try {
			accountResult = session.selectList("AccountDao.selectAllAccountWithUserId", userId);
		} finally {
			session.close();
		}
		return accountResult;
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
		SqlSession session = getSqlSessionFactory();
		Account account = null;
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map.put("userId", userId);
			map.put("name", name);
			account = session.selectOne("AccountDao.selectOneAccountWithName", map);
		} finally {
			session.close();
		}
		return account;
	}
}
