package fr.cph.stock.business;

import fr.cph.stock.entities.Account;
import fr.cph.stock.entities.Portfolio;
import fr.cph.stock.entities.ShareValue;
import fr.cph.stock.exception.YahooException;

import java.util.Calendar;

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
	 * @param taxe              the taxe
	 * @param commentary        the commentary
	 */
	void updateCurrentShareValue(final Portfolio portfolio, final Account account, final Double liquidityMovement, final Double yield, final Double buy, final Double sell, final Double taxe, final String commentary);

	/**
	 * Delete a share value
	 *
	 * @param sv the share value
	 */
	void deleteShareValue(final ShareValue sv);

	/**
	 * Add a share value
	 *
	 * @param share a sharevalue
	 */
	void addShareValue(final ShareValue share);

	/**
	 * Auto update all companies data and if there is no error, auto update the share value of selected user
	 *
	 * @param date the date
	 * @throws YahooException the yahoo exception
	 */
	void autoUpdateUserShareValue(final Calendar date) throws YahooException;

	/**
	 * Get a share value
	 *
	 * @param id the id of the sharevalue to get
	 * @return a share value
	 */
	ShareValue selectOneShareValue(final int id);

	/**
	 * Update commentare in share value
	 *
	 * @param sv the share value
	 */
	void updateCommentaryShareValue(final ShareValue sv);
}