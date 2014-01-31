package br.com.cafebinario.event;

import br.com.cafebinario.exception.EventException;

public interface EventField {
	public String intercepted(int index, Class type, Object dbFieldValue,
			long rowNum, String eventType) throws EventException;
}
