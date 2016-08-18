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

import fr.cph.stock.dao.mybatis.SessionManager;
import fr.cph.stock.entities.Company;
import org.apache.ibatis.session.SqlSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.cph.stock.util.Constants.MANUAL;

/**
 * This class implements DAO functions and add some more. It access to the Company in DB.
 *
 * @author Carl-Philipp Harmant
 *
 */
public enum CompanyDAO implements DAO<Company> {

	INSTANCE;

	private SessionManager sessionManager = SessionManager.INSTANCE;

	@Override
	public final void insert(final Company company) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(true)) {
			session.insert("CompanyDao.insertOneCompany", company);
		}
	}

	@Override
	public final Company select(final int id) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(false)) {
			return session.selectOne("CompanyDao.selectOneCompany", id);
		}
	}

	@Override
	public final void update(final Company company) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(true)) {
			session.update("CompanyDao.updateOneCompany", company);
		}
	}

	@Override
	public final void delete(final Company company) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(true)) {
			session.delete("CompanyDao.deleteOneCompany", company);
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
		try (final SqlSession session = sessionManager.getSqlSessionFactory(false)) {
			return session.selectOne("CompanyDao.selectOneCompanyWithYahooId", yahooId);
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
		final Map<String, Boolean> options = new HashMap<>();
		options.put("realTime", realTime);
		// Remove manual companies
		options.put(MANUAL, false);
		try (final SqlSession session = sessionManager.getSqlSessionFactory(false)) {
			return session.selectList("CompanyDao.selectAllCompanyNotRealTime", options);
		}
	}

	/**
	 * Get a list of unsed company
	 *
	 * @return a list of integer representing company ids.
	 */
	public final List<Integer> selectAllUnusedCompanyIds() {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(false)) {
			return session.selectList("CompanyDao.selectAllUnusedCompanyIds");
		}
	}
}
