package br.com.cafebinario.factory;

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;

import br.com.cafebinario.integration.IIntegracao;

public class IntegracaoFactory extends DynamicFactory<IIntegracao> {
	private static Logger LOG = Logger.getLogger(IntegracaoFactory.class);
	private static IntegracaoFactory thisInstance = new IntegracaoFactory();

	public static IntegracaoFactory getInstance() {
		return IntegracaoFactory.thisInstance;
	}

	private IntegracaoFactory() {
	}

	public IIntegracao getIIntegracaoImpl(String className, FactoryMode mode)
			throws IllegalArgumentException, SecurityException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException,
			ClassNotFoundException {

		LOG.debug("className=" + className);
		LOG.debug("FactoryMode=" + mode.name());

		return getObject(IIntegracao.class, className, mode);
	}
}
