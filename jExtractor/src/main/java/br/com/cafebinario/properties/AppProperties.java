package br.com.cafebinario.properties;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class AppProperties extends Properties {

	private static Logger LOG = Logger.getLogger(AppProperties.class);

	private static final long serialVersionUID = 4408418694809890352L;

	private static AppProperties thisInstance = new AppProperties();

	public static AppProperties instanceOf() {
		return AppProperties.thisInstance;
	}

	private String pathFile;

	private AppProperties() {

	}

	public String getPathFile() {
		return pathFile;
	}

	public void load() {
		LOG.debug("carregar arquivo de propriedades");
		try {
			LOG.debug("pathFile=" + pathFile);
			InputStream in = new java.io.FileInputStream(pathFile);
			super.load(in);
			in.close();
		} catch (FileNotFoundException e) {
			LOG.error("arquivo de propriedades nao encontrado. pathFile="
					+ pathFile, e);
		} catch (IOException e) {
			LOG.error("erro ao ler arquivo de propriedades. pathFile="
					+ pathFile, e);
		}
	}

	public void setPathFile(String pathFile) {
		this.pathFile = pathFile;
	}
}
