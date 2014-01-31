package br.com.cafebinario.integration;

public interface EventWrite<T> {
	void writeDocument(T document);
}
