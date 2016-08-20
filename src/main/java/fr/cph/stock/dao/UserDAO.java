/**
 * Copyright 2013 Carl-Philipp Harmant
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

import fr.cph.stock.dao.mybatis.SessionManager;
import fr.cph.stock.entities.User;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 * This class implements DAO functions and add some more. It access to the User in DB.
 *
 * @author Carl-Philipp Harmant
 */
public enum UserDAO implements DAO<User> {

	INSTANCE;

	private static final String INSERT = "UserDao.insertOneUser";
	private static final String SELECT = "UserDao.selectOneUser";
	private static final String UPDATE = "UserDao.updateOneUser";
	private static final String DELETE = "UserDao.deleteOneUser";
	private static final String UPDATE_PASSWORD = "UserDao.updateOneUserPassword";
	private static final String SELECT_WITH_LOGIN = "UserDao.selectOneUserWithLogin";
	private static final String SELECT_WITH_EMAIL = "UserDao.selectOneUserWithEmail";
	private static final String SELECT_ALL_USER = "UserDao.selectAllUsers";

	private SessionManager sessionManager = SessionManager.INSTANCE;

	@Override
	public final void insert(final User user) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(true)) {
			session.insert(INSERT, user);
		}
	}

	@Override
	public final User select(final int id) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(false)) {
			return session.selectOne(SELECT, id);
		}
	}

	@Override
	public final void update(final User user) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(true)) {
			session.update(UPDATE, user);
		}
	}

	/**
	 * Update one user password
	 *
	 * @param user the user
	 */
	public final void updateOneUserPassword(final User user) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(true)) {
			session.update(UPDATE_PASSWORD, user);
		}
	}

	@Override
	public final void delete(final User user) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(true)) {
			session.delete(DELETE, user);
		}
	}

	/**
	 * Get a user with its login
	 *
	 * @param login the login
	 * @return a user
	 */
	public final User selectWithLogin(final String login) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(false)) {
			return session.selectOne(SELECT_WITH_LOGIN, login);
		}
	}

	/**
	 * Get a user with its email
	 *
	 * @param email the email
	 * @return a user
	 */
	public final User selectWithEmail(final String email) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(false)) {
			return session.selectOne(SELECT_WITH_EMAIL, email);
		}
	}

	/**
	 * Get all users
	 *
	 * @return a list of user
	 */
	public final List<User> selectAllUsers() {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(false)) {
			return session.selectList(SELECT_ALL_USER);
		}
	}
}
