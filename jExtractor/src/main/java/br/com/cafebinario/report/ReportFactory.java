package br.com.cafebinario.report;

import java.io.File;

import br.com.cafebinario.dynamic.DynamicJ;

public class ReportFactory {

	private static ReportFactory thisInstance = new ReportFactory();

	public static ReportFactory getInstance() {
		return ReportFactory.thisInstance;
	}

	private DynamicJ dynacode = new DynamicJ();

	private ReportFactory() {
	}

	public IReport getReport(String className) {
		dynacode.addSourceDir(new File("dynacode"));
		return (IReport) dynacode.newProxyInstance(IReport.class, className);
	}
}
