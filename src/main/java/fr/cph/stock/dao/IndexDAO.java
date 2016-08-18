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
import fr.cph.stock.entities.Index;
import org.apache.ibatis.session.SqlSession;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class implements DAO functions and add some more. It access to the Index in DB.
 *
 * @author Carl-Philipp Harmant
 *
 */
public enum IndexDAO implements DAO<Index> {

	INSTANCE;

	private SessionManager sessionManager = SessionManager.INSTANCE;

	@Override
	public final void insert(final Index index) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(true)) {
			session.insert("IndexDao.insertOneIndex", index);
		}
	}

	@Override
	public final Index select(final int id) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(false)) {
			return session.selectOne("IndexDao.selectOneIndex", id);
		}
	}

	/**
	 * Get Index from DB
	 *
	 * @param ind
	 *            the index
	 * @return an index
	 */
	public final Index selectOneIndexWithIdAndIndex(final Index ind) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(false)) {
			return session.selectOne("IndexDao.selectOneIndexWithIdAndIndex", ind);
		}
	}

	@Override
	public final void update(final Index index) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(true)) {
			session.update("IndexDao.updateOneIndex", index);
		}
	}

	@Override
	public final void delete(final Index index) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(true)) {
			session.delete("IndexDao.deleteOneIndex", index);
		}
	}

	/**
	 * Get a list of Index
	 *
	 * @param yahooId
	 *            the index requested
	 * @param from
	 *            first date
	 * @param to
	 *            second date
	 * @return a list of Index
	 */
	public final List<Index> selectListFrom(final String yahooId, final Date from, final Date to) {
		final Map<String, Object> map = new HashMap<>();
		map.put("yahooId", yahooId);
		map.put("from", from);
		map.put("to", to);
		try (final SqlSession session = sessionManager.getSqlSessionFactory(false)) {
			return session.selectList("IndexDao.selectListIndexFromTo", map);
		}
	}

	/**
	 * Get last Index
	 *
	 * @param yahooId
	 *            the index requested
	 * @return and Index with last data
	 */
	public final Index selectLast(final String yahooId) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(false)) {
			return session.selectOne("IndexDao.selectLastIndex", yahooId);
		}
	}
}
