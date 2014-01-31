package br.com.cafebinario.formatter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;

public class FormatterFieldImpl implements FormatterField {

	public String execute(BigDecimal bigDecimalValue) {
		return String.valueOf(bigDecimalValue);
	}

	public String execute(BigDecimal bigDecimalValue, String... values) {
		return execute(bigDecimalValue);
	}

	public String execute(BigInteger bigIntegerValue) {
		return String.valueOf(bigIntegerValue);
	}

	public String execute(BigInteger bigIntegerValue, String... values) {
		return execute(bigIntegerValue);
	}

	public String execute(Byte byteValue) {
		return String.valueOf(byteValue);
	}

	public String execute(Byte byteValue, String... values) {
		return execute(byteValue);
	}

	public String execute(Character charValue) {
		return String.valueOf(charValue);
	}

	public String execute(Character charValue, String... values) {
		return execute(charValue);
	}

	public String execute(Date dateValue) {
		return String.valueOf(dateValue);
	}

	public String execute(Date dateValue, String... values) {
		return execute(dateValue);
	}

	public String execute(Double doubleValue) {
		return String.valueOf(doubleValue);
	}

	public String execute(Double doubleValue, String... values) {
		return execute(doubleValue);
	}

	public String execute(Float floatValue) {
		return String.valueOf(floatValue);
	}

	public String execute(Float floatValue, String... values) {
		return execute(floatValue);
	}

	public String execute(Integer intValue) {
		return String.valueOf(intValue);
	}

	public String execute(Integer intValue, String... values) {
		return execute(intValue);
	}

	public String execute(Long longValue) {
		return String.valueOf(longValue);
	}

	public String execute(Long longValue, String... values) {
		return execute(longValue);
	}

	public String execute(Short shortValue) {
		return String.valueOf(shortValue);
	}

	public String execute(Short shortValue, String... values) {
		return execute(shortValue);
	}

	public String execute(String stringValue) {
		return execute(stringValue);
	}

	public String execute(String stringValue, String... values) {
		return execute(stringValue);
	}
}
