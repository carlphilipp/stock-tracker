package fr.cph.stock.external.web.currency.history;

import lombok.Data;

import java.util.Date;

@Data
public class Query {
	private double count;
	private Date created;
	private String lang;
	private Results results;

	@Data
	public static class WrapperQuery {
		private Query query;
	}
}
