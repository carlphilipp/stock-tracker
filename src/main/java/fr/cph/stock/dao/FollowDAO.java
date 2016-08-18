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

import fr.cph.stock.entities.Follow;
import org.apache.ibatis.session.SqlSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class implements IDAO functions and add some more. It access to the Follow object in DB.
 *
 * @author Carl-Philipp Harmant
 *
 */
public class FollowDAO extends AbstractDAO<Follow> {

	@Override
	public final void insert(final Follow follow) {
		final SqlSession session = getSqlSessionFactory(true);
		try {
			session.insert("FollowDao.insertOneFollow", follow);
		} finally {
			session.close();
		}
	}

	@Override
	public final Follow select(final int id) {
		final SqlSession session = getSqlSessionFactory(false);
		try {
			return session.selectOne("FollowDao.selectOneFollow", id);
		} finally {
			session.close();
		}
	}

	@Override
	public final void update(final Follow follow) {
		final SqlSession session = getSqlSessionFactory(true);
		try {
			session.update("FollowDao.updateOneFollow", follow);
		} finally {
			session.close();
		}
	}

	@Override
	public final void delete(final Follow follow) {
		final SqlSession session = getSqlSessionFactory(true);
		try {
			session.delete("FollowDao.deleteOneFollow", follow);
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
	public final List<Follow> selectListFollow(final int userId) {
		final SqlSession session = getSqlSessionFactory(false);
		try {
			return session.selectList("FollowDao.selectListFollow", userId);
		} finally {
			session.close();
		}
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
	public final Follow selectOneFollow(final int userId, final int companyId) {
		final SqlSession session = getSqlSessionFactory(false);
		final Map<String, Integer> map = new HashMap<>();
		map.put("userId", userId);
		map.put("companyId", companyId);
		try {
			return session.selectOne("FollowDao.selectOneFollow", map);
		} finally {
			session.close();
		}
	}
}
