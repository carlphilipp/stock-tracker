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

import fr.cph.stock.entities.Index;
import org.apache.ibatis.session.SqlSession;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class implements IDAO functions and add some more. It access to the Index in DB.
 *
 * @author Carl-Philipp Harmant
 *
 */
public class IndexDAO extends AbstractDAO<Index> {

	@Override
	public final void insert(final Index index) {
		final SqlSession session = getSqlSessionFactory(true);
		try {
			session.insert("IndexDao.insertOneIndex", index);
		} finally {
			session.close();
		}
	}

	@Override
	public final Index select(final int id) {
		final SqlSession session = getSqlSessionFactory(false);
		try {
			return session.selectOne("IndexDao.selectOneIndex", id);
		} finally {
			session.close();
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
		final SqlSession session = getSqlSessionFactory(false);
		try {
			return session.selectOne("IndexDao.selectOneIndexWithIdAndIndex", ind);
		} finally {
			session.close();
		}
	}

	@Override
	public final void update(final Index index) {
		final SqlSession session = getSqlSessionFactory(true);
		try {
			session.update("IndexDao.updateOneIndex", index);
		} finally {
			session.close();
		}
	}

	@Override
	public final void delete(final Index index) {
		final SqlSession session = getSqlSessionFactory(true);
		try {
			session.delete("IndexDao.deleteOneIndex", index);
		} finally {
			session.close();
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
		final SqlSession session = getSqlSessionFactory(false);
		final Map<String, Object> map = new HashMap<>();
		map.put("yahooId", yahooId);
		map.put("from", from);
		map.put("to", to);
		try {
			return session.selectList("IndexDao.selectListIndexFromTo", map);
		} finally {
			session.close();
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
		final SqlSession session = getSqlSessionFactory(false);
		try {
			return session.selectOne("IndexDao.selectLastIndex", yahooId);
		} finally {
			session.close();
		}
	}
}
