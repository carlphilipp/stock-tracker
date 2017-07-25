/**
 * Copyright 2017 Carl-Philipp Harmant
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

package fr.cph.stock.repository;

import fr.cph.stock.repository.mybatis.SessionManager;
import fr.cph.stock.entities.Company;
import org.apache.ibatis.session.SqlSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static fr.cph.stock.util.Constants.MANUAL;

/**
 * This class implements Repository functions and add some more. It access to the Company in DB.
 *
 * @author Carl-Philipp Harmant
 */
@org.springframework.stereotype.Repository
public class CompanyRepository implements Repository<Company> {

	private static final String INSERT = "fr.cph.stock.repository.CompanyRepository.insertOneCompany";
	private static final String SELECT = "fr.cph.stock.repository.CompanyRepository.selectOneCompany";
	private static final String UPDATE = "fr.cph.stock.repository.CompanyRepository.updateOneCompany";
	private static final String DELETE = "fr.cph.stock.repository.CompanyRepository.deleteOneCompany";
	private static final String SELECT_WITH_ID = "fr.cph.stock.repository.CompanyRepository.selectOneCompanyWithYahooId";
	private static final String SELECT_NOT_REAL_TIME = "fr.cph.stock.repository.CompanyRepository.selectAllCompanyNotRealTime";
	private static final String SELECT_UNUSED = "fr.cph.stock.repository.CompanyRepository.selectAllUnusedCompanyIds";

	private final SessionManager sessionManager = SessionManager.INSTANCE;

	@Override
	public void insert(final Company company) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(true)) {
			session.insert(INSERT, company);
		}
	}

	@Override
	public Optional<Company> select(final int id) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(false)) {
			return Optional.ofNullable(session.selectOne(SELECT, id));
		}
	}

	@Override
	public void update(final Company company) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(true)) {
			session.update(UPDATE, company);
		}
	}

	@Override
	public void delete(final Company company) {
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
	public Optional<Company> selectWithYahooId(final String yahooId) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(false)) {
			return Optional.ofNullable(session.selectOne(SELECT_WITH_ID, yahooId));
		}
	}

	/**
	 * Get all the companies in DB
	 *
	 * @param realTime a boolean that represents a real time data information. If
	 * @return a list of company
	 */
	public List<Company> selectAllCompany(final boolean realTime) {
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
	public List<Integer> selectAllUnusedCompanyIds() {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(false)) {
			return session.selectList(SELECT_UNUSED);
		}
	}
}
