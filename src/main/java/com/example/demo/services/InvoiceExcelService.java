package com.example.demo.services;

import com.example.demo.models.Invoice;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class InvoiceExcelService {

    public ByteArrayInputStream generateInvoiceExcel(Invoice invoice) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Invoices");
            String[] headers = {"Invoice ID", "Invoice Number", "Issue Date", "Amount"};

            // Create Header Row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Populate data rows
            int rowIndex = 1;
//            for (Invoice invoice : invoices) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(invoice.getCustomer().getCustomerName());
            row.createCell(1).setCellValue(invoice.getInvoiceNumber());
            row.createCell(2).setCellValue(invoice.getInvoiceDate().toString());
            row.createCell(3).setCellValue(invoice.getTotalAmount());
//            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}
