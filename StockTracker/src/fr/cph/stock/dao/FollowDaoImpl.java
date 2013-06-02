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

/**
 * This class implements IDao functions and add some more. It access to the Follow object in DB.
 * 
 * @author Carl-Philipp Harmant
 * 
 */
public class FollowDaoImpl extends AbstractDao<Follow> {

	// private static final Logger log = Logger.getLogger(FollowDaoImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.cph.stock.dao.IDao#insert(java.lang.Object)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.cph.stock.dao.IDao#select(int)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.cph.stock.dao.IDao#update(java.lang.Object)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.cph.stock.dao.IDao#delete(java.lang.Object)
	 */
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

	/**
	 * Get a list of company of the user given
	 * 
	 * @param userId
	 *            the user id
	 * @return a list of follow
	 */
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

	/**
	 * Get on company that the user follow
	 * 
	 * @param userId
	 *            the user id
	 * @param companyId
	 *            the company id
	 * @return a Follow object
	 */
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
