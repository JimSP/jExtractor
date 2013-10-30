package br.com.cafebinario.integration.implementation;

import java.sql.Date;
import java.sql.ResultSet;
import java.util.List;

import br.com.cafebinario.exception.DaoException;
import br.com.cafebinario.exception.IntegracaoException;
import br.com.cafebinario.file.interfaces.IIntegrationArchive;
import br.com.cafebinario.integration.IFastLane;
import br.com.cafebinario.integration.IIntegracao;
import br.com.cafebinario.properties.AppProperties;
import br.com.cafebinario.transport.GenericTO;
import br.com.cafebinario.transport.GenericVO;

public class GenericReader extends IntegracaoBase implements IIntegracao {

	protected IFastLane dispacher;
	protected IIntegrationArchive integrationArchive;
	protected List<String> registros;
	protected ResultSet rs;

	public void createHeader() {
		System.out.println("GenericReader.createHeader#not implemented");
	}

	public void createTrailler(int lineNumbers) {
		System.out.println("GenericReader.createTrailler#not implemented");
	}

	public void execute() throws IntegracaoException {
		try {
			dao.execute(this);
		} catch (DaoException e) {
			LOG.error("erro ao executar DAO", e);
			e.printStackTrace();
			throw new IntegracaoException("Problemas durante a execução.", e);
		}
	}

	public String getClassName() {
		return this.getClass().getSimpleName();
	}

	public GenericTO getData() {
		return data;
	}

	public List<String> getDataRegistro() {
		return registros;
	}

	public IFastLane getDispacher() {
		return this.dispacher;
	}

	public String getFileName() {
		return AppProperties.instanceOf().getProperty(
				"FILE_" + this.getClass().getSimpleName())
				+ new Date(System.currentTimeMillis()).toString();
	}

	public IIntegrationArchive getIntegrationArchive() {
		return integrationArchive;
	}

	public GenericVO getParameter() {
		return parameter;
	}

	public String getSqlSource() {
		return AppProperties.instanceOf().getProperty(
				"SQL_" + this.getClass().getSimpleName());
	}

	public boolean isIFastLaneInstanceOf() {
		return this.dispacher != null;
	}

	public void setData(GenericTO data) {
		this.data = data;
	}

	public void setDataRegistro(List<String> data) {
		this.registros = data;

	}

	public void setIFastLane(IFastLane fastLane) {
		this.dispacher = fastLane;
	}

	public void setIntegrationArchive(IIntegrationArchive integrationArchive) {
		this.integrationArchive = integrationArchive;
	}

	public void setParameter(GenericVO parameter) {
		this.parameter = parameter;
	}
}
