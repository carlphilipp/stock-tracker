package fr.cph.stock.business;

import fr.cph.stock.entities.Company;
import fr.cph.stock.entities.Equity;
import fr.cph.stock.exception.EquityException;
import fr.cph.stock.exception.YahooException;

import java.io.UnsupportedEncodingException;

public interface EquityBusiness {

	void createEquity(final int userId, final String ticker, final Equity equity) throws EquityException, YahooException;

	void createManualEquity(final int userId, final Company company, final Equity equity) throws EquityException;

	void updateEquity(final int userId, final String ticker, final Equity equity) throws UnsupportedEncodingException, YahooException;

	void deleteEquity(final Equity equity);
}
