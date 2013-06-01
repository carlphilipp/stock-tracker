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

public class IndexDaoImpl extends AbstractDao<Index> {

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
