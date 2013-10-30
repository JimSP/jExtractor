package br.com.cafebinario.workflow;

import java.io.File;

import br.com.cafebinario.dynamic.DynamicJ;

public class WorkFlowLoader {

	private static WorkFlowLoader thisInstance = new WorkFlowLoader();

	public static WorkFlowLoader getInstance() {
		return WorkFlowLoader.thisInstance;
	}

	private DynamicJ dynacode = new DynamicJ();

	public IWorkflow loader(String className) {
		dynacode.addSourceDir(new File("dynacode"));
		return (IWorkflow) dynacode
				.newProxyInstance(IWorkflow.class, className);
	}

}
