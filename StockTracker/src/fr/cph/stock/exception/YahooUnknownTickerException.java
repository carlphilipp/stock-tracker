package fr.cph.stock.exception;

public class YahooUnknownTickerException extends YahooException {

	private static final long serialVersionUID = 149761237308430399L;

	/** Error message **/
	public static String TOCKEN_UNKNOWN = " yahooID is unknown.";

	public YahooUnknownTickerException(String message) {
		super(message);
	}

	public YahooUnknownTickerException(String message, Exception e) {
		super(message, e);
	}

}
