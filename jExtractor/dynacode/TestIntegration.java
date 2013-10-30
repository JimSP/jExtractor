import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.List;

import br.com.cafebinario.exception.IntegracaoException;
import br.com.cafebinario.exception.IntegrationArchiveException;
import br.com.cafebinario.file.interfaces.IIntegrationArchive;
import br.com.cafebinario.integration.IFastLane;
import br.com.cafebinario.integration.implementation.GenericReader;
import br.com.cafebinario.integration.IIntegracao;
import br.com.cafebinario.integration.IFastLane;
import br.com.cafebinario.properties.AppProperties;
import br.com.cafebinario.transport.GenericTO;
import br.com.cafebinario.transport.GenericVO;


public class TestIntegration extends GenericReader {
	
	public TestIntegration(){
		super();
	}

	public String getSqlSource() {
		return AppProperties.instanceOf().getProperty("SQL_" + this.getClass().getSimpleName());
	}

	public String getFileName() {
		return AppProperties.instanceOf().getProperty("FILE_" + this.getClass().getSimpleName());
	}
}
