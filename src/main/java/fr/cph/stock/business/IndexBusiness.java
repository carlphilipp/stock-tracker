package fr.cph.stock.business;

import fr.cph.stock.entities.Index;
import fr.cph.stock.exception.YahooException;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public interface IndexBusiness {

	/**
	 * Get index information
	 *
	 * @param yahooId the yahoo id
	 * @param from    the from date
	 * @param to      the to date
	 * @return a list of index
	 */
	List<Index> getIndexes(final String yahooId, final Date from, final Date to);

	/**
	 * Update an index
	 *
	 * @param yahooId the yahoo id
	 * @throws YahooException the yahoo exception
	 */
	void updateIndex(final String yahooId) throws YahooException;

	/**
	 * Don't remember what it does
	 *
	 * @param yahooId  the yahoo id
	 * @param timeZone the timezone
	 * @throws YahooException the yahoo exception
	 */
	void checkUpdateIndex(final String yahooId, final TimeZone timeZone) throws YahooException;
}
