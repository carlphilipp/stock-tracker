package fr.cph.stock.external.web.currency.history;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Quote {
	@JsonProperty("Close")
	private double close;
}
