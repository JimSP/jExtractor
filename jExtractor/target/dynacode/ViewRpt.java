import java.io.PrintStream;

import br.com.cafebinario.report.IReport;
import br.com.cafebinario.workflow.Result;
import br.com.cafebinario.shared.ExecutionContex;


public class ViewRpt implements IReport{

	private PrintStream printStream;

	public void setPrintStream(PrintStream out){
		this.printStream = out;
	}

	public void execute(Result result, ExecutionContex executionContex){
		System.out.println("arquivo gerado: " + result.getObject());
	}
}
