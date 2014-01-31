package br.com.cafebinario.integration;

import java.sql.Connection;
import java.util.List;

import br.com.cafebinario.exception.IntegracaoException;
import br.com.cafebinario.file.interfaces.IIntegrationArchive;
import br.com.cafebinario.transport.GenericTO;
import br.com.cafebinario.transport.GenericVO;

public interface IIntegracao {

	void begin() throws IntegracaoException;

	void begin(Connection connection) throws IntegracaoException;

	void begin(String stringConnectionName, String driverDBName)
			throws IntegracaoException;

	void createHeader();

	void createTrailler(int lineNumbers);

	void end() throws IntegracaoException;

	void execute() throws IntegracaoException;

	String getClassName();

	GenericTO getData();

	List<String> getDataRegistro();

	IFastLane getDispacher();

	String getFileName();

	IIntegrationArchive getIntegrationArchive();

	GenericVO getParameter();

	String getSqlSource();

	boolean isIFastLaneInstanceOf();

	void setData(GenericTO data);

	void setDataRegistro(List<String> data);

	void setIFastLane(IFastLane fastLane);

	void setIntegrationArchive(IIntegrationArchive integrationArchive);

	void setParameter(GenericVO parameter);
}
