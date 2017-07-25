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

import fr.cph.stock.entities.CurrencyData;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * This class implements DAO functions and add some more. It access to the Currency in DB.
 *
 * @author Carl-Philipp Harmant
 */
@RequiredArgsConstructor
@Component
public class CurrencyRepository implements DAO<CurrencyData> {

	private static final String INSERT = "fr.cph.stock.repository.CurrencyData.insertOneCurrencyData";
	private static final String SELECT = "fr.cph.stock.repository.CurrencyData.selectOneCurrencyData";
	private static final String UPDATE = "fr.cph.stock.repository.CurrencyData.updateOneCurrencyData";
	private static final String DELETE = "fr.cph.stock.repository.CurrencyData.deleteOneCurrencyData";
	private static final String SELECT_WITH_PARAM = "fr.cph.stock.repository.CurrencyData.selectOneCurrencyDataWithParam";
	private static final String SELECT_LIST = "fr.cph.stock.repository.CurrencyData.selectListCurrencyData";
	private static final String SELECT_LIST_ALL = "fr.cph.stock.repository.CurrencyData.selectListAllCurrencyData";

	@NonNull
	private final SqlSession session;

	@Override
	public final void insert(final CurrencyData currencyData) {
		session.insert(INSERT, currencyData);
	}

	@Override
	public final Optional<CurrencyData> select(final int id) {
		return Optional.ofNullable(session.selectOne(SELECT, id));
	}

	@Override
	public final void update(final CurrencyData currencyData) {
		session.update(UPDATE, currencyData);
	}

	@Override
	public final void delete(final CurrencyData currencyData) {
		session.delete(DELETE, currencyData);
	}

	public final Optional<CurrencyData> selectOneCurrencyDataWithParam(final CurrencyData currencyD) {
		return Optional.ofNullable(session.selectOne(SELECT_WITH_PARAM, currencyD));
	}

	public final List<CurrencyData> selectListCurrency(final String currency) {
		return session.selectList(SELECT_LIST, currency);
	}

	public final List<CurrencyData> selectListAllCurrency() {
		return session.selectList(SELECT_LIST_ALL);
	}
}
