package br.com.cafebinario.file.implementation;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSetMetaData;

import org.apache.log4j.Logger;

import br.com.cafebinario.exception.IntegrationArchiveException;
import br.com.cafebinario.file.interfaces.IIntegrationArchive;
import br.com.cafebinario.integration.EventWrite;
import br.com.cafebinario.integration.IIntegracao;
import br.com.cafebinario.properties.AppProperties;
import br.com.cafebinario.transport.GenericVO;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;

public class PdfIntegrationArchive implements IIntegrationArchive {

	private static Logger LOG = Logger.getLogger(PdfIntegrationArchive.class);
	private EventWrite<Document> callerEvent = null;
	private Document document = null;
	private String fileName = "";

	private IIntegracao integracao = null;
	protected AppProperties properties = AppProperties.instanceOf();

	public PdfIntegrationArchive() {

	}

	private void addContent(Document document) throws DocumentException {
		callerEvent.writeDocument(document);
	}

	public void discharge() throws IntegrationArchiveException {
		// TODO Auto-generated method stub

	}

	public void finalize() {
		System.out.println("Close PDF Document");
		document.close();
	}

	public EventWrite<Document> getCallerEvent() {
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

		try {
			document = new Document();
			PdfWriter.getInstance(document, new FileOutputStream(fileName));
			document.open();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}

		return fileName;
	}

	public String initializeOrder(IIntegracao integracao) {
		this.integracao = integracao;
		return null;
		// finalizar depois
	}

	public void setCallerEvent(EventWrite<Document> callerEvent) {
		this.callerEvent = callerEvent;
	}

	public String white() throws IntegrationArchiveException {
		try {
			addContent(document);
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
		// TODO Auto-generated method stub
		return null;
	}
}
