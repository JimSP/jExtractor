package br.com.cafebinario.exception;

public class IntegrationArchiveException extends Exception {

	private static final long serialVersionUID = -503118979440489120L;

	public IntegrationArchiveException(String message) {
		super(message);
	}

	public IntegrationArchiveException(String message, Exception ex) {
		super(message, ex);
	}
}
