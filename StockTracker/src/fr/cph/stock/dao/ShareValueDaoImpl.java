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

public class ShareValueDaoImpl extends AbstractDao<ShareValue> {

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

	public void insertWithDate(ShareValue shareValue) {
		SqlSession session = getSqlSessionFactory();
		try {
			session.insert("ShareValue.insertOneShareValueWithDate", shareValue);
			session.commit();
		} finally {
			session.close();
		}
	}

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
