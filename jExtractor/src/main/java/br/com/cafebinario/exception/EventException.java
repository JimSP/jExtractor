package br.com.cafebinario.exception;

public class EventException extends Exception {

	private static final long serialVersionUID = 1684609861637711153L;

	public EventException(String message) {
		super(message);
	}

	public EventException(String message, Exception ex) {
		super(message, ex);
	}
}
