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

import fr.cph.stock.entities.Company;
import org.apache.ibatis.session.SqlSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.cph.stock.util.Constants.MANUAL;

/**
 * This class implements IDAO functions and add some more. It access to the Company in DB.
 * 
 * @author Carl-Philipp Harmant
 * 
 */
public class CompanyDAO extends AbstractDAO<Company> {

	@Override
	public final void insert(final Company company) {
		final SqlSession session = getSqlSessionFactory();
		try {
			session.insert("CompanyDao.insertOneCompany", company);
			session.commit();
		} finally {
			session.close();
		}
	}

	@Override
	public final Company select(final int id) {
		final SqlSession session = getSqlSessionFactory();
		try {
			return session.selectOne("CompanyDao.selectOneCompany", id);
		} finally {
			session.close();
		}
	}

	@Override
	public final void update(final Company company) {
		final SqlSession session = getSqlSessionFactory();
		try {
			session.update("CompanyDao.updateOneCompany", company);
			session.commit();
		} finally {
			session.close();
		}
	}

	@Override
	public final void delete(final Company company) {
		final SqlSession session = getSqlSessionFactory();
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
	public final Company selectWithYahooId(final String yahooId) {
		final SqlSession session = getSqlSessionFactory();
		try {
			return session.selectOne("CompanyDao.selectOneCompanyWithYahooId", yahooId);
		} finally {
			session.close();
		}
	}

	/**
	 * Get all the companies in DB
	 * 
	 * @param realTime
	 *            a boolean that represents a real time data information. If
	 * @return a list of company
	 */
	public final List<Company> selectAllCompany(final boolean realTime) {
		final SqlSession session = getSqlSessionFactory();
		final Map<String, Boolean> options = new HashMap<>();
		options.put("realTime", realTime);
		// Remove manual companies
		options.put(MANUAL, false);
		try {
			return session.selectList("CompanyDao.selectAllCompanyNotRealTime", options);
		} finally {
			session.close();
		}
	}

	/**
	 * Get a list of unsed company
	 * 
	 * @return a list of integer representing company ids.
	 */
	public final List<Integer> selectAllUnusedCompanyIds() {
		final SqlSession session = getSqlSessionFactory();
		try {
			return session.selectList("CompanyDao.selectAllUnusedCompanyIds");
		} finally {
			session.close();
		}
	}
}
