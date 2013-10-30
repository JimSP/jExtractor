import br.com.cafebinario.workflow.IWorkflow;
import br.com.cafebinario.factory.FactoryMode;
import br.com.cafebinario.factory.IntegracaoFactory;
import br.com.cafebinario.file.factory.FileIntegrationFactory;
import br.com.cafebinario.file.implementation.CsvIntegrationArchive;
import br.com.cafebinario.file.interfaces.IIntegrationArchive;
import br.com.cafebinario.integration.IIntegracao;
import br.com.cafebinario.integration.implementation.GenericReader;
import br.com.cafebinario.integration.implementation.IntegracaoBase;
import br.com.cafebinario.properties.AppProperties;
import br.com.cafebinario.transport.GenericVO;
import br.com.cafebinario.workflow.Result;
import br.com.cafebinario.shared.ExecutionContex;

public class TestFlow implements IWorkflow {

	private AppProperties appProperties = AppProperties.instanceOf();
	private IIntegracao testIntegration;
	private IIntegrationArchive testIIntegrationArchive;
	private GenericVO parameter;

	public TestFlow() {
	}

	public GenericVO getParameter() {
		return parameter;
	}

	public void setParameter(GenericVO parameter) {
		this.parameter = parameter;
	}

	public Result execute(ExecutionContex executionContex) throws Exception {

		testIntegration = IntegracaoFactory.getInstance().getIIntegracaoImpl(
				appProperties.getProperty("TestIntegrationDBClass"),
				FactoryMode.DYNAMIC);

		testIIntegrationArchive = FileIntegrationFactory.getInstance()
				.getIIntegrationArchiveImpl(
						appProperties
								.getProperty("testIIntegrationArchiveClass"));

		testIntegration.setIntegrationArchive(testIIntegrationArchive);
		testIntegration.setParameter(this.parameter);
		
		Result result = new Result();
		testIntegration.begin();
		testIntegration.execute();
		testIntegration.end();

		testIIntegrationArchive.initialize(testIntegration);
		testIntegration.createHeader();
		String fileName = testIIntegrationArchive.write();
		testIntegration.createTrailler(testIntegration.getData().size()/testIntegration.getData().getCollNumber());
		testIIntegrationArchive.finalize();

		result.setClazz(String.class);
		result.setObject(fileName);

		return result;
	}
}