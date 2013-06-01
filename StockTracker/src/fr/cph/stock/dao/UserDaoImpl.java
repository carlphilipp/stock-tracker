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

public class UserDaoImpl extends AbstractDao<User> {

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
	
	public void updateOneUserPassword(User user){
		SqlSession session = getSqlSessionFactory();
		try {
			session.update("UserDao.updateOneUserPassword", user);
			session.commit();
		} finally {
			session.close();
		}
	}

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
