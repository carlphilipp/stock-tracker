package fr.cph.stock.external.web.currency.xchange;

import lombok.Getter;
import lombok.ToString;

import java.util.Date;

@Getter
@ToString
public class Query {
	private double count;
	private Date created;
	private String lang;
	private Results results;
}
