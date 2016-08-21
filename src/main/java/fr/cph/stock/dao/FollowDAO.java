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
import fr.cph.stock.entities.Follow;
import org.apache.ibatis.session.SqlSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class implements DAO functions and add some more. It access to the Follow object in DB.
 *
 * @author Carl-Philipp Harmant
 *
 */
@Singleton
public class FollowDAO implements DAO<Follow> {

	private static final String INSERT = "FollowDao.insertOneFollow";
	private static final String SELECT = "FollowDao.selectOneFollow";
	private static final String UPDATE = "FollowDao.updateOneFollow";
	private static final String DELETE = "FollowDao.deleteOneFollow";
	private static final String SELECT_LIST = "FollowDao.selectListFollow";

	private SessionManager sessionManager = SessionManager.INSTANCE;

	@Override
	public final void insert(final Follow follow) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(true)) {
			session.insert(INSERT, follow);
		}
	}

	@Override
	public final Follow select(final int id) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(false)) {
			return session.selectOne(SELECT, id);
		}
	}

	@Override
	public final void update(final Follow follow) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(true)) {
			session.update(UPDATE, follow);
		}
	}

	@Override
	public final void delete(final Follow follow) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(true)) {
			session.delete(DELETE, follow);
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
		try (final SqlSession session = sessionManager.getSqlSessionFactory(false)) {
			return session.selectList(SELECT_LIST, userId);
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
		final Map<String, Integer> map = new HashMap<>();
		map.put("userId", userId);
		map.put("companyId", companyId);
		try (final SqlSession session = sessionManager.getSqlSessionFactory(false)) {
			return session.selectOne(SELECT, map);
		}
	}
}
