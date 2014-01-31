import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.text.MaskFormatter;

import br.com.cafebinario.formatter.FormatterFieldImpl;


public class MyCNPJFormat extends FormatterFieldImpl {

	private MaskFormatter maskFormatter;
	
	public String execute(String cnpj){
		try {
			maskFormatter = new MaskFormatter("##.###.###/####-##");
			maskFormatter.setValueContainsLiteralCharacters(false);
			return maskFormatter.valueToString(cnpj);
		} catch (ParseException e) {
			return cnpj;
		}
	}

	public String execute(String cnpj, String... values){
		try {
			maskFormatter = new MaskFormatter("##.###.###/####-##");
			maskFormatter.setValueContainsLiteralCharacters(false);
			return maskFormatter.valueToString(cnpj);
		} catch (ParseException e) {
			return cnpj;
		}
	}
}