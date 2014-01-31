package br.com.cafebinario.workflow;

public class Result {

	private Class clazz;
	private Object object;

	public Class getClazz() {
		return clazz;
	}

	public Object getObject() {
		return object;
	}

	public void setClazz(Class clazz) {
		this.clazz = clazz;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	@Override
	public String toString() {
		return "Result [clazz=" + clazz + ", object=" + object + "]";
	}
}
