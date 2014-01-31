package br.com.cafebinario.file.interfaces;

import java.io.IOException;
import java.sql.ResultSetMetaData;

import br.com.cafebinario.exception.IntegrationArchiveException;
import br.com.cafebinario.integration.IIntegracao;
import br.com.cafebinario.transport.GenericVO;

public interface IIntegrationArchive {
	void discharge() throws IntegrationArchiveException;

	void finalize() throws IntegrationArchiveException;

	String getFileName();

	String initialize(IIntegracao integracao) throws IOException;

	String initializeOrder(IIntegracao integracao) throws IOException;

	String write() throws IntegrationArchiveException;

	void write(GenericVO vo, int collNumber)
			throws IntegrationArchiveException, IOException;

	String writeRecord(GenericVO vo, int collNumber, ResultSetMetaData mt)
			throws IntegrationArchiveException;
}
