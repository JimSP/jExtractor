package br.com.cafebinario.exception;

public class IntegracaoException extends Exception {

	private static final long serialVersionUID = 1L;

	public IntegracaoException(String message) {
		super(message);
	}

	public IntegracaoException(String message, Exception ex) {
		super(message, ex);
	}

}
