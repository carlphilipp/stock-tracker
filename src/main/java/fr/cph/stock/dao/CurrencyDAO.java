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
import fr.cph.stock.entities.CurrencyData;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 * This class implements DAO functions and add some more. It access to the Currency in DB.
 *
 * @author Carl-Philipp Harmant
 *
 */
public class CurrencyDAO implements DAO<CurrencyData> {

	private SessionManager sessionManager = SessionManager.INSTANCE;

	@Override
	public final void insert(final CurrencyData currencyData) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(true)) {
			session.insert("CurrencyData.insertOneCurrencyData", currencyData);
		}

	}

	@Override
	public final CurrencyData select(final int id) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(false)) {
			return session.selectOne("CurrencyData.selectOneCurrencyData", id);
		}
	}

	@Override
	public final void update(final CurrencyData currencyData) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(false)) {
			session.update("CurrencyData.updateOneCurrencyData", currencyData);
		}
	}

	@Override
	public final void delete(final CurrencyData currencyData) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(true)) {
			session.delete("CurrencyData.deleteOneCurrencyData", currencyData);
		}
	}

	/**
	 * Get one currency data
	 *
	 * @param currencyD
	 *            the currency data
	 * @return a currency data
	 */
	public final CurrencyData selectOneCurrencyDataWithParam(final CurrencyData currencyD) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(false)) {
			return session.selectOne("CurrencyData.selectOneCurrencyDataWithParam", currencyD);
		}
	}

	/**
	 * Get a list of currency data
	 *
	 * @param currency
	 *            the currency
	 * @return a list of currency
	 */
	public final List<CurrencyData> selectListCurrency(final String currency) {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(false)) {
			return session.selectList("CurrencyData.selectListCurrencyData", currency);
		}
	}

	/**
	 * Get all currency data
	 *
	 * @return a list of currency data
	 */
	public final List<CurrencyData> selectListAllCurrency() {
		try (final SqlSession session = sessionManager.getSqlSessionFactory(false)) {
			return session.selectList("CurrencyData.selectListAllCurrencyData");
		}
	}
}
