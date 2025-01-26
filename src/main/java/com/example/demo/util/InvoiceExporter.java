package com.example.demo.util;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.example.demo.models.SalesTransaction;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.sl.usermodel.Sheet;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;

import com.example.demo.models.Invoice;
import com.example.demo.models.Product;
import com.example.demo.util.EmailService;
import org.springframework.core.io.ClassPathResource;

public class InvoiceExporter {
	private XSSFWorkbook workbook;
	private XSSFSheet sheet;
	private Integer rowCount = 0;
	private Integer coloumnCount = 0;
	private Row row;
	private FileInputStream file;
	Invoice invoice;
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	//NumToWords ntw;
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	public InvoiceExporter(Invoice invoice) throws IOException {
		this.invoice = invoice;
		workbook = new XSSFWorkbook();
		ClassPathResource resource = new ClassPathResource("inv.xlsx");
		try (InputStream file = resource.getInputStream()) {
			workbook = new XSSFWorkbook(file);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private void substitute() {
		CellStyle styleBlack = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setFontHeight(11);
		font.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
		styleBlack.setFont(font);
		styleBlack.setAlignment(HorizontalAlignment.LEFT);
		styleBlack.setVerticalAlignment(VerticalAlignment.TOP);
		styleBlack.setBorderBottom(BorderStyle.THIN);
		styleBlack.setBorderTop(BorderStyle.THIN);
		styleBlack.setBorderLeft(BorderStyle.THIN);
		styleBlack.setBorderRight(BorderStyle.THIN);
		
		CellStyle styleNum = workbook.createCellStyle();
//		styleNum = styleBlack;
		styleNum.setFont(font);
		styleNum.setVerticalAlignment(VerticalAlignment.TOP);
		styleNum.setBorderBottom(BorderStyle.THIN);
		styleNum.setBorderTop(BorderStyle.THIN);
		styleNum.setBorderLeft(BorderStyle.THIN);
		styleNum.setBorderRight(BorderStyle.THIN);
		styleNum.setAlignment(HorizontalAlignment.CENTER);
		
		XSSFSheet sheet = workbook.getSheetAt(0);
		// Buyer Name
		row = sheet.getRow(7);
		createCell(row, 0, invoice.getCustomer().getCustomerName(), styleBlack);
		// Address
		row = sheet.getRow(8);
		createCell(row, 0, invoice.getCustomer().getAddress(), styleBlack);
		// Invoice Number
		row = sheet.getRow(6);
		createCell(row, 4, invoice.getInvoiceNumber(), styleBlack);
		// Vendor Code
		row = sheet.getRow(7);
		createCell(row, 4, invoice.getCustomer().getVendorCode(), styleBlack);
		// Date
		row = sheet.getRow(8);
		createCell(row, 4, invoice.getInvoiceDate().format(formatter), styleBlack);
		// GST Number
		row = sheet.getRow(11);
		createCell(row, 1, invoice.getCustomer().getGstNumber(), styleBlack);

		int rownumber = 13;
		int srno = 1;
		Double rate = 0.0;
		int qty = 0;

		List<SalesTransaction> list = this.invoice.getSalesTransactions();
		for (SalesTransaction entry : list) {
			rate = entry.getPrice();
			qty = entry.getQuantity();
			if (srno > 7) {
				row = sheet.createRow(rownumber);
			} else {
				row = sheet.getRow(rownumber);
			}
			createCell(row, 0, srno, styleBlack);
			createCell(row, 1, entry.getProduct().getName(), styleBlack);
			createCell(row, 2, entry.getProduct().getHsnCode(), styleNum);
			createCell(row, 3, qty, styleNum);
			createCell(row, 4, rate, styleNum);
			createCell(row, 5, rate * qty, styleNum);
			rownumber++;
			srno++;
		}
		if(srno<=7) {
			rownumber = 20;
		}
		
		Double excGST = invoice.getTotalAmount();
		Double gst = excGST*0.09;
		Double incGST = excGST + gst*2;
		Double roundOff = incGST - incGST.intValue();
		Integer grandTotal = (int) (incGST - roundOff);
		//Total
		row = sheet.getRow(rownumber++);
		createCell(row,5 , excGST , styleNum);
		//SGST
		row = sheet.getRow(rownumber++);
		createCell(row,5 , gst , styleNum);
		//CGST
		row = sheet.getRow(rownumber++);
		createCell(row,5 , gst , styleNum);
		//roundoff
		row = sheet.getRow(rownumber++);
		createCell(row,5 , roundOff , styleNum);
		//Grand Total
		row = sheet.getRow(rownumber++);
		createCell(row,5 , grandTotal , styleNum);
		//amount in words
		rownumber++;
		row = sheet.getRow(rownumber++);
		createCell(row,1 , NumToWords.convert(grandTotal) , styleNum);
	}

	private void createCell(Row row, int columnCount, Object value, CellStyle style) {
		// TODO Auto-generated method stub
		Cell cell = row.createCell(columnCount);
		if (value instanceof Integer) {
			cell.setCellValue((Integer) value);
		} else if (value instanceof Boolean) {
			cell.setCellValue((Boolean) value);
		} else if (value instanceof Float) {
			cell.setCellValue((Float) value);
		} else if (value instanceof Double) {
			cell.setCellValue((Double) value);
		} else {
			cell.setCellValue((String) value);
		}
		cell.setCellStyle(style);
	}

	public ByteArrayInputStream export() throws IOException {
		substitute();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		workbook.write(out);
		return new ByteArrayInputStream(out.toByteArray());
	}

}
