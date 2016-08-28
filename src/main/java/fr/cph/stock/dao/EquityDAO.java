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
import fr.cph.stock.entities.Equity;
import org.apache.ibatis.session.SqlSession;

import java.util.Optional;

/**
 * This class implements DAO functions and add some more. It access to the Equity in DB.
 *
 * @author Carl-Philipp Harmant
 *
 */
@Singleton
public class EquityDAO implements DAO<Equity> {

	private static final String INSERT = "fr.cph.stock.dao.EquityDao.insertOneEquity";
	private static final String SELECT = "fr.cph.stock.dao.EquityDao.selectOneEquity";
	private static final String UPDATE = "fr.cph.stock.dao.EquityDao.updateOneEquity";
	private static final String DELETE = "fr.cph.stock.dao.EquityDao.deleteOneEquity";

	private final SessionManager sessionManager = SessionManager.INSTANCE;

	@Override
	public final void insert(final Equity equity) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(true)) {
			session.insert(INSERT, equity);
		}
	}

	@Override
	public final Optional<Equity> select(final int id) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(false)) {
			return Optional.ofNullable(session.selectOne(SELECT, id));
		}
	}

	@Override
	public final void update(final Equity equity) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(true)) {
			session.update(UPDATE, equity);
		}
	}

	@Override
	public final void delete(final Equity equity) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(true)) {
			session.delete(DELETE, equity);
		}
	}
}
