package br.com.cafebinario.transport;

import java.util.ArrayList;

public class GenericTO extends ArrayList<GenericVO> {
	private static final long serialVersionUID = 8483049475148954269L;

	private int collNumber = 0;

	public int getCollNumber() {
		return collNumber;
	}

	public void setCollNumber(int collNumber) {
		this.collNumber = collNumber;
	}
}
