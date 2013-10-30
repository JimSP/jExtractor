package br.com.cafebinario.factory;

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;

import br.com.cafebinario.event.EventField;

public class EventFieldFactory extends DynamicFactory<EventField> {

	private static Logger LOG = Logger.getLogger(EventFieldFactory.class);
	private static EventFieldFactory thisInstance = new EventFieldFactory();

	public static EventFieldFactory getInstance() {
		return EventFieldFactory.thisInstance;
	}

	public EventField getIEventFieldImpl(String className, FactoryMode mode)
			throws IllegalArgumentException, SecurityException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException,
			ClassNotFoundException {

		LOG.debug("className=" + className);
		LOG.debug("FactoryMode=" + mode.name());

		return getObject(EventField.class, className, mode);
	}
}
