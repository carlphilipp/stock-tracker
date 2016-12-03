package fr.cph.stock.external.web.currency;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Rate {
	private String id;
	@SerializedName("Name")
	private String name;
	@SerializedName("Rate")
	private double rate;
	@SerializedName("Date")
	private String date;
	@SerializedName("Time")
	private String time;
	@SerializedName("Ask")
	private double ask;
	@SerializedName("Bid")
	private double bid;

	public double getRate() {
		return rate;
	}
}
