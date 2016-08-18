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

import fr.cph.stock.entities.User;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 * This class implements IDAO functions and add some more. It access to the User in DB.
 *
 * @author Carl-Philipp Harmant
 */
public class UserDAO extends AbstractDAO<User> {

	@Override
	public final void insert(final User user) {
		try (final SqlSession session = getSqlSessionFactory(true)) {
			session.insert("UserDao.insertOneUser", user);
		}
	}

	@Override
	public final User select(final int id) {
		try (final SqlSession session = getSqlSessionFactory(false)) {
			return session.selectOne("UserDao.selectOneUser", id);
		}
	}

	@Override
	public final void update(final User user) {
		try (final SqlSession session = getSqlSessionFactory(true)) {
			session.update("UserDao.updateOneUser", user);
		}
	}

	/**
	 * Update one user password
	 *
	 * @param user the user
	 */
	public final void updateOneUserPassword(final User user) {
		try (final SqlSession session = getSqlSessionFactory(true)) {
			session.update("UserDao.updateOneUserPassword", user);
		}
	}

	@Override
	public final void delete(final User user) {
		try (final SqlSession session = getSqlSessionFactory(true)) {
			session.delete("UserDao.deleteOneUser", user);
		}
	}

	/**
	 * Get a user with its login
	 *
	 * @param login the login
	 * @return a user
	 */
	public final User selectWithLogin(final String login) {
		try (final SqlSession session = getSqlSessionFactory(false)) {
			return session.selectOne("UserDao.selectOneUserWithLogin", login);
		}
	}

	/**
	 * Get a user with its email
	 *
	 * @param email the email
	 * @return a user
	 */
	public final User selectWithEmail(final String email) {
		try (final SqlSession session = getSqlSessionFactory(false)) {
			return session.selectOne("UserDao.selectOneUserWithEmail", email);
		}
	}

	/**
	 * Get all users
	 *
	 * @return a list of user
	 */
	public final List<User> selectAllUsers() {
		try (final SqlSession session = getSqlSessionFactory(false)) {
			return session.selectList("UserDao.selectAllUsers");
		}
	}
}
