package fr.cph.stock.business.impl;

import fr.cph.stock.business.IndexBusiness;
import fr.cph.stock.dao.IndexDAO;
import fr.cph.stock.entities.Index;
import fr.cph.stock.exception.YahooException;
import fr.cph.stock.external.IExternalDataAccess;
import fr.cph.stock.external.YahooExternalDataAccess;
import fr.cph.stock.util.Util;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public enum IndexBusinessImpl implements IndexBusiness {

	INSTANCE;

	private static final Logger LOG = Logger.getLogger(IndexBusinessImpl.class);
	private static final int PERCENT = 100;
	private static final MathContext MATHCONTEXT = MathContext.DECIMAL32;

	private final IndexDAO indexDAO;
	private final IExternalDataAccess yahoo;

	IndexBusinessImpl() {
		indexDAO = IndexDAO.INSTANCE;
		yahoo = new YahooExternalDataAccess();
	}

	@Override
	public final List<Index> getIndexes(final String yahooId, final Date from, final Date to) {
		final List<Index> indexes = indexDAO.selectListFrom(yahooId, from, to);
		for (int i = 0; i < indexes.size(); i++) {
			final Index currentIndex = indexes.get(i);
			if (i == 0) {
				currentIndex.setShareValue((double) PERCENT);
				// To make it pretty in chart
				currentIndex.setDate(from);
			} else {
				final Index lastIndex = indexes.get(i - 1);
				double shareValue = currentIndex.getValue() * lastIndex.getShareValue() / lastIndex.getValue();
				shareValue = new BigDecimal(Double.toString(shareValue), MATHCONTEXT).doubleValue();
				currentIndex.setShareValue(shareValue);
			}
		}
		return indexes;
	}

	@Override
	public final void updateIndex(final String yahooId) throws YahooException {
		final Index index = yahoo.getIndexData(yahooId);
		indexDAO.insert(index);
	}

	@Override
	public final void checkUpdateIndex(final String yahooId, final TimeZone timeZone) throws YahooException {
		final Index index = indexDAO.selectLast(yahooId);
		final Calendar currentCal = Util.getCurrentCalendarInTimeZone(timeZone);
		final Calendar indexCal = Util.getDateInTimeZone(index.getDate(), timeZone);
		LOG.debug("Check update for " + yahooId + " in timezone : " + timeZone.getDisplayName());
		LOG.debug("CurrentHour: " + currentCal.get(Calendar.HOUR_OF_DAY) + "h" + currentCal.get(Calendar.MINUTE) + " / indexHour: "
			+ indexCal.get(Calendar.HOUR_OF_DAY) + "h" + indexCal.get(Calendar.MINUTE));
		if (!Util.isSameDay(currentCal, indexCal)) {
			LOG.debug("Update index after checking! " + yahooId);
			updateIndex(yahooId);
		}
	}
}
