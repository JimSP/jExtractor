package br.com.cafebinario.dao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import org.apache.log4j.Logger;

import br.com.cafebinario.event.EventField;
import br.com.cafebinario.exception.DaoException;
import br.com.cafebinario.exception.EventException;
import br.com.cafebinario.exception.IntegrationArchiveException;
import br.com.cafebinario.factory.EventFieldFactory;
import br.com.cafebinario.factory.FactoryMode;
import br.com.cafebinario.integration.IIntegracao;
import br.com.cafebinario.properties.AppProperties;
import br.com.cafebinario.transport.GenericTO;
import br.com.cafebinario.transport.GenericVO;

public class JdbcDao {

	protected static Logger LOG = Logger.getLogger(JdbcDao.class);
	protected Connection connection;
	protected IIntegracao integracao;

	/*
	 * Direciona a gravação do arquivo, dando flush no disco pela quantidade de
	 * registros passados no parametro qtdeRegsFlush desse método.
	 */
	protected void dispacherRow(ResultSet rs, Integer qtdeRegsFlush)
			throws SQLException, IntegrationArchiveException, DaoException {
		int numeroLinhas = 1;
		GenericTO to = new GenericTO();
		int colunas = getColunasResultset(rs);
		to.setCollNumber(colunas);
		GenericVO vo = null;
		while (rs.next()) {
			for (int i = 1; i <= colunas; i++) {
				try {
					vo = new GenericVO();
					vo.put(i, rs.getObject(i) == null ? "" : rs.getObject(i)
							.toString());

					if (numeroLinhas > qtdeRegsFlush) {
						this.integracao.setData(to);
						this.integracao.getDispacher().colletorRow(vo, colunas,
								rs.getMetaData());
						this.integracao.getIntegrationArchive().discharge();
						to = new GenericTO();
						numeroLinhas = 0;
					}

				} catch (Exception e) {
					throw new DaoException(
							"Erro ao transferir ResultSet para VO:", e);
				}
			}
			numeroLinhas++;
		}
		this.integracao.setData(to);
		this.integracao.getDispacher().colletorRow(vo, colunas,
				rs.getMetaData());
		this.integracao.getIntegrationArchive().discharge();
	}

