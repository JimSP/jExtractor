package br.com.cafebinario.integration.implementation;

import java.sql.Connection;

import org.apache.log4j.Logger;

import br.com.cafebinario.dao.JdbcDao;
import br.com.cafebinario.database.DataBase;
import br.com.cafebinario.exception.DBException;
import br.com.cafebinario.exception.IntegracaoException;
import br.com.cafebinario.transport.GenericTO;
import br.com.cafebinario.transport.GenericVO;

public abstract class IntegracaoBase {

	protected static Logger LOG = Logger.getLogger(IntegracaoBase.class);

	protected Connection connection;
	protected JdbcDao dao = new JdbcDao();
	protected GenericTO data;
	protected GenericVO parameter;

	public void begin() throws IntegracaoException {
		try {
			connection = DataBase.instanceOf().getConnection();
			dao.setConnection(connection);
		} catch (DBException e) {
			LOG.error("erro ao obter conexao", e);
			throw new IntegracaoException(
					"Problemas ao conectar com Banco de Dados.", e);
		}
	}

	public void begin(Connection connection) throws IntegracaoException {
		this.connection = connection;
		dao.setConnection(connection);
	}

	public void begin(String strConnName, String driverDbName)
			throws IntegracaoException {
		try {
			connection = DataBase.instanceOf().getConnection(strConnName,
					driverDbName);
			dao.setConnection(connection);
		} catch (DBException e) {
			LOG.error("erro ao obter conexao", e);
			throw new IntegracaoException(
					"Problemas ao conectar com Banco de Dados.", e);
		}
	}

	public void end() throws IntegracaoException {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (Exception e) {
			LOG.error("erro ao fechar conexao", e);
			throw new IntegracaoException("Erro ao finalizar Integracão", e);
		}
	}
}
