package fr.cph.stock.external.web.currency.xchange;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Rate {
	@JsonProperty("Rate")
	private double rate;
}
