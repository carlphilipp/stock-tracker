package fr.cph.stock.external.web.company;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Quote {
	private String symbol;
	@JsonProperty("StockExchange")
	private String stockExchange;
	@JsonProperty("Name")
	private String name;
	@JsonProperty("Currency")
	private String currency;
	@JsonProperty("DividendYield")
	private Double dividendYield;
	@JsonProperty("LastTradePriceOnly")
	private Double lastTradePriceOnly;
	@JsonProperty("MarketCapitalization")
	private String marketCapitalization;
	@JsonProperty("PreviousClose")
	private Double previousClose;
	@JsonProperty("ChangeinPercent")
	private String changeinPercent;
	@JsonProperty("YearLow")
	private Double yearLow;
	@JsonProperty("YearHigh")
	private Double yearHigh;
}
