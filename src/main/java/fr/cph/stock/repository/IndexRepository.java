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

import fr.cph.stock.entities.Index;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * This class implements DAO functions and add some more. It access to the Index in DB.
 *
 * @author Carl-Philipp Harmant
 */
@RequiredArgsConstructor
@Component
public class IndexRepository implements DAO<Index> {

	private static final String INSERT = "fr.cph.stock.repository.IndexRepository.insertOneIndex";
	private static final String SELECT = "fr.cph.stock.repository.IndexRepository.selectOneIndex";
	private static final String UPDATE = "fr.cph.stock.repository.IndexRepository.updateOneIndex";
	private static final String DELETE = "fr.cph.stock.repository.IndexRepository.deleteOneIndex";
	private static final String SELECT_FROM_TO = "fr.cph.stock.repository.IndexRepository.selectListIndexFromTo";
	private static final String SELECT_LAST = "fr.cph.stock.repository.IndexRepository.selectLastIndex";

	@NonNull
	private final SqlSession session;

	@Override
	public final void insert(final Index index) {
		session.insert(INSERT, index);
	}

	@Override
	public final Optional<Index> select(final int id) {
		return Optional.ofNullable(session.selectOne(SELECT, id));
	}

	@Override
	public final void update(final Index index) {
		session.update(UPDATE, index);
	}

	@Override
	public final void delete(final Index index) {
		session.delete(DELETE, index);
	}

	public final List<Index> selectListFrom(final String yahooId, final Date from, final Date to) {
		final Map<String, Object> map = new HashMap<>();
		map.put("yahooId", yahooId);
		map.put("from", from);
		map.put("to", to);
		return session.selectList(SELECT_FROM_TO, map);
	}

	public final Optional<Index> selectLast(final String yahooId) {
		return Optional.ofNullable(session.selectOne(SELECT_LAST, yahooId));
	}
}
