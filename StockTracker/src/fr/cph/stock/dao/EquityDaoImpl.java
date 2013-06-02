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

import org.apache.ibatis.session.SqlSession;

import fr.cph.stock.entities.Equity;

/**
 * This class implements IDao functions and add some more. It access to the Equity in DB.
 * 
 * @author Carl-Philipp Harmant
 * 
 */
public class EquityDaoImpl extends AbstractDao<Equity> {

	/* (non-Javadoc)
	 * @see fr.cph.stock.dao.IDao#insert(java.lang.Object)
	 */
	@Override
	public void insert(Equity equity) {
		SqlSession session = getSqlSessionFactory();
		try {
			session.insert("EquityDao.insertOneEquity", equity);
			session.commit();
		} finally {
			session.close();
		}
		
	}

	/* (non-Javadoc)
	 * @see fr.cph.stock.dao.IDao#select(int)
	 */
	@Override
	public Equity select(int id) {
		SqlSession session = getSqlSessionFactory();
		Equity equity = null;
		try {
			equity = session.selectOne("EquityDao.selectOneEquity", id);
		} finally {
			session.close();
		}
		return equity;
	}

	/* (non-Javadoc)
	 * @see fr.cph.stock.dao.IDao#update(java.lang.Object)
	 */
	@Override
	public void update(Equity equity) {
		SqlSession session = getSqlSessionFactory();
		try {
			session.update("EquityDao.updateOneEquity", equity);
			session.commit();
		} finally {
			session.close();
		}
		
	}

	/* (non-Javadoc)
	 * @see fr.cph.stock.dao.IDao#delete(java.lang.Object)
	 */
	@Override
	public void delete(Equity equity) {
		SqlSession session = getSqlSessionFactory();
		try {
			session.delete("EquityDao.deleteOneEquity", equity);
			session.commit();
		} finally {
			session.close();
		}
	}

}
