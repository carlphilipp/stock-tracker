package fr.cph.stock.exception;

/**
 * Yahoo unknown exception
 *
 * @author Carl-Philipp Harmant
 *
 */
public class YahooUnknownTickerException extends YahooException {

	private static final long serialVersionUID = 149761237308430399L;

	/** Error message **/
	public static final String TOKEN_UNKNOWN = " yahooID is unknown.";

	/**
	 *
	 * @param message
	 *            the message
	 */
	public YahooUnknownTickerException(final String message) {
		super(message);
	}

	/**
	 *
	 * @param message
	 *            the message
	 * @param e
	 *            the exception
	 */
	public YahooUnknownTickerException(final String message, final Exception e) {
		super(message, e);
	}

}
