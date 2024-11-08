package com.example.demo.controller;

import com.example.demo.dto.InvoiceDTO;
import com.example.demo.models.Invoice;
import com.example.demo.services.InvoiceExcelService;
import com.example.demo.services.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private InvoiceExcelService invoiceExcelService;

    @PostMapping
    public Invoice createInvoice(@RequestBody InvoiceDTO invoiceDTO, @AuthenticationPrincipal UserDetails userDetails) {
        return invoiceService.createInvoice(invoiceDTO, userDetails);
    }

    @GetMapping
    public List<Invoice> getInvoices(@AuthenticationPrincipal UserDetails userDetails) {
        return invoiceService.getInvoices(userDetails);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteInvoice(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        invoiceService.deleteInvoice(id, userDetails);
        return ResponseEntity.ok("Invoice successfully deleted");
    }

    @GetMapping("/{id}")
    public Optional<Invoice> getInvoiceById(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return invoiceService.getInvoiceById(id, userDetails);
    }

    @GetMapping("/download-invoice/{id}")
    public ResponseEntity<InputStreamResource> downloadInvoiceExcel(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        Optional<Invoice> invoiceOptional = invoiceService.getInvoiceById(id, userDetails);

        if (invoiceOptional.isPresent()) {
            ByteArrayInputStream excelStream = invoiceExcelService.generateInvoiceExcel(invoiceOptional.get());

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=invoices.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(new InputStreamResource(excelStream));
        }
        return ResponseEntity.notFound().build();
    }

}
