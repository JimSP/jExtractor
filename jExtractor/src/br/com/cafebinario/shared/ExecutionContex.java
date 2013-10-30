package br.com.cafebinario.shared;

import java.util.HashMap;

public class ExecutionContex extends HashMap<String, Object> {
	private static final long serialVersionUID = 4942857294595131908L;

	public static ExecutionContex getInstance() {
		return new ExecutionContex();
	}

	private ExecutionContex() {

	}
}
