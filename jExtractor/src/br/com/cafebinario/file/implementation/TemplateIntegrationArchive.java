package br.com.cafebinario.file.implementation;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.util.Date;
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

	private final long CURRENT_TIME_MILLIS = System.currentTimeMillis();
	private String DATE;

	private FileReader reader = null;
	private FileWriter writer = null;
	private Long pagina = 1L;

	private String fileNameResult = "";
	private String fileNameTemplate = "";

	private IIntegracao integracao = null;
	protected AppProperties properties = AppProperties.instanceOf();

	private String template = "";
	private String[] vars;

	private String header = "";
	private String content = "";
	private String footer = "";

	private String buffer = "";
	private String result = "";

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

	public String initialize(IIntegracao integracao) throws IOException {
		this.integracao = integracao;
		fileNameTemplate = properties.getProperty("LocalTemplateDirectory")
				+ AppProperties.instanceOf().getProperty(
						"TEMPLATE_" + this.integracao.getClassName());

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

		fileNameResult = properties.getProperty("LocalOutputDirectory")
				+ AppProperties.instanceOf().getProperty(
						"FILE_" + this.integracao.getClassName());

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
				result += header.replace("${date," + lenStr + "}", backValue
						+ getSpaces(Integer.parseInt(pattern[0].trim())
								- backValue.length()));
			} else {
				result += header.replace("${date," + lenStr + "}",
						DATE
								+ getSpaces(Integer.parseInt(pattern[0].trim())
										- DATE.length()));
			}

			while (it.hasNext()) {
				GenericVO vo = it.next();
				write(vo, to.getCollNumber());
				result += buffer;
				buffer = content;
			}

			result += footer;

			writer.write(result);

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
							params[0] = var;

							int i = 1;
							while (i < params.length) {
								params[i] = javaFormatter[i];
								i++;
							}

							String backValue = invokeFormatter(clazz, function,
									params);
							buffer = content
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

	private String invokeFormatter(String clazz, String function, Object[] value)
			throws IllegalArgumentException, SecurityException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException,
			ClassNotFoundException {

		Object obj = FormatterFieldFactory.getInstance().getFormatterField(clazz,
				FactoryMode.DYNAMIC);

		String backValue = (String) MethodInvoker.getInstance().invoker(obj,
				function, value);

		return backValue;
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

	/*
	 * public void write(GenericVO vo, int collNumber) throws
	 * IntegrationArchiveException, IOException {
	 * 
	 * String lenStr = content.substring(content.indexOf("${page,") + 7,
	 * content.indexOf("}", content.indexOf("${page,") + 7));
	 * 
	 * buffer = content.replace( "${page," + lenStr + "}",
	 * String.valueOf(pagina) + getSpaces(Integer.parseInt(lenStr.trim()) -
	 * String.valueOf(pagina).length()));
	 * 
	 * for (String var : vars) { String value = null;
	 * 
	 * String indexMapStr = properties.getProperty(var);
	 * 
	 * if (!"".equals(indexMapStr) && indexMapStr != null) { Integer indexMap =
	 * Integer.parseInt(indexMapStr); value = vo.get(indexMap);
	 * 
	 * if (value != null) { lenStr = buffer .substring( buffer.indexOf("${" +
	 * var + ",") + 3 + var.length(), buffer.indexOf("}", buffer.indexOf("${" +
	 * var + ",") + 3 + var.length())); buffer = buffer.replace("${" + var + ","
	 * + lenStr + "}", value + getSpaces(Integer.parseInt(lenStr.trim()) -
	 * value.length())); } } }
	 * 
	 * pagina++; }
	 */

	public String writeRecord(GenericVO vo, int collNumber, ResultSetMetaData mt)
			throws IntegrationArchiveException {
		return null;
	}
}