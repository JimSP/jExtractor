package br.com.cafebinario.dynamic;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

public final class Javac {

	String bootclasspath;
	String classpath;
	String encoding;
	String extdirs;
	String outputdir;
	String sourcepath;
	String target;

	public Javac(String classpath, String outputdir) {
		this.classpath = classpath;
		this.outputdir = outputdir;
	}

	private String[] buildJavacArgs(String srcFiles[]) {
		ArrayList args = new ArrayList();

		if (classpath != null) {
			args.add("-classpath");
			args.add(classpath);
		}
		if (outputdir != null) {
			args.add("-d");
			args.add(outputdir);
		}
		if (sourcepath != null) {
			args.add("-sourcepath");
			args.add(sourcepath);
		}
		if (bootclasspath != null) {
			args.add("-bootclasspath");
			args.add(bootclasspath);
		}
		if (extdirs != null) {
			args.add("-extdirs");
			args.add(extdirs);
		}
		if (encoding != null) {
			args.add("-encoding");
			args.add(encoding);
		}
		if (target != null) {
			args.add("-target");
			args.add(target);
		}

		for (int i = 0; i < srcFiles.length; i++) {
			args.add(srcFiles[i]);
		}

		return (String[]) args.toArray(new String[args.size()]);
	}

	public String compile(File srcFiles[]) {
		String paths[] = new String[srcFiles.length];
		for (int i = 0; i < paths.length; i++) {
			paths[i] = srcFiles[i].getAbsolutePath();
		}
		return compile(paths);
	}

	public String compile(String srcFiles[]) {
		StringWriter err = new StringWriter();
		PrintWriter errPrinter = new PrintWriter(err);

		String args[] = buildJavacArgs(srcFiles);
		int resultCode = com.sun.tools.javac.Main.compile(args, errPrinter);

		errPrinter.close();
		return (resultCode == 0) ? null : err.toString();
	}

	public String getBootclasspath() {
		return bootclasspath;
	}

	public String getClasspath() {
		return classpath;
	}

	public String getEncoding() {
		return encoding;
	}

	public String getExtdirs() {
		return extdirs;
	}

	public String getOutputdir() {
		return outputdir;
	}

	public String getSourcepath() {
		return sourcepath;
	}

	public String getTarget() {
		return target;
	}

	public void setBootclasspath(String bootclasspath) {
		this.bootclasspath = bootclasspath;
	}

	public void setClasspath(String classpath) {
		this.classpath = classpath;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public void setExtdirs(String extdirs) {
		this.extdirs = extdirs;
	}

	public void setOutputdir(String outputdir) {
		this.outputdir = outputdir;
	}

	public void setSourcepath(String sourcepath) {
		this.sourcepath = sourcepath;
	}

	public void setTarget(String target) {
		this.target = target;
	}
}