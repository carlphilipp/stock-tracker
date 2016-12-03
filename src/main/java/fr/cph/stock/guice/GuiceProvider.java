package fr.cph.stock.guice;

import com.google.gson.Gson;
import com.google.inject.Provides;

public class GuiceProvider {

	@Provides
	public Gson gson() {
		return new Gson();
	}
}
