package br.com.cafebinario.factory;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import br.com.cafebinario.dynamic.DynamicJ;

public abstract class DynamicFactory<T> {
	private DynamicJ dynacode = new DynamicJ();

	protected T getObject(Class<T> interfaceTarget, String className,
			FactoryMode mode) throws IllegalArgumentException,
			SecurityException, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException,
			ClassNotFoundException {

		if (FactoryMode.REFLECTION == mode) {
			return (T) Class.forName(className).getConstructor(new Class[0])
					.newInstance(new Object[0]);
		} else if (FactoryMode.DYNAMIC == mode) {
			dynacode.addSourceDir(new File("dynacode"));
			return (T) dynacode.newProxyInstance(interfaceTarget, className);
		} else {
			throw new ClassNotFoundException();
		}
	}
}
