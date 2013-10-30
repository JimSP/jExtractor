package br.com.cafebinario.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import br.com.cafebinario.exception.DBException;
import br.com.cafebinario.properties.AppProperties;

public class DataBase {
	private static Logger LOG = Logger.getLogger(DataBase.class);
	private static DataBase thisInstance = new DataBase();

	public static DataBase instanceOf() {
		return DataBase.thisInstance;
	}

	protected AppProperties properties = AppProperties.instanceOf();

	private DataBase() {
	}

	public Connection getConnection() throws DBException {
		return getConnection(properties.getProperty("DRIVER_DB"),
				properties.getProperty("STR_CON_DB"),
				properties.getProperty("USER_DB"),
				properties.getProperty("PASSWD_DB"));
	}

	public Connection getConnection(String driverDbName, String strConnName)
			throws DBException {
		return getConnection(properties.getProperty(driverDbName),
				properties.getProperty(strConnName),
				properties.getProperty("USER_DB"),
				properties.getProperty("PASSWD_DB"));
	}

	public Connection getConnection(String driverDbName, String strConnName,
			String user, String passwd) throws DBException {
		Connection connection = null;
		
		try {
			Class.forName(driverDbName);
			connection = DriverManager.getConnection(strConnName, user, passwd);
		} catch (ClassNotFoundException e) {
			
			LOG.error("Driver BD nao encontrado", e);
			throw new DBException("Driver DB nao encontrado");
		} catch (SQLException e) {
			e.printStackTrace();
			LOG.error("Problemas de conexao com banco de dados", e);
			throw new DBException(
					"Problemas ao estabelecer conexao com banco de dados");
		}
		return connection;
	}

}
