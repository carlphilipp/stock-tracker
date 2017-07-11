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
import fr.cph.stock.entities.Portfolio;
import fr.cph.stock.entities.ShareValue;
import fr.cph.stock.exception.YahooException;

import java.util.Calendar;
import java.util.Optional;

public interface ShareValueBusiness {

	/**
	 * Update current share value
	 *
	 * @param portfolio         the portfolio
	 * @param account           the account
	 * @param liquidityMovement the liquidity movement
	 * @param yield             the yield
	 * @param buy               the amount buy
	 * @param sell              the amount sell
	 * @param tax              the tax
	 * @param commentary        the commentary
	 */
	void updateCurrentShareValue(Portfolio portfolio, Account account, Double liquidityMovement, Double yield, Double buy, Double sell, Double tax, String commentary);

	/**
	 * Delete a share value
	 *
	 * @param sv the share value
	 */
	void deleteShareValue(ShareValue sv);

	/**
	 * Add a share value
	 *
	 * @param share a sharevalue
	 */
	void addShareValue(ShareValue share);

	/**
	 * Auto update all companies data and if there is no error, auto update the share value of selected user
	 *
	 * @param date the date
	 * @throws YahooException the yahoo exception
	 */
	void autoUpdateUserShareValue(Calendar date) throws YahooException;

	/**
	 * Get a share value
	 *
	 * @param id the id of the sharevalue to get
	 * @return a share value
	 */
	Optional<ShareValue> selectOneShareValue(int id);

	/**
	 * Update commentare in share value
	 *
	 * @param sv the share value
	 */
	void updateCommentaryShareValue(ShareValue sv);
}
