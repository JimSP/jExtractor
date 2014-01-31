package br.com.cafebinario.formatter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;

public interface FormatterField {
	public String execute(BigDecimal bigDecimalValue);

	public String execute(BigDecimal bigDecimalValue, String... values);

	public String execute(BigInteger bigIntegerValue);

	public String execute(BigInteger bigIntegerValue, String... values);

	public String execute(Byte byteValue);

	public String execute(Byte byteValue, String... values);

	public String execute(Character charValue);

	public String execute(Character charValue, String... values);

	public String execute(Date dateValue);

	public String execute(Date dateValue, String... values);

	public String execute(Double doubleValue);

	public String execute(Double doubleValue, String... values);

	public String execute(Float floatValue);

	public String execute(Float floatValue, String... values);

	public String execute(Integer intValue);

	public String execute(Integer intValue, String... values);

	public String execute(Long longValue);

	public String execute(Long longValue, String... values);

	public String execute(Short shortValue);

	public String execute(Short shortValue, String... values);

	public String execute(String stringValue);

	public String execute(String stringValue, String... values);
}
