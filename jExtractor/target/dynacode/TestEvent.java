import br.com.cafebinario.event.EventField;
import br.com.cafebinario.exception.EventException;


public class TestEvent implements EventField {
	public String intercepted(int index, Class type, Object dbFieldValue, long rowNum, String eventType) throws EventException {
		System.out.println("index=" + index);
		System.out.println("dbFieldValue=" + dbFieldValue);
		System.out.println("rowNum=" + rowNum);
		System.out.println("eventType=" + eventType);
		System.out.println("-------------------------");
		return dbFieldValue.toString();
	}
}
