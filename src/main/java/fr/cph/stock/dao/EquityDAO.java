/**
 * Copyright 2013 Carl-Philipp Harmant
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

import fr.cph.stock.entities.Equity;
import org.apache.ibatis.session.SqlSession;

/**
 * This class implements IDAO functions and add some more. It access to the Equity in DB.
 *
 * @author Carl-Philipp Harmant
 *
 */
public class EquityDAO extends AbstractDAO<Equity> {

	@Override
	public final void insert(final Equity equity) {
		try (final SqlSession session = getSqlSessionFactory(true)) {
			session.insert("EquityDao.insertOneEquity", equity);
		}

	}

	@Override
	public final Equity select(final int id) {
		try (final SqlSession session = getSqlSessionFactory(false)) {
			return session.selectOne("EquityDao.selectOneEquity", id);
		}
	}

	@Override
	public final void update(final Equity equity) {
		try (final SqlSession session = getSqlSessionFactory(true)) {
			session.update("EquityDao.updateOneEquity", equity);
		}
	}

	@Override
	public final void delete(final Equity equity) {
		try (final SqlSession session = getSqlSessionFactory(true)) {
			session.delete("EquityDao.deleteOneEquity", equity);
		}
	}
}
