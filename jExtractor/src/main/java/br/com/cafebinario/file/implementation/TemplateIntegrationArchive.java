package br.com.cafebinario.file.implementation;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.util.Iterator;

import org.apache.log4j.Logger;

import br.com.cafebinario.exception.IntegrationArchiveException;
import br.com.cafebinario.factory.FactoryMode;
import br.com.cafebinario.factory.FormatterFieldFactory;
import br.com.cafebinario.file.interfaces.IIntegrationArchive;
import br.com.cafebinario.integration.IIntegracao;
import br.com.cafebinario.invoker.MethodInvoker;
import br.com.cafebinario.properties.AppProperties;
import br.com.cafebinario.transport.GenericTO;
import br.com.cafebinario.transport.GenericVO;

public class TemplateIntegrationArchive implements IIntegrationArchive {

	private static Logger LOG = Logger
			.getLogger(TemplateIntegrationArchive.class);

	private String buffer = "";
	private String content = "";

	private final long CURRENT_TIME_MILLIS = System.currentTimeMillis();
	private String DATE;
	private String fileNameResult = "";

	private String fileNameTemplate = "";
	private String footer = "";

	private String header = "";
	private IIntegracao integracao = null;

	private Long pagina = 1L;
	protected AppProperties properties = AppProperties.instanceOf();
	
	private int writeCount = 0;

	private FileReader reader = null;
	private StringBuilder result = new StringBuilder();
	private String template = "";

	private String[] vars;
	private FileWriter writer = null;

