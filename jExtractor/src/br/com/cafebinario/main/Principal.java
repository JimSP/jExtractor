package br.com.cafebinario.main;

import org.apache.log4j.Logger;

import br.com.cafebinario.parse.FieldParser;
import br.com.cafebinario.properties.AppProperties;
import br.com.cafebinario.report.IReport;
import br.com.cafebinario.report.ReportFactory;
import br.com.cafebinario.shared.ExecutionContex;
import br.com.cafebinario.transport.GenericFilter;
import br.com.cafebinario.workflow.IWorkflow;
import br.com.cafebinario.workflow.Result;
import br.com.cafebinario.workflow.WorkFlowLoader;

public class Principal {

	private static FieldParser argsParse = new FieldParser();
	private static Logger LOG = Logger.getLogger(Principal.class);
	public static void main(String[] args) {
		LOG.info("Inicio da execucao");

		LOG.debug("String[] args");
		int i = 0;
		for (String arg : args) {
			LOG.debug(i++ + "=" + arg);
		}

		if (args.length == 0) {
			System.out.println("invalid argument");
			System.out.println("call example:");
			System.out
					.println("java -jar GenericExtractor.jar \"app.properties\" v1 v2 v3 ... vn \"interfaceType\"");
			System.exit(-1);
		}

		try {

			AppProperties.instanceOf().setPathFile(args[0]);
			AppProperties.instanceOf().load();

			Principal principal = new Principal();
			principal.start(argsParse.parse(args));

			LOG.info("Termino da execucao");
			System.exit(0);
		} catch (Throwable t) {
			t.printStackTrace();
			LOG.fatal("Termino da execucao", t);
			System.exit(-1);
		}
	}

	private ExecutionContex executionContex = ExecutionContex.getInstance();

	private void start(GenericFilter parameter) throws Throwable {
		LOG.debug("Start-Parameter:" + parameter.toString());
		try {

			LOG.debug("getInterfaceType=" + parameter.getInterfaceType());
			String[] designer = AppProperties.instanceOf()
					.getProperty(parameter.getInterfaceType()).split("[,]");
			String wfStr = designer[0];
			String rptStr = designer[1];

			LOG.debug("wfStr=" + wfStr);
			LOG.debug("rptStr=" + rptStr);

			LOG.debug("WorkFlow.execute");
			IWorkflow wf = WorkFlowLoader.getInstance().loader(wfStr);
			wf.setParameter(parameter);
			Result result = wf.execute(this.executionContex);

			LOG.debug("Report.execute");
			IReport report = ReportFactory.getInstance().getReport(rptStr);
			report.setPrintStream(System.out);
			report.execute(result, this.executionContex);
		} catch (Throwable t) {
			LOG.error("Erro durante a execucao", t);
			throw t;
		}
	}
}