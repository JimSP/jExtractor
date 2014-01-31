import java.io.IOException;
import java.io.OutputStream;

import br.com.cafebinario.report.IReport;
import br.com.cafebinario.shared.ExecutionContex;
import br.com.cafebinario.workflow.Result;


public class DynamicRpt implements IReport{
	
	private OutputStream out;

	@Override
	public void execute(Result result, ExecutionContex executionContex) {
		try {
			out.write(String.valueOf(result).getBytes());
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setOutputStream(OutputStream out) {
		this.out = out;
	}
}
