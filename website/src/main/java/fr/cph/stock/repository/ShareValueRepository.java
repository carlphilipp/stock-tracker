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

import fr.cph.stock.entities.ShareValue;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * This class implements DAO functions and add some more. It access to the ShareValue in DB.
 *
 * @author Carl-Philipp Harmant
 */
@RequiredArgsConstructor
@Component
public class ShareValueRepository implements DAO<ShareValue> {

	private static final String INSERT = "fr.cph.stock.repository.ShareValueRepository.insertOneShareValue";
	private static final String SELECT = "fr.cph.stock.repository.ShareValueRepository.selectOneShareValue";
	private static final String UPDATE = "fr.cph.stock.repository.ShareValueRepository.updateOneShareValue";
	private static final String DELETE = "fr.cph.stock.repository.ShareValueRepository.deleteOneShareValue";
	private static final String INSERT_WITH_DATE = "fr.cph.stock.repository.ShareValueRepository.insertOneShareValueWithDate";
	private static final String SELECT_LAST_VALUE = "fr.cph.stock.repository.ShareValueRepository.selectLastValue";

	@NonNull
	private final SqlSession session;

	@Override
	public final void insert(final ShareValue shareValue) {
		session.insert(INSERT, shareValue);
	}

	public final void insertWithDate(final ShareValue shareValue) {
		session.insert(INSERT_WITH_DATE, shareValue);
	}

	@Override
	public final Optional<ShareValue> select(final int id) {
		return Optional.ofNullable(session.selectOne(SELECT, id));
	}

	@Override
	public final void update(final ShareValue shareValue) {
		session.update(UPDATE, shareValue);
	}

	@Override
	public final void delete(final ShareValue shareValue) {
		session.delete(DELETE, shareValue);
	}

	/**
	 * Get the last share value of a user
	 *
	 * @param userId the user id
	 * @return a share value
	 */
	public final Optional<ShareValue> selectLastValue(final int userId) {
		return Optional.ofNullable(session.selectOne(SELECT_LAST_VALUE, userId));
	}
}
