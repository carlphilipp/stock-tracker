/**
 * Copyright 2017 Carl-Philipp Harmant
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
import fr.cph.stock.entities.User;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * This class implements DAO functions and add some more. It access to the User in DB.
 *
 * @author Carl-Philipp Harmant
 */
@Repository
@Singleton
public class UserDAO implements DAO<User> {

	private static final String INSERT = "fr.cph.stock.dao.UserDao.insertOneUser";
	private static final String SELECT = "fr.cph.stock.dao.UserDao.selectOneUser";
	private static final String UPDATE = "fr.cph.stock.dao.UserDao.updateOneUser";
	private static final String DELETE = "fr.cph.stock.dao.UserDao.deleteOneUser";
	private static final String UPDATE_PASSWORD = "fr.cph.stock.dao.UserDao.updateOneUserPassword";
	private static final String SELECT_WITH_LOGIN = "fr.cph.stock.dao.UserDao.selectOneUserWithLogin";
	private static final String SELECT_WITH_EMAIL = "fr.cph.stock.dao.UserDao.selectOneUserWithEmail";
	private static final String SELECT_ALL_USER = "fr.cph.stock.dao.UserDao.selectAllUsers";

	private final SessionManager sessionManager = SessionManager.INSTANCE;

	@Override
	public final void insert(final User user) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(true)) {
			session.insert(INSERT, user);
		}
	}

	@Override
	public final Optional<User> select(final int id) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(false)) {
			return Optional.ofNullable(session.selectOne(SELECT, id));
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
	public final Optional<User> selectWithLogin(final String login) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(false)) {
			return Optional.ofNullable(session.selectOne(SELECT_WITH_LOGIN, login));
		}
	}

	/**
	 * Get a user with its email
	 *
	 * @param email the email
	 * @return a user
	 */
	public final Optional<User> selectWithEmail(final String email) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(false)) {
			return Optional.ofNullable(session.selectOne(SELECT_WITH_EMAIL, email));
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
