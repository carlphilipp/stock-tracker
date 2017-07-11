package fr.cph.stock.external;

import com.google.gson.JsonObject;
import fr.cph.stock.exception.YahooException;

public interface YahooGateway {

	JsonObject getJSONObject(String yqlRequest) throws YahooException;

	<T> T getObject(String yqlQuery, Class<T> clazz) throws YahooException;
}
