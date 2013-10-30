package br.com.cafebinario.file.factory;

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;

import br.com.cafebinario.file.interfaces.IIntegrationArchive;

public class FileIntegrationFactory {
	private static Logger LOG = Logger.getLogger(FileIntegrationFactory.class);
	private static FileIntegrationFactory thisInstance = new FileIntegrationFactory();

	public static FileIntegrationFactory getInstance() {
		return FileIntegrationFactory.thisInstance;
	}

	private FileIntegrationFactory() {

	}

	public IIntegrationArchive getIIntegrationArchiveImpl(String className)
			throws IllegalArgumentException, SecurityException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException,
			ClassNotFoundException {
		LOG.debug("className=" + className);
		return (IIntegrationArchive) Class.forName(className)
				.getConstructor(new Class[0]).newInstance(new Object[0]);
	}
}
