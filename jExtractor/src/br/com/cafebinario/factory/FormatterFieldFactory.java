package br.com.cafebinario.factory;

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;

import br.com.cafebinario.formatter.FormatterField;

public class FormatterFieldFactory extends DynamicFactory<FormatterField>{
	private static Logger LOG = Logger.getLogger(FormatterFieldFactory.class);
	private static FormatterFieldFactory thisInstance = new FormatterFieldFactory();

	public static FormatterFieldFactory getInstance() {
		return FormatterFieldFactory.thisInstance;
	}

	public FormatterField getFormatterField(String className, FactoryMode mode)
			throws IllegalArgumentException, SecurityException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException,
			ClassNotFoundException {

		LOG.debug("className=" + className);
		LOG.debug("FactoryMode=" + mode.name());
		
		return getObject(FormatterField.class, className, mode);
	}
}
