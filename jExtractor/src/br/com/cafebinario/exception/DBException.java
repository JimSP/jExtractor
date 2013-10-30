package br.com.cafebinario.exception;

public class DBException extends Exception {

	private static final long serialVersionUID = 9035271445074011933L;

	public DBException(String message) {
		super(message);
	}

	public DBException(String message, Exception ex) {
		super(message, ex);
	}
}
