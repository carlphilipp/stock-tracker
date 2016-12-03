package fr.cph.stock.external;

import com.google.gson.JsonObject;
import fr.cph.stock.exception.YahooException;

public interface YahooGateway {
	JsonObject getJSONObject(String yqlRequest) throws YahooException;

	Object getObject(final String yqlQuery, final Class<?>  clazz) throws YahooException;
}
