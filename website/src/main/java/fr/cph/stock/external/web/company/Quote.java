package fr.cph.stock.external.web.company;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Quote {
	private String symbol;
	@SerializedName("StockExchange")
	private String stockExchange;
	@SerializedName("Name")
	private String name;
	@SerializedName("Currency")
	private String currency;
	@SerializedName("DividendYield")
	private Double dividendYield;
	@SerializedName("LastTradePriceOnly")
	private Double lastTradePriceOnly;
	@SerializedName("MarketCapitalization")
	private String marketCapitalization;
	@SerializedName("PreviousClose")
	private Double previousClose;
	@SerializedName("ChangeinPercent")
	private String changeinPercent;
	@SerializedName("YearLow")
	private Double yearLow;
	@SerializedName("YearHigh")
	private Double yearHigh;
}
