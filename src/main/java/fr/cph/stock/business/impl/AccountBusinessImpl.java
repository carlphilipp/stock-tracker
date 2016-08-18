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

package fr.cph.stock.business.impl;

import fr.cph.stock.business.AccountBusiness;
import fr.cph.stock.dao.AccountDAO;
import fr.cph.stock.entities.Account;

/**
 * AccountBusinessImpl class that access database and process data
 *
 * @author Carl-Philipp Harmant
 * @version 1
 */
public enum AccountBusinessImpl implements AccountBusiness {

	INSTANCE;

	private final AccountDAO accountDAO;

	AccountBusinessImpl() {
		accountDAO = new AccountDAO();
	}

	// Account
	@Override
	public final void addAccount(final Account account) {
		accountDAO.insert(account);
	}

	@Override
	public final void updateAccount(final Account account) {
		accountDAO.update(account);
	}

	@Override
	public final void deleteAccount(final Account account) {
		accountDAO.delete(account);
	}
}