package br.com.cafebinario.file.implementation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.ResultSetMetaData;
import java.util.Enumeration;

import org.apache.log4j.Logger;

import br.com.cafebinario.exception.IntegrationArchiveException;
import br.com.cafebinario.file.interfaces.IIntegrationArchive;
import br.com.cafebinario.integration.IIntegracao;
import br.com.cafebinario.properties.AppProperties;
import br.com.cafebinario.transport.GenericTO;
import br.com.cafebinario.transport.GenericVO;

public class CsvIntegrationArchive implements IIntegrationArchive {

	private static Logger LOG = Logger.getLogger(CsvIntegrationArchive.class);
	private FileWriter arquivo = null;

	private String delimitador = null;
	private IIntegracao integracao = null;
	protected AppProperties properties = AppProperties.instanceOf();
	private String quebraLinha = null;
	private int writeCount = 0;
	
	private StringBuilder buffer = new StringBuilder();
	
	public void discharge() throws IntegrationArchiveException {
		try {
			arquivo.flush();
		} catch (IOException e) {
			throw new IntegrationArchiveException(
					"Erro ao descarregar buffer do arquivo.", e);
		}
	}

	public void finalize() throws IntegrationArchiveException {
		try {
			arquivo.flush();
			arquivo.close();
		} catch (IOException e) {
			LOG.error("Erro ao encerrar conexão com arquivo " + integracao.getFileName(), e);
			throw new IntegrationArchiveException(
					"Erro ao encerrar conexão com arquivo.", e);
		}
	}

	public String getFileName() {
		return integracao.getFileName();
	}

	public String initialize(IIntegracao integracao) throws IOException {
		this.integracao = integracao;
		delimitador = properties.getProperty("DELIMITADOR");
		quebraLinha = properties.getProperty("QUEBRA_DE_LINHA");
		writeCount = Integer.parseInt(properties.getProperty("IO_QuantidadeLinhas", "-1"));

		File arq = new File(integracao.getFileName());

		if (Boolean.parseBoolean(AppProperties.instanceOf().getProperty(
				"DELETE_IF_EXIST_FILE"))) {
			if (arq.exists()) {
				arq.delete();
			}
		}

		try {
			arquivo = new FileWriter(arq, Boolean.parseBoolean(AppProperties
					.instanceOf().getProperty("APPEND_FILE")));
		} catch (IOException e) {
			throw e;
		}

		return integracao.getFileName();
	}

	public String initializeOrder(IIntegracao integracao) throws IOException {
		this.integracao = integracao;
		delimitador = properties.getProperty("DELIMITADOR");
		quebraLinha = properties.getProperty("QUEBRA_DE_LINHA");

		File arq = new File(integracao.getFileName());

		if (Boolean.parseBoolean(AppProperties.instanceOf().getProperty(
				"DELETE_IF_EXIST_FILE"))) {
			if (arq.exists()) {
				arq.delete();
			}
		}

		try {
			arquivo = new FileWriter(arq, Boolean.parseBoolean(AppProperties
					.instanceOf().getProperty("APPEND_FILE")));
		} catch (IOException e) {
			throw e;
		}

		return integracao.getFileName();
	}

	public String write() throws IntegrationArchiveException {

		try {
			LOG.debug("OUTPUT_FILE=" + integracao.getFileName());
			GenericTO data = integracao.getData();
			
			int i = 0;
			for (GenericVO vo : data) {
				write(vo, data.getCollNumber());
				
				if(i == writeCount){
					arquivo.append(buffer.toString());
					buffer = new StringBuilder();
					i = 0;
				}
				i++;
			}
			
			arquivo.append(buffer.toString());
			arquivo.flush();
			return integracao.getFileName();
		} catch (Exception e) {
			LOG.error("Erro ao gerar arquivo " + integracao.getFileName(), e);
			throw new IntegrationArchiveException("erro ao escrever arquivo.",
					e);
		}
	}

	public void write(GenericVO vo, int collNumber) throws IOException {
		Enumeration<Integer> itKey = vo.keys();
		int i = 1;
		
		
		while (itKey.hasMoreElements()) {
			Integer key = itKey.nextElement();
			String value = vo.get(key);
			buffer.append(value + (i++ < collNumber ? delimitador : quebraLinha));
		}
	}

	public String writeRecord(GenericVO vo, int collNumber, ResultSetMetaData mt)
			throws IntegrationArchiveException {

		FileChannel rwChannel = null;
		try {
			LOG.debug("OUTPUT_FILE=" + integracao.getFileName());

			/*
			 * Calcula o buffer da saida
			 */
			int tamanho = 0;
			int columnCount = mt.getColumnCount();
			int i = 1;
			while (i < columnCount) {
				if (i == 1) {
					tamanho += mt.getColumnDisplaySize(i)
							+ quebraLinha.length();
				} else {
					tamanho += mt.getColumnDisplaySize(i)
							+ delimitador.length();
				}
				i++;
			}
			/*
			 * Grava no arquivo o conteúdo do ResultSet
			 */
			rwChannel = new RandomAccessFile(integracao.getFileName(), "rw").getChannel();
			ByteBuffer wrBuf = null;
			wrBuf = rwChannel.map(FileChannel.MapMode.READ_WRITE, 0, tamanho);
			byte[] buffer = null;

			StringBuilder registro = new StringBuilder(tamanho);

			Enumeration<Integer> itKey = vo.keys();
			while (itKey.hasMoreElements()) {
				Integer key = itKey.nextElement();
				registro.append(vo.get(key)
						+ (key.intValue() < collNumber ? delimitador
								: quebraLinha));
			}

			buffer = registro.toString().getBytes();
			wrBuf.put(buffer);

		} catch (FileNotFoundException e) {
			LOG.error("Erro ao gerar arquivo " + integracao.getFileName(), e);
			throw new IntegrationArchiveException(
					"Erro ao escrever arquivo. FileNotFoundException: ", e);
		} catch (IOException e) {
			LOG.error("Erro ao gerar arquivo " + integracao.getFileName(), e);
			throw new IntegrationArchiveException(
					"Erro ao escrever arquivo. IOException: ", e);
		} catch (Exception e) {
			LOG.error("Erro ao gerar arquivo " + integracao.getFileName(), e);
			throw new IntegrationArchiveException(
					"Erro ao escrever arquivo. Exception: ", e);
		} finally {
			try {
				if (rwChannel != null) {
					rwChannel.close();
				}
			} catch (IOException e) {
				LOG.error("Erro ao encerrar conexão com arquivo. " + integracao.getFileName(),
						e);
				throw new IntegrationArchiveException(
						"Erro ao encerrar conexão com arquivo.", e);
			}
		}
		return integracao.getFileName();
	}
}