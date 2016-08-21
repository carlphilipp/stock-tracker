/**
 * Copyright 2016 Carl-Philipp Harmant
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

import com.google.inject.Singleton;
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
 */
@Singleton
public class CompanyDAO implements DAO<Company> {

	private static final String INSERT = "CompanyDao.insertOneCompany";
	private static final String SELECT = "CompanyDao.selectOneCompany";
	private static final String UPDATE = "CompanyDao.updateOneCompany";
	private static final String DELETE = "CompanyDao.deleteOneCompany";
	private static final String SELECT_WITH_ID = "CompanyDao.selectOneCompanyWithYahooId";
	private static final String SELECT_NOT_REAL_TIME = "CompanyDao.selectAllCompanyNotRealTime";
	private static final String SELECT_UNUSED = "CompanyDao.selectAllUnusedCompanyIds";

	private SessionManager sessionManager = SessionManager.INSTANCE;

	@Override
	public final void insert(final Company company) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(true)) {
			session.insert(INSERT, company);
		}
	}

	@Override
	public final Company select(final int id) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(false)) {
			return session.selectOne(SELECT, id);
		}
	}

	@Override
	public final void update(final Company company) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(true)) {
			session.update(UPDATE, company);
		}
	}

	@Override
	public final void delete(final Company company) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(true)) {
			session.delete(DELETE, company);
		}
	}

	/**
	 * Get a company
	 *
	 * @param yahooId the yahoo id
	 * @return a company
	 */
	public final Company selectWithYahooId(final String yahooId) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(false)) {
			return session.selectOne(SELECT_WITH_ID, yahooId);
		}
	}

	/**
	 * Get all the companies in DB
	 *
	 * @param realTime a boolean that represents a real time data information. If
	 * @return a list of company
	 */
	public final List<Company> selectAllCompany(final boolean realTime) {
		final Map<String, Boolean> options = new HashMap<>();
		options.put("realTime", realTime);
		// Remove manual companies
		options.put(MANUAL, false);
		try (final SqlSession session = sessionManager.getSqlSessionFactory(false)) {
			return session.selectList(SELECT_NOT_REAL_TIME, options);
		}
	}

	/**
	 * Get a list of unsed company
	 *
	 * @return a list of integer representing company ids.
	 */
	public final List<Integer> selectAllUnusedCompanyIds() {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(false)) {
			return session.selectList(SELECT_UNUSED);
		}
	}
}
