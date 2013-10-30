package br.com.cafebinario.file.implementation;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSetMetaData;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.apache.log4j.Logger;

import br.com.cafebinario.exception.IntegrationArchiveException;
import br.com.cafebinario.file.interfaces.IIntegrationArchive;
import br.com.cafebinario.integration.EventWrite;
import br.com.cafebinario.integration.IIntegracao;
import br.com.cafebinario.properties.AppProperties;
import br.com.cafebinario.transport.GenericVO;

import com.itextpdf.text.DocumentException;

public class XlsIntegrationArchive implements IIntegrationArchive {

	private static Logger LOG = Logger.getLogger(PdfIntegrationArchive.class);
	private EventWrite<WritableWorkbook> callerEvent = null;
	private String fileName = "";

	private IIntegracao integracao = null;

	protected AppProperties properties = AppProperties.instanceOf();

	private WritableWorkbook workbook = null;

	public XlsIntegrationArchive() {

	}

	private void addContent(WritableWorkbook document) throws DocumentException {
		callerEvent.writeDocument(document);
	}

	public void discharge() throws IntegrationArchiveException {
	}

	public void finalize() {
		System.out.println("Close XLS Document");
		try {
			workbook.write();
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}

	public EventWrite<WritableWorkbook> getCallerEvent() {
		return callerEvent;
	}

	public String getFileName() {
		return fileName;
	}

	public String initialize(IIntegracao integracao) {
		this.integracao = integracao;

		String fileName = properties.getProperty("LocalOutputDirectory")
				+ integracao.getFileName();
		LOG.debug("OUTPUT_FILE=" + fileName);

		File file = new File(fileName);
		WorkbookSettings wbSettings = new WorkbookSettings();

		wbSettings.setLocale(new Locale("pt", "BR"));

		try {
			workbook = Workbook.createWorkbook(file, wbSettings);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return fileName;
	}

	public String initializeOrder(IIntegracao integracao) {

		this.integracao = integracao;
		return "";
	}

	public void setCallerEvent(EventWrite<WritableWorkbook> callerEvent) {
		this.callerEvent = callerEvent;
	}

	public String white() throws IntegrationArchiveException {

		try {
			addContent(workbook);

		} catch (DocumentException e) {
			e.printStackTrace();
		}

		return properties.getProperty("LocalOutputDirectory")
				+ integracao.getFileName();
	}

	public String write() throws IntegrationArchiveException {
		return null;
	}

	public void write(GenericVO vo, int collNumber)
			throws IntegrationArchiveException, IOException {
	}

	public String writeRecord(GenericVO vo, int collNumber, ResultSetMetaData mt)
			throws IntegrationArchiveException {
		return null;
	}
}