	@SuppressWarnings("unchecked")
	public void execute(IIntegracao integracao) throws DaoException {
		this.integracao = integracao;
		PreparedStatement statement = null;
		ResultSet rs = null;
		String fileName = AppProperties.instanceOf().getProperty(
				"LocalInputDirectory")
				+ integracao.getSqlSource();
		LOG.debug("SQL FILE:" + fileName);
		Integer qtdeRegsFlush = Integer.valueOf(AppProperties.instanceOf()
				.getProperty("IO_QuantidadeLinhas"));

		String sql = null;
		try {
			sql = readerSql(fileName);
			if (sql == null) {
				LOG.error("erro ao ler arquivo=" + fileName);
				throw new DaoException("Erro ao Ler fonte do SQL. FileName: "
						+ fileName);
			}
			if (integracao.getParameter() != null) {
				LOG.debug("GENERIC DAO#SET PARAMETER");
				LOG.debug(integracao.getParameter().toString());
				LOG.debug("-----------------------");
				Iterator<Integer> itKey = (Iterator<Integer>) integracao
						.getParameter().keys();
				/*
				 * Substitui os parametros do sql
				 */
				while (itKey.hasNext()) {
					Integer key = itKey.next();
					String value = integracao.getParameter().get(key);
					Integer tamanho = key.toString().length();

					int indice = sql.indexOf("&" + key.intValue() + "decode");
					if (indice != -1) {
						int indiceFinal = sql.indexOf("&" + key.intValue()
								+ "remdecode", indice);
						if (indiceFinal == -1) {
							throw new DaoException("SQL invalido:" + sql);
						}
						int indiceInicio = indice - 1;
						while (!sql.substring(indiceInicio,
								indiceInicio + tamanho + 10).equals(
								"&" + key.intValue() + "remdecode")) {
							indiceInicio--;
							if (indiceInicio == -1) {
								throw new DaoException("SQL invalido:" + sql);
							}
						}
						/*
						 * Linhas com indicacao 'remdecode' serao eliminadas se
						 * não tiver sido passado parametro ou substituidas com
						 * o campo da tabela e a igualdade com o parametro sem o
						 * decode
						 */
						if (new Integer(value).intValue() == 0) {
							sql = sql.replace(
									sql.substring(indiceInicio, indiceFinal
											+ tamanho + 10), "");
							indice = sql.indexOf("&" + key.intValue()
									+ "decode");
							while (indice != -1) {
								indiceFinal = sql.indexOf("&" + key.intValue()
										+ "remdecode", indice);
								if (indiceFinal == -1) {
									throw new DaoException("SQL invalido:"
											+ sql);
								}
								indiceInicio = indice - 1;
								while (!sql.substring(indiceInicio,
										indiceInicio + tamanho + 10).equals(
										"&" + key.intValue() + "remdecode")) {
									indiceInicio--;
									if (indiceInicio == -1) {
										throw new DaoException("SQL invalido:"
												+ sql);
									}
								}
								sql = sql.replace(
										sql.substring(indiceInicio, indiceFinal
												+ tamanho + 10), "");
								indice = sql.indexOf("&" + key.intValue()
										+ "decode");
							}
							/*
							 * Verifica se ficou AND logo apos o WHERE
							 */
							int indiceWhere = 0;
							int indiceAnd = 0;
							int diferenca = 0;
							indiceWhere = sql.indexOf("WHERE");
							while (indiceWhere != -1) {
								String espacos = "";
								indiceAnd = sql.indexOf("AND", indiceWhere);
								if (indiceAnd != -1) {
									diferenca = indiceAnd - indiceWhere - 5;
									for (int i = 0; i <= diferenca; i++) {
										espacos += " ";
									}
									sql = sql.replace(
											"WHERE" + espacos + "AND", "");
								}
								indiceWhere = sql.indexOf("WHERE",
										indiceWhere + 1);
							}
						} else {
							sql = sql.replace("&" + key.intValue() + "decode",
									value);
							sql = sql.replace(
									sql.substring(indiceInicio, indiceInicio
											+ tamanho + 10), "");
						}
					} else {
						sql = sql.replace("&" + key.intValue(), value);
					}
				}
			}

			LOG.debug("SQL QUERY:" + sql);
			System.out.println("SQL QUERY:" + sql);

			if (this.connection == null) {
				throw new DaoException(
						"Não foi possível obter a conexão com o Banco de Dados.");
			}

			statement = this.connection.prepareStatement(sql,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);

			if ("S".equals(sql.substring(0, 1).toUpperCase())) {
				LOG.debug("EXECUTE:statement.executeQuery");
				rs = statement.executeQuery();
				if (this.integracao.isIFastLaneInstanceOf()) {
					dispacherRow(rs, qtdeRegsFlush);
				} else {
					writeResultSet(rs);
				}
			} else if ("B".equals(sql.substring(0, 1).toUpperCase())) {
				LOG.debug("EXECUTE:statement.execute");
				statement.execute();
				rs = statement.getResultSet();
				if (rs != null) {
					if (this.integracao.isIFastLaneInstanceOf()) {
						dispacherRow(rs, qtdeRegsFlush);
					} else {
						writeResultSet(rs);
					}
				}
			} else {
				LOG.debug("EXECUTE:statement.executeUpdate");
				statement.executeUpdate();
				connection.commit();
			}
		} catch (IntegrationArchiveException e) {
			LOG.error("Erro ao Ler fonte do SQL =" + sql + " do Arquivo: "
					+ fileName, e);
			e.printStackTrace();
			throw new DaoException("Erro ao Ler fonte do SQL: " + sql
					+ " IntegrationArquiveException: " + fileName, e);
		} catch (IOException e) {
			LOG.error("erro ao Ler fonte do SQL =" + sql + " do Arquivo: "
					+ fileName, e);
			e.printStackTrace();
			throw new DaoException("Erro ao Ler fonte do SQL. IOException: ", e);
		} catch (SQLException e) {
			LOG.error("erro ao  Ler fonte do SQL =" + sql + " do Arquivo: "
					+ fileName, e);
			e.printStackTrace();
			throw new DaoException("Erro ao Ler fonte do SQL. SQLException: ",
					e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					LOG.error("Erro ao fechar ResultSet.", e);
					throw new DaoException("Erro ao fechar ResultSet.", e);
				}
			}
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					LOG.error("Erro ao fechar Statement", e);
					throw new DaoException("Erro ao fechar Statement.", e);
				}
			}
		}
	}

	protected int getColunasResultset(ResultSet rs) throws SQLException {
		int colunas = 0;
		if (rs.getMetaData() != null) {
			colunas = rs.getMetaData().getColumnCount();
		}
		return colunas;
	}

	public Connection getConnection() {
		return connection;
	}

	protected String readerSql(String fileName) throws IOException {
		StringBuffer buffer = new StringBuffer();
		FileReader reader = new FileReader(fileName);
		BufferedReader bufferReader = new BufferedReader(reader);
		while (bufferReader.ready()) {
			buffer.append(bufferReader.readLine() + "\n");
		}
		reader.close();
		bufferReader.close();
		return buffer.toString();
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	protected void writeResultSet(ResultSet rs) throws SQLException {

		boolean dispachEventField = Boolean.parseBoolean(AppProperties
				.instanceOf().getProperty("EVENT_FORMAT_FIELD"));

		long rowNum = 1;
		GenericTO to = new GenericTO();
		if (rs != null && integracao != null) {
			if (rs.getMetaData() != null) {
				GenericVO vo = null;
				int colunas = rs.getMetaData().getColumnCount();
				to.setCollNumber(colunas);
				while (rs.next()) {
					vo = new GenericVO();
					for (int i = 1; i <= colunas; i++) {

						Object value = rs.getObject(i);

						if (dispachEventField) {

							String[] keys = AppProperties.instanceOf()
									.getProperty("CONDITION_KEYS").split("[,]");

							Class type = value.getClass();
							int collNumber = i;

							for (String key : keys) {
								try {
									String target = AppProperties.instanceOf()
											.getProperty(key);
									EventField eventField = EventFieldFactory
											.getInstance()
											.getIEventFieldImpl(target,
													FactoryMode.DYNAMIC);
									String[] conditions = AppProperties
											.instanceOf()
											.getProperty("CONDITION_" + target)
											.split("[,]");

									for (String condition : conditions) {
										if (("INDEX_CONDITION_" + target)
												.equals(condition)) {
											int indexCondition = Integer
													.parseInt(AppProperties
															.instanceOf()
															.getProperty(
																	"INDEX_CONDITION_"
																			+ target));

											if (collNumber == indexCondition) {
												try {
													vo.put(i, eventField
															.intercepted(
																	collNumber,
																	type,
																	value,
																	rowNum,
																	"INDEX"));
												} catch (EventException e) {
													e.printStackTrace();
													throw new SQLException(
															e.getMessage());
												}
											} else {
												vo.put(i,
														value != null ? value
																.toString()
																: "");
											}
										}

										if (("TYPE_CONDITION_" + target)
												.equals(condition)) {
											String typeCondition = AppProperties
													.instanceOf().getProperty(
															"TYPE_CONDITION_"
																	+ target);

											if (typeCondition != null
													&& typeCondition
															.equals(value
																	.getClass())) {
												try {
													vo.put(i, eventField
															.intercepted(
																	collNumber,
																	type,
																	value,
																	rowNum,
																	"TYPE"));
												} catch (EventException e) {
													e.printStackTrace();
													throw new SQLException(
															e.getMessage());
												}
											} else {
												vo.put(i,
														value != null ? value
																.toString()
																: "");
											}
										}

										if (("VALUE_CONDITION_" + target)
												.equals(condition)) {
											String valueCondition = AppProperties
													.instanceOf().getProperty(
															"VALUE_CONDITION_"
																	+ target);

											if (value != null
													&& valueCondition
															.equals(String
																	.valueOf(value))) {
												try {
													vo.put(i, eventField
															.intercepted(
																	collNumber,
																	type,
																	value,
																	rowNum,
																	"VALUE"));
												} catch (EventException e) {
													e.printStackTrace();
													throw new SQLException(
															e.getMessage());
												}
											} else {
												vo.put(i,
														value != null ? value
																.toString()
																: "");
											}
										}

										if (("ROW_NUM_CONDITION_" + target)
												.equals(condition)) {
											long rowNumCondition = Long
													.parseLong(AppProperties
															.instanceOf()
															.getProperty(
																	"ROW_NUM_CONDITION_"
																			+ target));
											if (rowNum == rowNumCondition) {
												try {
													vo.put(i, eventField
															.intercepted(
																	collNumber,
																	type,
																	value,
																	rowNum,
																	"ROW_NUM"));
												} catch (EventException e) {
													e.printStackTrace();
													throw new SQLException(
															e.getMessage());
												}
											} else {
												vo.put(i,
														value != null ? value
																.toString()
																: "");
											}
										}
									}

								} catch (IllegalArgumentException e) {
									LOG.error("Erro ao disparar Evento", e);
								} catch (SecurityException e) {
									LOG.error("Erro ao disparar Evento", e);
								} catch (InstantiationException e) {
									LOG.error("Erro ao disparar Evento", e);
								} catch (IllegalAccessException e) {
									LOG.error("Erro ao disparar Evento", e);
								} catch (InvocationTargetException e) {
									LOG.error("Erro ao disparar Evento", e);
								} catch (NoSuchMethodException e) {
									LOG.error("Erro ao disparar Evento", e);
								} catch (ClassNotFoundException e) {
									LOG.error("Erro ao disparar Evento", e);
								}
							}

						} else {
							vo.put(i, value != null ? value.toString() : "");
						}
					}
					to.add(vo);
					rowNum++;
				}
				integracao.setData(to);
			}
		}
	}
}