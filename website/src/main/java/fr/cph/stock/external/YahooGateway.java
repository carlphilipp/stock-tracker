package fr.cph.stock.external;

import fr.cph.stock.exception.YahooException;

public interface YahooGateway {

	<T> T getObject(String yqlQuery, Class<T> clazz) throws YahooException;
}
