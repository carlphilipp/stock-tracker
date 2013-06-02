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

import fr.cph.stock.entities.Company;

/**
 * This class implements IDao functions and add some more. It access to the Company in DB.
 * 
 * @author Carl-Philipp Harmant
 * 
 */
public class CompanyDaoImpl extends AbstractDao<Company> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.cph.stock.dao.IDao#insert(java.lang.Object)
	 */
	@Override
	public void insert(Company company) {
		SqlSession session = getSqlSessionFactory();
		try {
			session.insert("CompanyDao.insertOneCompany", company);
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
	public Company select(int id) {
		SqlSession session = getSqlSessionFactory();
		Company company = null;
		try {
			company = session.selectOne("CompanyDao.selectOneCompany", id);
		} finally {
			session.close();
		}
		return company;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.cph.stock.dao.IDao#update(java.lang.Object)
	 */
	@Override
	public void update(Company company) {
		SqlSession session = getSqlSessionFactory();
		try {
			session.update("CompanyDao.updateOneCompany", company);
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
	public void delete(Company company) {
		SqlSession session = getSqlSessionFactory();
		try {
			session.delete("CompanyDao.deleteOneCompany", company);
			session.commit();
		} finally {
			session.close();
		}
	}

	/**
	 * Get a company
	 * 
	 * @param yahooId
	 *            the yahoo id
	 * @return a company
	 */
	public Company selectWithYahooId(String yahooId) {
		SqlSession session = getSqlSessionFactory();
		Company company = null;
		try {
			company = session.selectOne("CompanyDao.selectOneCompanyWithYahooId", yahooId);
		} finally {
			session.close();
		}
		return company;
	}

	/**
	 * Get all the companies in DB
	 * 
	 * @param realTime
	 *            a boolean that represents a real time data information. If
	 * @return a list of company
	 */
	public List<Company> selectAllCompany(boolean realTime) {
		SqlSession session = getSqlSessionFactory();
		List<Company> companies = null;
		try {
			companies = session.selectList("CompanyDao.selectAllCompanyNotRealTime", realTime);
		} finally {
			session.close();
		}
		return companies;
	}

	/**
	 * Get a list of unsed company
	 * 
	 * @return a list of integer representing company ids.
	 */
	public List<Integer> selectAllUnusedCompanyIds() {
		SqlSession session = getSqlSessionFactory();
		List<Integer> companies = null;
		try {
			companies = session.selectList("CompanyDao.selectAllUnusedCompanyIds");
		} finally {
			session.close();
		}
		return companies;
	}

}
