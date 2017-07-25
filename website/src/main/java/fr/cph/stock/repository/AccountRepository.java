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

import fr.cph.stock.entities.Account;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * This class implements DAO functions and add some more. It access to the Account in DB.
 *
 * @author Carl-Philipp Harmant
 */
@RequiredArgsConstructor
@Component
public class AccountRepository implements DAO<Account> {

	private static final String INSERT = "fr.cph.stock.repository.AccountRepository.insertOneAccount";
	private static final String SELECT = "fr.cph.stock.repository.AccountRepository.selectOneAccount";
	private static final String UPDATE = "fr.cph.stock.repository.AccountRepository.updateOneAccount";
	private static final String DELETE = "fr.cph.stock.repository.AccountRepository.deleteOneAccount";

	@NonNull
	private final SqlSession session;

	@Override
	public void insert(final Account account) {
		session.insert(INSERT, account);
	}

	@Override
	public Optional<Account> select(final int id) {
		return Optional.ofNullable(session.selectOne(SELECT, id));
	}

	@Override
	public void update(final Account account) {
		session.update(UPDATE, account);
	}

	@Override
	public void delete(final Account account) {
		session.delete(DELETE, account);
	}
}
