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

import fr.cph.stock.entities.Company;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Component;
import fr.cph.stock.util.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This class implements DAO functions and add some more. It access to the Company in DB.
 *
 * @author Carl-Philipp Harmant
 */
@RequiredArgsConstructor
@Component
public class CompanyRepository implements DAO<Company> {

	private static final String INSERT = "fr.cph.stock.repository.CompanyRepository.insertOneCompany";
	private static final String SELECT = "fr.cph.stock.repository.CompanyRepository.selectOneCompany";
	private static final String UPDATE = "fr.cph.stock.repository.CompanyRepository.updateOneCompany";
	private static final String DELETE = "fr.cph.stock.repository.CompanyRepository.deleteOneCompany";
	private static final String SELECT_WITH_ID = "fr.cph.stock.repository.CompanyRepository.selectOneCompanyWithYahooId";
	private static final String SELECT_NOT_REAL_TIME = "fr.cph.stock.repository.CompanyRepository.selectAllCompanyNotRealTime";
	private static final String SELECT_UNUSED = "fr.cph.stock.repository.CompanyRepository.selectAllUnusedCompanyIds";

	@NonNull
	private final SqlSession session;

	@Override
	public void insert(final Company company) {
		session.insert(INSERT, company);
	}

	@Override
	public Optional<Company> select(final int id) {
		return Optional.ofNullable(session.selectOne(SELECT, id));
	}

	@Override
	public void update(final Company company) {
		session.update(UPDATE, company);
	}

	@Override
	public void delete(final Company company) {
		session.delete(DELETE, company);
	}

	public Optional<Company> selectWithYahooId(final String yahooId) {
		return Optional.ofNullable(session.selectOne(SELECT_WITH_ID, yahooId));
	}

	public List<Company> selectAllCompany(final boolean realTime) {
		final Map<String, Boolean> options = new HashMap<>();
		options.put("realTime", realTime);
		// Remove manual companies
		options.put(Constants.MANUAL, false);
		return session.selectList(SELECT_NOT_REAL_TIME, options);
	}

	public List<Integer> selectAllUnusedCompanyIds() {
		return session.selectList(SELECT_UNUSED);
	}
}
