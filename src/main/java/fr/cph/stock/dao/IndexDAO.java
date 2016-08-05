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
		SqlSession session = getSqlSessionFactory();
		try {
			session.insert("IndexDao.insertOneIndex", index);
			session.commit();
		} finally {
			session.close();
		}
	}

	@Override
	public final Index select(final int id) {
		SqlSession session = getSqlSessionFactory();
		Index index = null;
		try {
			index = session.selectOne("IndexDao.selectOneIndex", id);
		} finally {
			session.close();
		}
		return index;
	}

	/**
	 * Get Index from DB
	 * 
	 * @param ind
	 *            the index
	 * @return an index
	 */
	public final Index selectOneIndexWithIdAndIndex(final Index ind) {
		SqlSession session = getSqlSessionFactory();
		Index index = null;
		try {
			index = session.selectOne("IndexDao.selectOneIndexWithIdAndIndex", ind);
		} finally {
			session.close();
		}
		return index;
	}

	@Override
	public final void update(final Index index) {
		SqlSession session = getSqlSessionFactory();
		try {
			session.update("IndexDao.updateOneIndex", index);
			session.commit();
		} finally {
			session.close();
		}
	}

	@Override
	public final void delete(final Index index) {
		SqlSession session = getSqlSessionFactory();
		try {
			session.delete("IndexDao.deleteOneIndex", index);
			session.commit();
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
		SqlSession session = getSqlSessionFactory();
		List<Index> indexes = null;
		Map<String, Object> map = new HashMap<>();
		map.put("yahooId", yahooId);
		map.put("from", from);
		map.put("to", to);
		try {
			indexes = session.selectList("IndexDao.selectListIndexFromTo", map);
		} finally {
			session.close();
		}
		return indexes;
	}

	/**
	 * Get last Index
	 * 
	 * @param yahooId
	 *            the index requested
	 * @return and Index with last data
	 */
	public final Index selectLast(final String yahooId) {
		SqlSession session = getSqlSessionFactory();
		Index index = null;
		try {
			index = session.selectOne("IndexDao.selectLastIndex", yahooId);
		} finally {
			session.close();
		}
		return index;
	}
}
