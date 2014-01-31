import java.text.ParseException;

import javax.swing.text.MaskFormatter;

import br.com.cafebinario.formatter.FormatterFieldImpl;


public class MyCardFormat extends FormatterFieldImpl {

private MaskFormatter maskFormatter;
	
	public String execute(String cardNumber){
		try {
			maskFormatter = new MaskFormatter("####.##XX.XXXX.XX##");
			maskFormatter.setValueContainsLiteralCharacters(false);
			return maskFormatter.valueToString(cardNumber);
		} catch (ParseException e) {
			return "erro.erro.erro.erro";
		}
	}

	public String execute(String cardNumber, String... values){
		try {
			maskFormatter = new MaskFormatter("####.##XX.XXXX.XX##");
			maskFormatter.setValueContainsLiteralCharacters(false);
			return maskFormatter.valueToString(cardNumber);
		} catch (ParseException e) {
			return "erro.erro.erro.erro";
		}
	}

}
