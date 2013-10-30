package br.com.cafebinario.dynamic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public final class DynamicJ {

	private static class LoadedClass {
		File binFile;
		String className;
		Class clazz;
		long lastModified;
		SourceDir srcDir;
		File srcFile;

		LoadedClass(String className, SourceDir src) {
			this.className = className;
			this.srcDir = src;
			String path = className.replace('.', '/');
			this.srcFile = new File(src.srcDir, path + ".java");
			this.binFile = new File(src.binDir, path + ".class");
			System.out
					.println("Diretório temporário dos executáveis (.class): "
							+ this.binFile);
			compileAndLoadClass();
		}

		void compileAndLoadClass() {
			if (clazz != null) {
				return;
			}

			String error = null;
			if (binFile.lastModified() < srcFile.lastModified()) {
				error = srcDir.javac.compile(new File[] { srcFile });
			}

			if (error != null) {
				throw new RuntimeException("Failed to compile "
						+ srcFile.getAbsolutePath() + ". Error: " + error);
			}

			try {
				clazz = srcDir.classLoader.loadClass(className);
				lastModified = srcFile.lastModified();

			} catch (ClassNotFoundException e) {
				throw new RuntimeException("Failed to load DynaCode class "
						+ srcFile.getAbsolutePath());
			}

			info("Init " + clazz);
		}

		boolean isChanged() {
			return srcFile.lastModified() != lastModified;
		}
	}
	private class MyInvocationHandler implements InvocationHandler {

		Object backend;
		String backendClassName;

		MyInvocationHandler(String className) {
			backendClassName = className;

			try {
				Class clz = loadClass(backendClassName);
				backend = newDynaCodeInstance(clz);

			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}

		
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			Class clz = loadClass(backendClassName);
			if (backend.getClass() != clz) {
				backend = newDynaCodeInstance(clz);
			}

			try {
				return method.invoke(backend, args);

			} catch (InvocationTargetException e) {
				throw e.getTargetException();
			}
		}

		private Object newDynaCodeInstance(Class clz) {
			try {
				return clz.newInstance();
			} catch (Exception e) {
				throw new RuntimeException(
						"Failed to new instance of DynaCode class "
								+ clz.getName(), e);
			}
		}

	}
	private class SourceDir {
		File binDir;
		URLClassLoader classLoader;
		Javac javac;
		File srcDir;

		SourceDir(File srcDir) {
			this.srcDir = srcDir;
			String subdir = srcDir.getAbsolutePath().replace(':', '_')
					.replace('/', '_').replace('\\', '_');
			this.binDir = new File(System.getProperty("java.io.tmpdir"),
					"dynacode/" + subdir);
			this.binDir.mkdirs();
			this.javac = new Javac(compileClasspath, binDir.getAbsolutePath());
			recreateClassLoader();
		}

		void recreateClassLoader() {
			try {
				classLoader = new URLClassLoader(new URL[] { binDir.toURL() },
						parentClassLoader);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}

	}
	private static String extractClasspath(ClassLoader cl) {
		StringBuffer buf = new StringBuffer();

		while (cl != null) {
			if (cl instanceof URLClassLoader) {
				URL urls[] = ((URLClassLoader) cl).getURLs();
				for (int i = 0; i < urls.length; i++) {
					if (buf.length() > 0) {
						buf.append(File.pathSeparatorChar);
					}
					buf.append(urls[i].getFile().toString());
				}
			}
			cl = cl.getParent();
		}

		return buf.toString();
	}

	private static void info(String msg) {
		System.out.println("[DynaCode] " + msg);
	}

	private String compileClasspath;

	private HashMap loadedClasses = new HashMap();

	private ClassLoader parentClassLoader;

	private ArrayList sourceDirs = new ArrayList();

	public DynamicJ() {
		this(Thread.currentThread().getContextClassLoader());
	}

	public DynamicJ(ClassLoader parentClassLoader) {
		this(extractClasspath(parentClassLoader), parentClassLoader);

	}

	public DynamicJ(String compileClasspath, ClassLoader parentClassLoader) {
		this.compileClasspath = compileClasspath;
		this.parentClassLoader = parentClassLoader;
	}

	public boolean addSourceDir(File srcDir) {

		try {
			srcDir = srcDir.getCanonicalFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		synchronized (sourceDirs) {
			for (int i = 0; i < sourceDirs.size(); i++) {
				SourceDir src = (SourceDir) sourceDirs.get(i);
				if (src.srcDir.equals(srcDir)) {
					return false;
				}
			}

			SourceDir src = new SourceDir(srcDir);
			sourceDirs.add(src);

			info("Add source dir " + srcDir);
		}

		return true;
	}

	public URL getResource(String resource) {
		try {

			SourceDir src = locateResource(resource);
			return src == null ? null : new File(src.srcDir, resource).toURL();

		} catch (MalformedURLException e) {
			return null;
		}
	}

	public InputStream getResourceAsStream(String resource) {
		try {

			SourceDir src = locateResource(resource);
			return src == null ? null : new FileInputStream(new File(
					src.srcDir, resource));

		} catch (FileNotFoundException e) {
			return null;
		}
	}

	public Class loadClass(String className) throws ClassNotFoundException {

		LoadedClass loadedClass = null;
		synchronized (loadedClasses) {
			loadedClass = (LoadedClass) loadedClasses.get(className);
		}

		if (loadedClass == null) {

			String resource = className.replace('.', '/') + ".java";
			SourceDir src = locateResource(resource);
			if (src == null) {
				throw new ClassNotFoundException("DynaCode class not found "
						+ className);
			}

			synchronized (this) {

				loadedClass = new LoadedClass(className, src);

				synchronized (loadedClasses) {
					loadedClasses.put(className, loadedClass);
				}
			}

			return loadedClass.clazz;
		}

		if (loadedClass.isChanged()) {
			unload(loadedClass.srcDir);
			return loadClass(className);
		}

		return loadedClass.clazz;
	}

	private SourceDir locateResource(String resource) {
		for (int i = 0; i < sourceDirs.size(); i++) {
			SourceDir src = (SourceDir) sourceDirs.get(i);
			if (new File(src.srcDir, resource).exists()) {
				return src;
			}
		}
		return null;
	}

	public Object newProxyInstance(Class interfaceClass, String implClassName)
			throws RuntimeException {
		MyInvocationHandler handler = new MyInvocationHandler(implClassName);
		return Proxy.newProxyInstance(interfaceClass.getClassLoader(),
				new Class[] { interfaceClass }, handler);
	}

	private void unload(SourceDir src) {
		synchronized (loadedClasses) {
			for (Iterator iter = loadedClasses.values().iterator(); iter
					.hasNext();) {
				LoadedClass loadedClass = (LoadedClass) iter.next();
				if (loadedClass.srcDir == src) {
					iter.remove();
				}
			}
		}

		src.recreateClassLoader();
	}
}