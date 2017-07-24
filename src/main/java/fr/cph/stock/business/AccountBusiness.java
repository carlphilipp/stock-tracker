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

package fr.cph.stock.business;

import fr.cph.stock.entities.Account;

import java.util.Optional;

/**
 * Interface defining function that access database and process information
 *
 * @author Carl-Philipp Harmant
 * @version 1
 */
public interface AccountBusiness {

	/**
	 * Get an account
	 *
	 * @param account the account to add
	 */
	Optional<Account> getAccount(int id);

	/**
	 * Add an account
	 *
	 * @param account the account to add
	 */
	void addAccount(Account account);

	/**
	 * Update an account
	 *
	 * @param account the account to update
	 */
	void updateAccount(Account account);

	/**
	 * Delete account
	 *
	 * @param account the account to delete
	 */
	void deleteAccount(Account account);
}
