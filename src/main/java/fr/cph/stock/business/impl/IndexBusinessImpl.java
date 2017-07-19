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
package fr.cph.stock.business.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import fr.cph.stock.business.IndexBusiness;
import fr.cph.stock.dao.IndexDAO;
import fr.cph.stock.entities.Index;
import fr.cph.stock.exception.NotFoundException;
import fr.cph.stock.exception.YahooException;
import fr.cph.stock.external.ExternalDataAccess;
import fr.cph.stock.util.Util;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Log4j2
@Singleton
@Component
public class IndexBusinessImpl implements IndexBusiness {

	private static final int PERCENT = 100;
	private static final MathContext MATHCONTEXT = MathContext.DECIMAL32;

	@Inject
	private ExternalDataAccess yahoo;
	private IndexDAO indexDAO;

	@Autowired
	@Inject
	public void setIndexDAO(@Named("Index") final IndexDAO dao) {
		indexDAO = dao;
	}

	@Override
	public List<Index> getIndexes(String yahooId, Date from) {
		return getIndexes(yahooId, from, null);
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
		final Index index = indexDAO.selectLast(yahooId).orElseThrow(() -> new NotFoundException(yahooId));
		final Calendar currentCal = Util.getCurrentCalendarInTimeZone(timeZone);
		final Calendar indexCal = Util.getDateInTimeZone(index.getDate(), timeZone);
		log.debug("Check update for {} in timezone : {}", yahooId, timeZone.getDisplayName());
		log.debug("CurrentHour: {}h{} / indexHour: {}h{}", currentCal.get(Calendar.HOUR_OF_DAY), currentCal.get(Calendar.MINUTE), indexCal.get(Calendar.HOUR_OF_DAY), indexCal.get(Calendar.MINUTE));
		if (!Util.isSameDay(currentCal, indexCal)) {
			log.debug("Update index after checking! {}", yahooId);
			updateIndex(yahooId);
		}
	}
}
