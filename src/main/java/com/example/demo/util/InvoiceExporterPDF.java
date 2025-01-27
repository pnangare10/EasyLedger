package com.example.demo.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.example.demo.models.SalesOrderItem;
import com.example.demo.models.SalesTransaction;
import org.apache.commons.math3.util.Precision;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

import com.example.demo.models.Invoice;
import com.example.demo.models.Product;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

@Component
public class InvoiceExporterPDF {
	
	Invoice invoice;
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

//	@Autowired
//	EmailService mailer;
	
	public InvoiceExporterPDF(Invoice invoice) {
		super();
		this.invoice = invoice;
	}

	public InvoiceExporterPDF() {
		super();
	}

	public ByteArrayInputStream export() throws IOException {
//		OutputStream output = response.getOutputStream();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		PdfDocument pdfDoc = new PdfDocument(new PdfWriter(out));
		Document doc = new Document(pdfDoc);
		try {
		FontProgram fontProgram = FontProgramFactory
				.createFont("com\\example\\demo\\helper\\calibri.ttf");
		PdfFont calibri = PdfFontFactory.createFont(fontProgram, PdfEncodings.WINANSI);
		doc.setFont(calibri);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		
		float[] columnWidths = new float[] { (float) 0.563828552, (float) 2.559879196, (float) 0.72203508,
				(float) 0.563828552, (float) 0.899756069, (float) 0.690672552 };

		Table table = new Table(UnitValue.createPercentArray(columnWidths));
		table.setWidth(UnitValue.createPercentValue(100));
		table.setFixedLayout();
		
		double ht = 15;
		double fontSize = 10;

		createCellFirst(table);
		// Buyer
		createCell(table, "Buyer", TextAlignment.LEFT, 1, 2, true, fontSize, ht);
		// Invoice no
		createCell(table, "Invoice No :", TextAlignment.LEFT, 1, 2, true, fontSize, ht);
		// Invoice value
		createCell(table, invoice.getInvoiceNumber().toString(), TextAlignment.LEFT, 1, 2, false, fontSize, 16.0);
		// Buyer Name
		createCell(table, invoice.getCustomer().getCustomerName(), TextAlignment.LEFT, 1, 2, false, fontSize, ht);
		// Vendor code
		createCell(table, "Vendor Code :", TextAlignment.LEFT, 1, 2, true, fontSize, ht);
		// Vendor Code Value
		createCell(table, invoice.getCustomer().getVendorCode(), TextAlignment.LEFT, 1, 2, false, fontSize, ht);
		// Buyer Address
		createCell(table, invoice.getCustomer().getAddress(), TextAlignment.LEFT, 3, 2, false, fontSize, ht);
		// Date
		createCell(table, "Date :", TextAlignment.LEFT, 1, 2, true, fontSize, ht);
		// Date Value
		createCell(table, sdf.format(invoice.getInvoiceDate()), TextAlignment.LEFT, 1, 2, false, fontSize, ht);
		// Blank
		createCell(table, "Payment Term : ADVANCE PAYMENT", TextAlignment.LEFT, 2, 4, true, fontSize, ht);
		// GST No
		createCell(table, "GST No : ", TextAlignment.LEFT, 1, 1, true, fontSize, ht);
		// GST No Value
		createCell(table, invoice.getCustomer().getGstNumber(), TextAlignment.LEFT, 1, 1, false, fontSize, ht);
		// Empty Row
		createCell(table, "", TextAlignment.LEFT, 1, 4, false, fontSize, ht);
		// table headers
		createCell(table, "Sr No.", TextAlignment.CENTER, 1, 1, true, fontSize, ht);
		createCell(table, "Description", TextAlignment.CENTER, 1, 1, true, fontSize, ht);
		createCell(table, "HSN Code", TextAlignment.CENTER, 1, 1, true, fontSize, ht);
		createCell(table, "Qty", TextAlignment.CENTER, 1, 1, true, fontSize, ht);
		createCell(table, "Rate", TextAlignment.CENTER, 1, 1, true, fontSize, ht);
		createCell(table, "Amount", TextAlignment.CENTER, 1, 1, true, fontSize, ht);
		List<SalesOrderItem> list = this.invoice.getSalesOrder().getItems();
		Integer i = 0;
		Double amount;
		Integer sr = 0;
		while (i < 7 || i <= list.size()) {
			try {
				SalesOrderItem entry = list.get(i);
				amount = entry.getQuantity() * entry.getUnitPrice();
				sr = i+1;
				createCell(table, sr.toString(), TextAlignment.CENTER, 1, 1, false, fontSize, ht);
				createCell(table, entry.getProduct().getName(), TextAlignment.CENTER, 1, 1, false, fontSize, ht);
				createCell(table, entry.getProduct().getHsnCode(), TextAlignment.CENTER, 1, 1, false, fontSize, ht);
				createCell(table, entry.getQuantity().toString(), TextAlignment.CENTER, 1, 1, false, fontSize, ht);
				createCell(table, entry.getUnitPrice().toString(), TextAlignment.CENTER, 1, 1, false, fontSize, ht);
				createCell(table, amount.toString(), TextAlignment.CENTER, 1, 1, false, fontSize, ht);
			} catch (Exception e) {
				addEmptyCells(table , fontSize, ht , 6);
			}
			i++;
		}
		
		
		Double excGST = invoice.getTotalAmount();
		Double gst = excGST*0.09;
		Double incGST = excGST + gst*2;
		Double roundOff = incGST - incGST.intValue();
		roundOff = Precision.round(roundOff , 2);
		Integer grandTotal = (int) (incGST - roundOff);
		gst = Precision.round(gst , 2);
		
		addEmptyCells(table , fontSize, ht , 4);
		//Total
		createCell(table, "Total", TextAlignment.LEFT, 1, 1, true, fontSize, ht);
		createCell(table, excGST.toString(), TextAlignment.LEFT, 1, 1, true, fontSize, ht);
		
		addEmptyCells(table , fontSize, ht , 4);
		//SGST
		createCell(table, "SGST @9%", TextAlignment.LEFT, 1, 1, true, fontSize, ht);
		createCell(table, gst.toString(), TextAlignment.LEFT, 1, 1, true, fontSize, ht);
		
		addEmptyCells(table , fontSize, ht , 4);
		createCell(table, "CGST@9%", TextAlignment.LEFT, 1, 1, true, fontSize, ht);
		createCell(table, gst.toString(), TextAlignment.LEFT, 1, 1, true, fontSize, ht);
		
		addEmptyCells(table , fontSize, ht , 4);
		createCell(table, "Round Off", TextAlignment.LEFT, 1, 1, true, fontSize, ht);
		createCell(table, roundOff.toString(), TextAlignment.LEFT, 1, 1, true, fontSize, ht);
		addEmptyCells(table , fontSize, ht , 4);
		createCell(table, "Grand Total", TextAlignment.LEFT, 1, 1, true, fontSize, ht);
		createCell(table, grandTotal.toString(), TextAlignment.LEFT, 1, 1, true, fontSize, ht);
		
		
		// Amount in words
		createCell(table, "Amount Chargeable (in words)", TextAlignment.LEFT, 1, 6, true, fontSize, ht);
		// INR
		createCell(table, "INR", TextAlignment.CENTER, 1, 1, false, fontSize, ht);
		// words
		createCell(table, NumToWords.convert(grandTotal), TextAlignment.LEFT, 1, 3, false, fontSize, ht);
		// only
		createCell(table, "only", TextAlignment.CENTER, 1, 1, false, fontSize, ht);
		// Empty Cell
		createCell(table, "", TextAlignment.CENTER, 1, 1, true, fontSize, ht);
		// Note
		createCell(table,
				"I/We hereby certify that our registration certificate under GST Act. 2017 is in force on the date on which the sale of goods specified in this tax invoice is made by usand that the transaction of sale covered by this invoice.",
				TextAlignment.CENTER, 3, 6, false, 7.0, 32.0);
		// empty cell
		createCell(table, "", TextAlignment.CENTER, 1, 3, true, fontSize, ht);
		// Pranesh Enterprises signature
		createCell(table, "For Pranesh Enterprises", TextAlignment.LEFT, 1, 3, true, fontSize, ht);
		// Empty space
		createCell(table, " ", TextAlignment.CENTER, 2, 6, true, fontSize, 42.0);
		// empty cell
		createCell(table, "", TextAlignment.CENTER, 1, 3, true, fontSize, ht);
		// For authorised signatory
		createCell(table, "Authorised Signatory", TextAlignment.LEFT, 1, 3, false, fontSize, ht);
		// Subject to pune jurisdiction
		createCell(table, "SUBJECT TO PUNE JURISDICTION", TextAlignment.CENTER, 1, 6, false, fontSize, ht);
		// this is computer generated invoice
		createCell(table, "This is a Computer Generated Invoice", TextAlignment.CENTER, 1, 6, false, fontSize, ht);
		doc.add(table);
		doc.close();
		return new ByteArrayInputStream(out.toByteArray());

	}

	private static void createCell(Table table, String text, TextAlignment align, int rowspan, int colspan,
			boolean isBold, Double fontSize, Double rowHeight) throws IOException {
		Paragraph paragraph = new Paragraph(text);
		// paragraph.setHorizontalAlignment(align);
		if (isBold) {
			paragraph.setBold();
		}
		paragraph.setFontSize(Float.parseFloat(fontSize.toString()));
		Cell cell = new Cell(rowspan, colspan).add(paragraph).setTextAlignment(align);

		cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
		cell.setHeight(Float.parseFloat(rowHeight.toString()));
		cell.setBorder(Border.NO_BORDER).setBorderTop(new SolidBorder(1f)).setBorderBottom(new SolidBorder(1f))
				.setBorderLeft(new SolidBorder(1f)).setBorderRight(new SolidBorder(1f));

		table.addCell(cell);
	}

	private static void createCellFirst(Table table) throws IOException {
		String str = "Plot no. 89, Gat no. 24, Sant Tukaramnagar,\n" + "Kadachiwadi, Chakan , Tal. Khed, Pune 410501 \n"
				+ "Mob.: 9423008805 , 9834323772 Email: pranesh.ent1005@gmail.com";
//			+ "GST No : 27ADEPN4739N2ZY ";
		Paragraph paragraph = new Paragraph("Tax Invoice");
		Paragraph paragraph1 = new Paragraph("PRANESH ENTERPRISES");
		Paragraph paragraph2 = new Paragraph(str);
		Paragraph paragraph3 = new Paragraph("GST No : 27ADEPN4739N2ZY ");

		paragraph.setFontSize(Float.parseFloat("24"));
		paragraph1.setFontSize(Float.parseFloat("24"));
		paragraph2.setFontSize(Float.parseFloat("9.0"));
		paragraph3.setFontSize(Float.parseFloat("9.0"));

		paragraph.setBold();
		paragraph1.setBold();
		paragraph3.setBold();

		Cell cell = new Cell(1, 6).add(paragraph).add(paragraph1).add(paragraph2).add(paragraph3)
				.setTextAlignment(TextAlignment.CENTER);

		cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
		cell.setHeight(Float.parseFloat("130"));
		cell.setBorder(Border.NO_BORDER).setBorderTop(new SolidBorder(1f)).setBorderBottom(new SolidBorder(1f))
				.setBorderLeft(new SolidBorder(1f)).setBorderRight(new SolidBorder(1f));
		table.addCell(cell);
	}

	public static void addEmptyCells(Table table , Double fontSize, Double ht , int num) throws IOException {
		for(int i=0;i<num;i++)
		createCell(table, "", TextAlignment.CENTER, 1, 1, false, fontSize, ht);
	}
}
