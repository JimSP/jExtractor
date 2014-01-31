package br.com.cafebinario.shared;

import java.util.HashMap;

public class SystemContext extends HashMap<String, Object> {

	private static final long serialVersionUID = 437865340485898373L;

	private static SystemContext thisInstance = new SystemContext();

	public static SystemContext getInstance() {
		return SystemContext.thisInstance;
	}
}
