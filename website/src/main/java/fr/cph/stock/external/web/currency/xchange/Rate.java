package fr.cph.stock.external.web.currency.xchange;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Rate {
	@JsonProperty("Rate")
	@SerializedName("Rate")
	private double rate;
}
