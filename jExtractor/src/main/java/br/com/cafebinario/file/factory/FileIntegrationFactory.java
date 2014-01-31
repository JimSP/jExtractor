package br.com.cafebinario.file.factory;

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;

import br.com.cafebinario.factory.DynamicFactory;
import br.com.cafebinario.factory.FactoryMode;
import br.com.cafebinario.factory.IntegracaoFactory;
import br.com.cafebinario.file.interfaces.IIntegrationArchive;

public class FileIntegrationFactory extends DynamicFactory<IIntegrationArchive> {
	private static Logger LOG = Logger.getLogger(IntegracaoFactory.class);
	private static FileIntegrationFactory thisInstance = new FileIntegrationFactory();

	public static FileIntegrationFactory getInstance() {
		return FileIntegrationFactory.thisInstance;
	}

	private FileIntegrationFactory() {
	}

	public IIntegrationArchive getIIntegrationArchiveImpl(String className, FactoryMode mode)
			throws IllegalArgumentException, SecurityException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException,
			ClassNotFoundException {

		LOG.debug("className=" + className);
		LOG.debug("FactoryMode=" + mode.name());

		return getObject(IIntegrationArchive.class, className, mode);
	}
}
