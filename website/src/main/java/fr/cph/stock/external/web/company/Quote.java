package fr.cph.stock.external.web.company;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Quote {
	private String symbol;
	@JsonProperty("StockExchange")
	@SerializedName("StockExchange")
	private String stockExchange;
	@JsonProperty("Name")
	@SerializedName("Name")
	private String name;
	@JsonProperty("Currency")
	@SerializedName("Currency")
	private String currency;
	@JsonProperty("DividendYield")
	@SerializedName("DividendYield")
	private Double dividendYield;
	@JsonProperty("LastTradePriceOnly")
	@SerializedName("LastTradePriceOnly")
	private Double lastTradePriceOnly;
	@JsonProperty("MarketCapitalization")
	@SerializedName("MarketCapitalization")
	private String marketCapitalization;
	@JsonProperty("PreviousClose")
	@SerializedName("PreviousClose")
	private Double previousClose;
	@JsonProperty("ChangeinPercent")
	@SerializedName("ChangeinPercent")
	private String changeinPercent;
	@JsonProperty("YearLow")
	@SerializedName("YearLow")
	private Double yearLow;
	@JsonProperty("YearHigh")
	@SerializedName("YearHigh")
	private Double yearHigh;
}
