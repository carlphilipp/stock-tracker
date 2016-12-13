package fr.cph.stock.external.web.currency.xchange;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Rate {
	@SerializedName("Rate")
	private double rate;
}
