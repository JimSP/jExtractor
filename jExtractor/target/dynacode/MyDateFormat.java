import br.com.cafebinario.formatter.FormatterFieldImpl;

public class MyDateFormat extends FormatterFieldImpl {

	public String execute(Long timeStamp){
		System.out.println("DYNAMIC FORMATTER=" + timeStamp);
		return "00-00-00 00:00:00";
	}

	public String execute(Long timeStamp, String... values){
		System.out.println("DYNAMIC FORMATTER=" + timeStamp + "," + values);
		return "00-00-00 00:00:00";
	}
}
