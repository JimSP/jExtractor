package br.com.cafebinario.exception;

public class DaoException extends Exception {
	private static final long serialVersionUID = 7236810302984395541L;

	public DaoException(String message) {
		super(message);
	}

	public DaoException(String message, Exception ex) {
		super(message, ex);
	}
}
