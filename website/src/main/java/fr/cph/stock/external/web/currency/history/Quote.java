package fr.cph.stock.external.web.currency.history;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Quote {
	@SerializedName("Close")
	private double close;
}
