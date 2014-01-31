import br.com.cafebinario.factory.FactoryMode;
import br.com.cafebinario.factory.IntegracaoFactory;
import br.com.cafebinario.file.factory.FileIntegrationFactory;
import br.com.cafebinario.file.implementation.CsvIntegrationArchive;
import br.com.cafebinario.file.interfaces.IIntegrationArchive;
import br.com.cafebinario.integration.IIntegracao;
import br.com.cafebinario.integration.implementation.GenericReader;
import br.com.cafebinario.properties.AppProperties;
import br.com.cafebinario.shared.ExecutionContex;
import br.com.cafebinario.transport.GenericTO;
import br.com.cafebinario.transport.GenericVO;
import br.com.cafebinario.workflow.IWorkflow;
import br.com.cafebinario.workflow.Result;


public class DynamicFlow implements IWorkflow{

	private GenericVO parameter;
	
	@Override
	public Result execute(ExecutionContex executionContex) throws Exception {
		
		Result result = new Result();
		
		String integrationClass = AppProperties.instanceOf().getProperty("dynamicIntegrationDBClass", "br.com.cafebinario.integration.implementation.GenericReader");
		String archiveClass = AppProperties.instanceOf().getProperty("dynamicIIntegrationArchiveClass", "br.com.cafebinario.file.implementation.CsvIntegrationArchive");
		IIntegracao integracao = IntegracaoFactory.getInstance().getIIntegracaoImpl(integrationClass, FactoryMode.REFLECTION);
		IIntegrationArchive integrationArchive = FileIntegrationFactory.getInstance().getIIntegrationArchiveImpl(archiveClass, FactoryMode.REFLECTION);
		
		integracao.setIntegrationArchive(integrationArchive);
		
		integracao.setParameter(parameter);
		
		integracao.begin();
		integracao.execute();
		integracao.end();
		
		integrationArchive.initialize(integracao);
		integrationArchive.write();
		integrationArchive.finalize();
		
		result.setClazz(String.class);
		result.setObject(integracao.getFileName());
		
		return result;
	}

	@Override
	public GenericVO getParameter() {
		return parameter;
	}

	@Override
	public void setParameter(GenericVO parameter) {
		this.parameter = parameter;
		
	}
}
