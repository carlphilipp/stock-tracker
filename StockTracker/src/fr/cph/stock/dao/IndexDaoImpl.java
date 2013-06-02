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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import fr.cph.stock.entities.Index;

/**
 * This class implements IDao functions and add some more. It access to the Index in DB.
 * 
 * @author Carl-Philipp Harmant
 * 
 */
public class IndexDaoImpl extends AbstractDao<Index> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.cph.stock.dao.IDao#insert(java.lang.Object)
	 */
	@Override
	public void insert(Index index) {
		SqlSession session = getSqlSessionFactory();
		try {
			session.insert("IndexDao.insertOneIndex", index);
			session.commit();
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.cph.stock.dao.IDao#select(int)
	 */
	@Override
	public Index select(int id) {
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
	 * @return
	 */
	public Index selectOneIndexWithIdAndIndex(Index ind) {
		SqlSession session = getSqlSessionFactory();
		Index index = null;
		try {
			index = session.selectOne("IndexDao.selectOneIndexWithIdAndIndex", ind);
		} finally {
			session.close();
		}
		return index;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.cph.stock.dao.IDao#update(java.lang.Object)
	 */
	@Override
	public void update(Index index) {
		SqlSession session = getSqlSessionFactory();
		try {
			session.update("IndexDao.updateOneIndex", index);
			session.commit();
		} finally {
			session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.cph.stock.dao.IDao#delete(java.lang.Object)
	 */
	@Override
	public void delete(Index index) {
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
	public List<Index> selectListFrom(String yahooId, Date from, Date to) {
		SqlSession session = getSqlSessionFactory();
		List<Index> indexes = new ArrayList<Index>();
		Map<String, Object> map = new HashMap<String, Object>();
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
	public Index selectLast(String yahooId) {
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
