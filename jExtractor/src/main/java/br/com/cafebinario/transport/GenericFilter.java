package br.com.cafebinario.transport;

public class GenericFilter extends GenericVO {

	private static final long serialVersionUID = 1401115878258305897L;

	private String interfaceType;

	public String getInterfaceType() {
		return interfaceType;
	}

	public void setInterfaceType(String interfaceType) {
		this.interfaceType = interfaceType;
	}

	@Override
	public String toString() {
		return interfaceType + ";" + super.toString();

	}
}
