package fr.cph.stock.business;

import fr.cph.stock.entities.Company;
import fr.cph.stock.entities.Equity;
import fr.cph.stock.exception.EquityException;
import fr.cph.stock.exception.YahooException;

public interface EquityBusiness {

	void createEquity(int userId, String ticker, Equity equity) throws EquityException, YahooException;

	void createManualEquity(int userId, Company company, Equity equity) throws EquityException;

	void updateEquity(int userId, String ticker, Equity equity) throws YahooException;

	void deleteEquity(Equity equity);
}
