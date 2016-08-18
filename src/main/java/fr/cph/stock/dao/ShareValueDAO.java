/**
 * Copyright 2013 Carl-Philipp Harmant
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

package fr.cph.stock.dao;

import fr.cph.stock.dao.mybatis.SessionManager;
import fr.cph.stock.entities.ShareValue;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 * This class implements DAO functions and add some more. It access to the ShareValue in DB.
 *
 * @author Carl-Philipp Harmant
 *
 */
public enum ShareValueDAO implements DAO<ShareValue> {

	INSTANCE;

	private SessionManager sessionManager = SessionManager.INSTANCE;

	@Override
	public final void insert(final ShareValue shareValue) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(true)) {
			session.insert("ShareValue.insertOneShareValue", shareValue);
		}
	}

	/**
	 * Insert a share value with a date
	 *
	 * @param shareValue
	 *            the share value
	 */
	public final void insertWithDate(final ShareValue shareValue) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(true)) {
			session.insert("ShareValue.insertOneShareValueWithDate", shareValue);
		}
	}

	@Override
	public final ShareValue select(final int id) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(false)) {
			return session.selectOne("ShareValue.selectOneShareValue", id);
		}
	}

	@Override
	public final void update(final ShareValue shareValue) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(true)) {
			session.update("ShareValue.updateOneShareValue", shareValue);
		}
	}

	@Override
	public final void delete(final ShareValue shareValue) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(true)) {
			session.delete("ShareValue.deleteOneShareValue", shareValue);
		}
	}

	/**
	 * Get the last share value of a user
	 *
	 * @param userId
	 *            the user id
	 * @return a share value
	 */
	public final ShareValue selectLastValue(final int userId) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(false)) {
			return session.selectOne("ShareValue.selectLastValue", userId);
		}
	}

	/**
	 * Get all ShareValue of a user
	 *
	 * @param userId
	 *            a user id
	 * @return a list of share value
	 */
	public final List<ShareValue> selectAllValue(final int userId) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(false)) {
			return session.selectList("ShareValue.selectAllValue", userId);
		}
	}
}
