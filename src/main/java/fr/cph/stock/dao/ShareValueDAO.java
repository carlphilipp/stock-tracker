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

import fr.cph.stock.entities.ShareValue;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 * This class implements IDAO functions and add some more. It access to the ShareValue in DB.
 * 
 * @author Carl-Philipp Harmant
 * 
 */
public class ShareValueDAO extends AbstractDAO<ShareValue> {

	@Override
	public final void insert(final ShareValue shareValue) {
		final SqlSession session = getSqlSessionFactory();
		try {
			session.insert("ShareValue.insertOneShareValue", shareValue);
			session.commit();
		} finally {
			session.close();
		}
	}

	/**
	 * Insert a share value with a date
	 * 
	 * @param shareValue
	 *            the share value
	 */
	public final void insertWithDate(final ShareValue shareValue) {
		final SqlSession session = getSqlSessionFactory();
		try {
			session.insert("ShareValue.insertOneShareValueWithDate", shareValue);
			session.commit();
		} finally {
			session.close();
		}
	}

	@Override
	public final ShareValue select(final int id) {
		final SqlSession session = getSqlSessionFactory();
		try {
			return session.selectOne("ShareValue.selectOneShareValue", id);
		} finally {
			session.close();
		}
	}

	@Override
	public final void update(final ShareValue shareValue) {
		final SqlSession session = getSqlSessionFactory();
		try {
			session.update("ShareValue.updateOneShareValue", shareValue);
			session.commit();
		} finally {
			session.close();
		}
	}

	@Override
	public final void delete(final ShareValue shareValue) {
		final SqlSession session = getSqlSessionFactory();
		try {
			session.delete("ShareValue.deleteOneShareValue", shareValue);
			session.commit();
		} finally {
			session.close();
		}
	}

	/**
	 * Get the last share value of a user
	 * 
	 * @param userId
	 *            the user id
	 * @return a share value
	 */
	public final ShareValue selectLastValue(final int userId) {
		final SqlSession session = getSqlSessionFactory();
		try {
			return session.selectOne("ShareValue.selectLastValue", userId);
		} finally {
			session.close();
		}
	}

	/**
	 * Get all ShareValue of a user
	 * 
	 * @param userId
	 *            a user id
	 * @return a list of share value
	 */
	public final List<ShareValue> selectAllValue(final int userId) {
		SqlSession session = getSqlSessionFactory();;
		try {
			return session.selectList("ShareValue.selectAllValue", userId);
		} finally {
			session.close();
		}
	}
}
