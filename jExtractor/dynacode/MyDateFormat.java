import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.cafebinario.formatter.FormatterFieldImpl;

public class MyDateFormat extends FormatterFieldImpl {

	public String execute(Long timeStamp){
		return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date(timeStamp));
	}

	public String execute(Long timeStamp, String... values){
		return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date(timeStamp));
	}
}
