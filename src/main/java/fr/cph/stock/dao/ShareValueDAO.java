/**
 * Copyright 2016 Carl-Philipp Harmant
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

import com.google.inject.Singleton;
import fr.cph.stock.dao.mybatis.SessionManager;
import fr.cph.stock.entities.ShareValue;
import org.apache.ibatis.session.SqlSession;

/**
 * This class implements DAO functions and add some more. It access to the ShareValue in DB.
 *
 * @author Carl-Philipp Harmant
 */
@Singleton
public class ShareValueDAO implements DAO<ShareValue> {

	private static final String INSERT = "fr.cph.stock.dao.ShareValue.insertOneShareValue";
	private static final String SELECT = "fr.cph.stock.dao.ShareValue.selectOneShareValue";
	private static final String UPDATE = "fr.cph.stock.dao.ShareValue.updateOneShareValue";
	private static final String DELETE = "fr.cph.stock.dao.ShareValue.deleteOneShareValue";
	private static final String INSERT_WITH_DATE = "fr.cph.stock.dao.ShareValue.insertOneShareValueWithDate";
	private static final String SELECT_LAST_VALUE = "fr.cph.stock.dao.ShareValue.selectLastValue";

	private SessionManager sessionManager = SessionManager.INSTANCE;

	@Override
	public final void insert(final ShareValue shareValue) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(true)) {
			session.insert(INSERT, shareValue);
		}
	}

	/**
	 * Insert a share value with a date
	 *
	 * @param shareValue the share value
	 */
	public final void insertWithDate(final ShareValue shareValue) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(true)) {
			session.insert(INSERT_WITH_DATE, shareValue);
		}
	}

	@Override
	public final ShareValue select(final int id) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(false)) {
			return session.selectOne(SELECT, id);
		}
	}

	@Override
	public final void update(final ShareValue shareValue) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(true)) {
			session.update(UPDATE, shareValue);
		}
	}

	@Override
	public final void delete(final ShareValue shareValue) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(true)) {
			session.delete(DELETE, shareValue);
		}
	}

	/**
	 * Get the last share value of a user
	 *
	 * @param userId the user id
	 * @return a share value
	 */
	public final ShareValue selectLastValue(final int userId) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(false)) {
			return session.selectOne(SELECT_LAST_VALUE, userId);
		}
	}
}
