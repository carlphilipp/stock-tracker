package fr.cph.stock.external.web.currency.history;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class Results {
	private List<Quote> quote;
}