	public TemplateIntegrationArchive() {
		DATE = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date(
				CURRENT_TIME_MILLIS));
	}

	public void discharge() throws IntegrationArchiveException {
		try {
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
			throw new IntegrationArchiveException(
					"Erro ao tentar gravar arquivo.", e);
		}
	}

	public void finalize() throws IntegrationArchiveException {
		try {
			reader.close();
		} catch (IOException e) {
			LOG.error("Erro ao encerrar conexão com template "
					+ fileNameTemplate, e);
			throw new IntegrationArchiveException(
					"Erro ao encerrar conexão com arquivo.", e);
		}

		try {
			writer.close();
		} catch (IOException e) {
			LOG.error("Erro ao encerrar conexão com arquivo "
					+ fileNameTemplate, e);
			throw new IntegrationArchiveException(
					"Erro ao encerrar conexão com arquivo.", e);
		}
	}

	public String getFileName() {
		return fileNameResult;
	}

	private String getSpaces(int len) {
		String space = "";
		int i = 0;
		while (i < len) {
			space += " ";
			i++;
		}

		return space;
	}

	public String initialize(IIntegracao integracao) throws IOException {
		this.integracao = integracao;
		fileNameTemplate = properties.getProperty("LocalTemplateDirectory")
				+ AppProperties.instanceOf().getProperty(
						"TEMPLATE_" + this.integracao.getClassName());
		
		writeCount = Integer.parseInt(properties.getProperty("IO_QuantidadeLinhas", "-1"));

		File arq = new File(fileNameTemplate);

		try {
			reader = new FileReader(arq);

			StringBuilder builder = new StringBuilder();
			while (reader.ready()) {

				char[] cbuf = new char[1];
				reader.read(cbuf);
				builder.append(cbuf);
			}

			template = builder.toString();

			header = template.substring(template.indexOf("${header}"),
					template.indexOf("${content}"))
					.replace("${header}\r\n", "");
			content = template.substring(template.indexOf("${content}"),
					template.indexOf("${footer}"))
					.replace("${content}\r\n", "");
			footer = template.substring(template.indexOf("${footer}")).replace(
					"${footer}\r\n", "");

			vars = template.split("[${,}\t\r\n]");
		} catch (IOException e) {
			throw e;
		}

		fileNameResult = integracao.getFileName();

		File arqResult = new File(fileNameResult);

		if (Boolean.parseBoolean(AppProperties.instanceOf().getProperty(
				"DELETE_IF_EXIST_FILE"))) {
			if (arqResult.exists()) {
				arqResult.delete();
			}
		}

		try {
			writer = new FileWriter(arqResult,
					Boolean.parseBoolean(AppProperties.instanceOf()
							.getProperty("APPEND_FILE")));
		} catch (IOException e) {
			LOG.error("erro ao tentar escrever no arquivo.", e);
			throw e;

		}

		return fileNameResult;
	}

	public String initializeOrder(IIntegracao integracao) throws IOException {
		return null;
	}

	private String invokeFormatter(String clazz, String function, Object[] value)
			throws IllegalArgumentException, SecurityException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException,
			ClassNotFoundException {

		Object obj = FormatterFieldFactory.getInstance().getFormatterField(
				clazz, FactoryMode.DYNAMIC);

		String backValue = (String) MethodInvoker.getInstance().invoker(obj,
				function, value);

		return backValue;
	}

	public String write() throws IntegrationArchiveException {
		try {
			GenericTO to = integracao.getData();
			Iterator<GenericVO> it = to.iterator();

			String lenStr = header.substring(header.indexOf("${date,") + 7,
					header.indexOf("}", header.indexOf("${date," + 7)));

			String[] pattern = lenStr.split("[,]");

			if (pattern.length == 2) {
				String[] javaFormatter = pattern[1].split("[(,)]");
				String clazz = javaFormatter[0];

				String function = "execute";
				Object[] params = new Object[javaFormatter.length];
				params[0] = this.CURRENT_TIME_MILLIS;

				int i = 1;
				while (i < params.length) {
					params[i] = javaFormatter[i];
					i++;
				}
				String backValue = invokeFormatter(clazz, function, params);
				result.append(header.replace("${date," + lenStr + "}", backValue
						+ getSpaces(Integer.parseInt(pattern[0].trim())
								- backValue.length())));
				
			} else {
				result.append(header.replace("${date," + lenStr + "}",
						DATE
								+ getSpaces(Integer.parseInt(pattern[0].trim())
										- DATE.length())));
			}

			long i = 0;
			while (it.hasNext()) {
				GenericVO vo = it.next();
				write(vo, to.getCollNumber());
				
				if(i == writeCount){
					i = 0;
					writer.write(result.toString());
					writer.flush();
					result = new StringBuilder();
				}
				
				result.append(buffer);
				i++;
			}

			result.append(buffer);

			writer.write(result.toString());
			writer.write(footer);
			writer.flush();
			
			
			result = new StringBuilder();

		} catch (Exception e) {
			throw new IntegrationArchiveException(
					"erro durante a geração do arquivo.", e);
		}

		return fileNameResult;
	}

	public void write(GenericVO vo, int collNumber) {

		try {
			String lenStr = content.substring(content.indexOf("${page,") + 7,
					content.indexOf("}", content.indexOf("${page,") + 7));

			String[] pattern = lenStr.split("[,]");

			if (pattern.length == 2) {
				String[] javaFormatter = pattern[1].split("[(,)]");
				String clazz = javaFormatter[0];

				String function = "execute";
				Object[] params = new Object[javaFormatter.length];
				params[0] = pagina;

				int i = 1;
				while (i < params.length) {
					params[i] = javaFormatter[i];
					i++;
				}

				String backValue = invokeFormatter(clazz, function, params);
				buffer = content.replace("${page," + lenStr + "}", backValue
						+ getSpaces(Integer.parseInt(pattern[0].trim())
								- backValue.length()));
			} else {
				buffer = content.replace(
						"${page," + lenStr + "}",
						String.valueOf(pagina)
								+ getSpaces(Integer.parseInt(lenStr.trim())
										- String.valueOf(pagina).length()));
			}

			for (String var : vars) {
				String value = null;

				String indexMapStr = properties.getProperty(var);

				if (!"".equals(indexMapStr) && indexMapStr != null) {
					Integer indexMap = Integer.parseInt(indexMapStr);
					value = vo.get(indexMap);

					if (value != null) {
						lenStr = buffer.substring(
								buffer.indexOf("${" + var + ",") + 3
										+ var.length(),
								buffer.indexOf("}",
										buffer.indexOf("${" + var + ",") + 3
												+ var.length()));

						pattern = lenStr.split("[,]");

						if (pattern.length == 2) {
							String[] javaFormatter = pattern[1].split("[(,)]");
							String clazz = javaFormatter[0];

							String function = "execute";
							Object[] params = new Object[javaFormatter.length];
							params[0] = value;

							int i = 1;
							while (i < params.length) {
								params[i] = javaFormatter[i];
								i++;
							}

							String backValue = invokeFormatter(clazz, function,
									params);
							buffer = buffer
									.replace(
											"${" + var + "," + lenStr + "}",
											backValue
													+ getSpaces(Integer
															.parseInt(pattern[0]
																	.trim())
															- String.valueOf(
																	backValue)
																	.length()));
						} else {
							buffer = buffer.replace(
									"${" + var + "," + lenStr + "}",
									value
											+ getSpaces(Integer.parseInt(lenStr
													.trim()) - value.length()));
						}
					}
				}
			}

			pagina++;
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("erro ao tentar escrever no arquivo.", e);
		}

	}

	public String writeRecord(GenericVO vo, int collNumber, ResultSetMetaData mt)
			throws IntegrationArchiveException {
		return null;
	}
}