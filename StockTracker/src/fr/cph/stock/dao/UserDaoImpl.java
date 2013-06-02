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

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import fr.cph.stock.entities.User;

/**
 * This class implements IDao functions and add some more. It access to the User in DB.
 * 
 * @author Carl-Philipp Harmant
 * 
 */
public class UserDaoImpl extends AbstractDao<User> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.cph.stock.dao.IDao#insert(java.lang.Object)
	 */
	@Override
	public void insert(User user) {
		SqlSession session = getSqlSessionFactory();
		try {
			session.insert("UserDao.insertOneUser", user);
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
	public User select(int id) {
		SqlSession session = getSqlSessionFactory();
		User userResult = null;
		try {
			userResult = session.selectOne("UserDao.selectOneUser", id);
		} finally {
			session.close();
		}
		return userResult;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.cph.stock.dao.IDao#update(java.lang.Object)
	 */
	@Override
	public void update(User user) {
		SqlSession session = getSqlSessionFactory();
		try {
			session.update("UserDao.updateOneUser", user);
			session.commit();
		} finally {
			session.close();
		}
	}

	/**
	 * Update one user password
	 * 
	 * @param user
	 *            the user
	 */
	public void updateOneUserPassword(User user) {
		SqlSession session = getSqlSessionFactory();
		try {
			session.update("UserDao.updateOneUserPassword", user);
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
	public void delete(User user) {
		SqlSession session = getSqlSessionFactory();
		try {
			session.delete("UserDao.deleteOneUser", user);
			session.commit();
		} finally {
			session.close();
		}
	}

	/**
	 * Get a user with its login
	 * 
	 * @param login
	 *            the login
	 * @return a user
	 */
	public User selectWithLogin(String login) {
		SqlSession session = getSqlSessionFactory();
		User userResult = null;
		try {
			userResult = session.selectOne("UserDao.selectOneUserWithLogin", login);
		} finally {
			session.close();
		}
		return userResult;
	}

	/**
	 * Get a user with its email
	 * 
	 * @param email
	 *            the email
	 * @return a user
	 */
	public User selectWithEmail(String email) {
		SqlSession session = getSqlSessionFactory();
		User userResult = null;
		try {
			userResult = session.selectOne("UserDao.selectOneUserWithEmail", email);
		} finally {
			session.close();
		}
		return userResult;
	}

	/**
	 * Get all users
	 * 
	 * @return a list of user
	 */
	public List<User> selectAllUsers() {
		SqlSession session = getSqlSessionFactory();
		List<User> userList = null;
		try {
			userList = session.selectList("UserDao.selectAllUsers");
		} finally {
			session.close();
		}
		return userList;
	}

}
