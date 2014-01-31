package br.com.cafebinario.transport;

import java.util.Enumeration;
import java.util.Hashtable;

public class GenericVO extends Hashtable<Integer, String> implements Cloneable {

	private static final long serialVersionUID = -2708157243641302995L;

	private int maxIndex = 0;

	public GenericVO() {

	}

	public int getMaxIndex() {
		return maxIndex;
	}

	public String print() {

		Enumeration<Integer> keys = this.keys();
		while (keys.hasMoreElements()) {
			Integer key = keys.nextElement();
			String value = this.get(key);
			System.out.println("Key=" + key + " Value=" + value);
		}

		return "";
	}

	@Override
	public String put(Integer key, String value) {
		maxIndex++;
		return super.put(key, value);
	}

	public String remove(Integer key) {
		if (maxIndex > 0)
			maxIndex--;

		return super.remove(key);
	}

	public String[] toArray() {
		String[] result = new String[maxIndex];

		int i = 0;
		while (i < maxIndex) {
			result[i] = super.get(new Integer(i));
			i++;
		}
		return result;
	}

	@Override
	public String toString() {
		String back = "";
		Enumeration<String> element = this.elements();

		int i = 1;
		while (element.hasMoreElements())
			if (i <= maxIndex)
				back += element.nextElement() + ";";
			else
				back += element.nextElement() + "\r\n";

		return back;
	}
}
