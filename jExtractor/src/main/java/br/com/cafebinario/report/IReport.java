package br.com.cafebinario.report;

import java.io.OutputStream;

import br.com.cafebinario.shared.ExecutionContex;
import br.com.cafebinario.workflow.Result;

public interface IReport {

	void execute(Result result, ExecutionContex executionContex);

	void setOutputStream(OutputStream out);

}
