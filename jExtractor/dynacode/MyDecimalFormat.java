import java.text.ParseException;

import javax.swing.text.MaskFormatter;

import br.com.cafebinario.formatter.FormatterFieldImpl;


public class MyCardFormat extends FormatterFieldImpl {

private MaskFormatter maskFormatter;
	
	public String execute(String decimal){
		try {
			maskFormatter = new MaskFormatter("###########,##");
			maskFormatter.setValueContainsLiteralCharacters(false);
			return maskFormatter.valueToString(decimal);
		} catch (ParseException e) {
			return decimal;
		}
	}

	public String execute(String decimal, String... values){
		try {
			maskFormatter = new MaskFormatter("###########,##");
			maskFormatter.setValueContainsLiteralCharacters(false);
			return maskFormatter.valueToString(decimal);
		} catch (ParseException e) {
			return decimal;
		}
	}
}
