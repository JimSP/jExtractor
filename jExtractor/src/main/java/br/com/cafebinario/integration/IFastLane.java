package br.com.cafebinario.integration;

import java.sql.ResultSetMetaData;

import br.com.cafebinario.exception.IntegrationArchiveException;
import br.com.cafebinario.transport.GenericVO;

public interface IFastLane {
	void colletorRow(GenericVO vo, int collNumber, ResultSetMetaData mt)
			throws IntegrationArchiveException;
}
