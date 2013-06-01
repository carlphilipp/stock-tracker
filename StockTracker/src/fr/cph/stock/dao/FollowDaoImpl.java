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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import fr.cph.stock.entities.Follow;

public class FollowDaoImpl extends AbstractDao<Follow> {
	
//	private static final Logger log = Logger.getLogger(FollowDaoImpl.class);

	@Override
	public void insert(Follow follow) {
		SqlSession session = getSqlSessionFactory();
		try {
			session.insert("FollowDao.insertOneFollow", follow);
			session.commit();
		} finally {
			session.close();
		}
	}

	@Override
	public Follow select(int id) {
		SqlSession session = getSqlSessionFactory();
		Follow follow = null;
		try {
			follow = session.selectOne("FollowDao.selectOneFollow", id);
		} finally {
			session.close();
		}
		return follow;
	}

	@Override
	public void update(Follow follow) {
		SqlSession session = getSqlSessionFactory();
		try {
			session.update("FollowDao.updateOneFollow", follow);
			session.commit();
		} finally {
			session.close();
		}
	}

	@Override
	public void delete(Follow follow) {
		SqlSession session = getSqlSessionFactory();
		try {
			session.delete("FollowDao.deleteOneFollow", follow);
			session.commit();
		} finally {
			session.close();
		}
	}
	
	public List<Follow> selectListFollow(int userId) {
		SqlSession session = getSqlSessionFactory();
		List<Follow> follow = new ArrayList<Follow>();
		try {
			follow = session.selectList("FollowDao.selectListFollow", userId);
		} finally {
			session.close();
		}
		return follow;
	}
	
	public Follow selectOneFollow(int userId, int companyId) {
		SqlSession session = getSqlSessionFactory();
		Follow follow = null;
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("userId", userId);
		map.put("companyId", companyId);
		try {
			follow = session.selectOne("FollowDao.selectOneFollow", map);
		} finally {
			session.close();
		}
		return follow;
	}

}
