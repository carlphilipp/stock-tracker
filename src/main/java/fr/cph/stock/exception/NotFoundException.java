package fr.cph.stock.exception;

public class NotFoundException extends RuntimeException {

	public NotFoundException(final int id) {
		super("Id not found: " + id);
	}

	public NotFoundException(final String ticker) {
		super("Ticker not found: " + ticker);
	}
}
