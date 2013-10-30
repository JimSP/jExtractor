package br.com.cafebinario.workflow;

import br.com.cafebinario.shared.ExecutionContex;
import br.com.cafebinario.transport.GenericVO;

public interface IWorkflow {

	public Result execute(ExecutionContex executionContex) throws Exception;

	public GenericVO getParameter();

	public void setParameter(GenericVO parameter);
}
