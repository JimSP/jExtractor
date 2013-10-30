package br.com.cafebinario.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

@WebServlet(value = "/ProcessRouter")
public class ProcessRouter extends HttpServlet {
	private static Logger LOG = Logger.getLogger(ProcessRouter.class);

	private static final long serialVersionUID = 1L;
	private FieldParser fp = new FieldParser();

	public ProcessRouter() {
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		LOG.debug("HTTP METHOD GET");
		execute(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		LOG.debug("HTTP METHOD POST");
		execute(request, response);
	}

	private void execute(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		LOG.debug("Inicio da execucao");
		try {
			GenericFilter parameter = fp.parse(request);

			String[] designer = AppProperties.instanceOf()
					.getProperty(parameter.getInterfaceType()).split("[,]");
			String wfStr = designer[0];
			String rptStr = designer[1];

			IWorkflow wf = WorkFlowLoader.getInstance().loader(wfStr);
			wf.setParameter(parameter);

			ExecutionContex executionContex = ExecutionContex.getInstance();

			executionContex.putAll(request.getParameterMap());

			Result result = wf.execute(executionContex);

			IReport report = ReportFactory.getInstance().getReport(rptStr);
			report.execute(result, executionContex);

		} catch (IllegalArgumentException e) {
			LOG.error("argumentos ilegais", e);
		} catch (SecurityException e) {
			LOG.error("problemas de segurança", e);
		} catch (IllegalAccessException e) {
			LOG.error("tantativa de acesso ilegal", e);
		} catch (InvocationTargetException e) {
			LOG.error("alvo invocado com problemas", e);
		} catch (NoSuchMethodException e) {
			LOG.error("metodo procurado nao encontrado", e);
			e.printStackTrace();
		} catch (Exception e) {
			LOG.error("problema durante a execucao", e);
		} finally {
			LOG.debug("Termino da Execução");
		}
	}
}
