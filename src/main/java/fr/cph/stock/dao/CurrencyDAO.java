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

import fr.cph.stock.entities.CurrencyData;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 * This class implements IDAO functions and add some more. It access to the Currency in DB.
 *
 * @author Carl-Philipp Harmant
 *
 */
public class CurrencyDAO extends AbstractDAO<CurrencyData> {

	@Override
	public final void insert(final CurrencyData currencyData) {
		final SqlSession session = getSqlSessionFactory(true);
		try {
			session.insert("CurrencyData.insertOneCurrencyData", currencyData);
		} finally {
			session.close();
		}

	}

	@Override
	public final CurrencyData select(final int id) {
		final SqlSession session = getSqlSessionFactory(false);
		try {
			return session.selectOne("CurrencyData.selectOneCurrencyData", id);
		} finally {
			session.close();
		}
	}

	@Override
	public final void update(final CurrencyData currencyData) {
		final SqlSession session = getSqlSessionFactory(false);
		try {
			session.update("CurrencyData.updateOneCurrencyData", currencyData);
		} finally {
			session.close();
		}
	}

	@Override
	public final void delete(final CurrencyData currencyData) {
		final SqlSession session = getSqlSessionFactory(true);
		try {
			session.delete("CurrencyData.deleteOneCurrencyData", currencyData);
		} finally {
			session.close();
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
		final SqlSession session = getSqlSessionFactory(false);
		try {
			return session.selectOne("CurrencyData.selectOneCurrencyDataWithParam", currencyD);
		} finally {
			session.close();
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
		final SqlSession session = getSqlSessionFactory(false);
		try {
			return session.selectList("CurrencyData.selectListCurrencyData", currency);
		} finally {
			session.close();
		}
	}

	/**
	 * Get all currency data
	 *
	 * @return a list of currency data
	 */
	public final List<CurrencyData> selectListAllCurrency() {
		final SqlSession session = getSqlSessionFactory(false);
		try {
			return session.selectList("CurrencyData.selectListAllCurrencyData");
		} finally {
			session.close();
		}
	}
}
