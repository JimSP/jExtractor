package br.com.cafebinario.file.implementation;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSetMetaData;
import java.util.Enumeration;

import org.apache.log4j.Logger;

import br.com.cafebinario.exception.IntegrationArchiveException;
import br.com.cafebinario.file.interfaces.IIntegrationArchive;
import br.com.cafebinario.integration.IIntegracao;
import br.com.cafebinario.properties.AppProperties;
import br.com.cafebinario.transport.GenericTO;
import br.com.cafebinario.transport.GenericVO;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class PdfIntegrationArchive implements IIntegrationArchive {

	private static Logger LOG = Logger.getLogger(PdfIntegrationArchive.class);

	private Document document = null;
	private PdfPTable pdfTable = null;

	private String fileName = "";
	private PdfWriter pdfWriter;

	private IIntegracao integracao = null;
	protected AppProperties properties = AppProperties.instanceOf();

	public void finalize() {
		System.out.println("Close PDF Document");
		document.close();
	}

	@Override
	public void discharge() throws IntegrationArchiveException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getFileName() {
		return fileName;
	}

	@Override
	public String initialize(IIntegracao integracao) throws IOException {
		this.integracao = integracao;

		this.fileName = integracao.getFileName();
		LOG.debug("OUTPUT_FILE=" + fileName);

		try {
			document = new Document();
			pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(
					this.fileName));
			document.open();
			
			createTable(getCollumn(integracao.getData()));
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}

		return fileName;
	}

	@Override
	public String initializeOrder(IIntegracao integracao) throws IOException {
		return this.initialize(integracao);
	}

	@Override
	public String write() throws IntegrationArchiveException {
		GenericTO to = this.integracao.getData();

		try {
			int i = 1;

			for (GenericVO genericVO : to) {
				write(genericVO, i++);
			}
			
			document.add(pdfTable);

		} catch (IOException e) {
			throw new IntegrationArchiveException(
					"erro ao tentar criar documento PDF. ", e);
		} catch (DocumentException e) {
			throw new IntegrationArchiveException(
					"erro ao tentar criar documento PDF. ", e);
		}

		return this.fileName;
	}

	private String[] getCollumn(GenericTO to) {
		return properties.getProperty("COLUMNS_HEADER").split(",");
	}

	private PdfPTable createTable(String[] collumns) throws DocumentException {
		PdfPTable table = new PdfPTable(collumns.length);

		for (String collumn : collumns) {
			PdfPCell cell = new PdfPCell(new Phrase(collumn));
			cell.setColspan(3);
			table.addCell(cell);
		}

		this.pdfTable = table;

		return table;
	}

	@Override
	public void write(GenericVO vo, int collNumber)
			throws IntegrationArchiveException, IOException {

		Enumeration<Integer> keys = vo.keys();

		while (keys.hasMoreElements()) {
			Integer key = keys.nextElement();
			String value = vo.get(key);
			pdfTable.addCell(value);
		}

		//pdfTable.completeRow();
	}

	@Override
	public String writeRecord(GenericVO vo, int collNumber, ResultSetMetaData mt)
			throws IntegrationArchiveException {
		return null;
	}
}
