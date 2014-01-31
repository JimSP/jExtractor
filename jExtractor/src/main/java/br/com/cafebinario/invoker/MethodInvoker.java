package br.com.cafebinario.invoker;

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;

public class MethodInvoker {

	private static Logger LOG = Logger.getLogger(MethodInvoker.class);
	private static MethodInvoker thisInstance = new MethodInvoker();

	public static MethodInvoker getInstance() {
		return MethodInvoker.thisInstance;
	}

	private MethodInvoker() {

	}

	public Object invoker(Object target, String methodName, Object[] params)
			throws SecurityException, NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {

		Class[] clazzArray = new Class[params.length];

		int i = 0;
		while (i < params.length) {
			if (params[i] != null)
				clazzArray[i] = params[i].getClass();
			else
				clazzArray[i] = Object.class;
			i++;
		}

		return target.getClass().getMethod(methodName, clazzArray)
				.invoke(target, params);
	}
}
