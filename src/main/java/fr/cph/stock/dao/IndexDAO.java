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

package fr.cph.stock.dao;

import com.google.inject.Singleton;
import fr.cph.stock.dao.mybatis.SessionManager;
import fr.cph.stock.entities.Index;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * This class implements DAO functions and add some more. It access to the Index in DB.
 *
 * @author Carl-Philipp Harmant
 */
@Repository
@Singleton
public class IndexDAO implements DAO<Index> {

	private static final String INSERT = "fr.cph.stock.dao.IndexDao.insertOneIndex";
	private static final String SELECT = "fr.cph.stock.dao.IndexDao.selectOneIndex";
	private static final String UPDATE = "fr.cph.stock.dao.IndexDao.updateOneIndex";
	private static final String DELETE = "fr.cph.stock.dao.IndexDao.deleteOneIndex";
	private static final String SELECT_FROM_TO = "fr.cph.stock.dao.IndexDao.selectListIndexFromTo";
	private static final String SELECT_LAST = "fr.cph.stock.dao.IndexDao.selectLastIndex";

	private final SessionManager sessionManager = SessionManager.INSTANCE;

	@Override
	public final void insert(final Index index) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(true)) {
			session.insert(INSERT, index);
		}
	}

	@Override
	public final Optional<Index> select(final int id) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(false)) {
			return Optional.ofNullable(session.selectOne(SELECT, id));
		}
	}

	@Override
	public final void update(final Index index) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(true)) {
			session.update(UPDATE, index);
		}
	}

	@Override
	public final void delete(final Index index) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(true)) {
			session.delete(DELETE, index);
		}
	}

	/**
	 * Get a list of Index
	 *
	 * @param yahooId the index requested
	 * @param from    first date
	 * @param to      second date
	 * @return a list of Index
	 */
	public final List<Index> selectListFrom(final String yahooId, final Date from, final Date to) {
		final Map<String, Object> map = new HashMap<>();
		map.put("yahooId", yahooId);
		map.put("from", from);
		map.put("to", to);
		try (final SqlSession session = sessionManager.getSqlSessionFactory(false)) {
			return session.selectList(SELECT_FROM_TO, map);
		}
	}

	/**
	 * Get last Index
	 *
	 * @param yahooId the index requested
	 * @return and Index with last data
	 */
	public final Optional<Index> selectLast(final String yahooId) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(false)) {
			return Optional.ofNullable(session.selectOne(SELECT_LAST, yahooId));
		}
	}
}
