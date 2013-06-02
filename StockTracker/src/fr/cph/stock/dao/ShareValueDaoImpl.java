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
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import fr.cph.stock.entities.ShareValue;

/**
 * This class implements IDao functions and add some more. It access to the ShareValue in DB.
 * 
 * @author Carl-Philipp Harmant
 * 
 */
public class ShareValueDaoImpl extends AbstractDao<ShareValue> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.cph.stock.dao.IDao#insert(java.lang.Object)
	 */
	@Override
	public void insert(ShareValue shareValue) {
		SqlSession session = getSqlSessionFactory();
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
	 */
	public void insertWithDate(ShareValue shareValue) {
		SqlSession session = getSqlSessionFactory();
		try {
			session.insert("ShareValue.insertOneShareValueWithDate", shareValue);
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
	public ShareValue select(int id) {
		SqlSession session = getSqlSessionFactory();
		ShareValue shareValue = null;
		try {
			shareValue = session.selectOne("ShareValue.selectOneShareValue", id);
		} finally {
			session.close();
		}
		return shareValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.cph.stock.dao.IDao#update(java.lang.Object)
	 */
	@Override
	public void update(ShareValue shareValue) {
		SqlSession session = getSqlSessionFactory();
		try {
			session.update("ShareValue.updateOneShareValue", shareValue);
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
	public void delete(ShareValue shareValue) {
		SqlSession session = getSqlSessionFactory();
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
	public ShareValue selectLastValue(int userId) {
		SqlSession session = getSqlSessionFactory();
		ShareValue shareValue = null;
		try {
			shareValue = session.selectOne("ShareValue.selectLastValue", userId);
		} finally {
			session.close();
		}
		return shareValue;
	}

	/**
	 * Get all ShareValue of a user
	 * 
	 * @param userId
	 *            a user id
	 * @return a list of share value
	 */
	public List<ShareValue> selectAllValue(int userId) {
		SqlSession session = getSqlSessionFactory();
		List<ShareValue> shareValues = new ArrayList<ShareValue>();
		try {
			shareValues = session.selectList("ShareValue.selectAllValue", userId);
		} finally {
			session.close();
		}
		return shareValues;
	}

}
