package com.example.banvexe.controllers;


import com.example.banvexe.services.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping("/invoice/{ticketId}/pdf")
    public ResponseEntity<byte[]> exportInvoice(@PathVariable Long ticketId) {

        byte[] pdfBytes = invoiceService.generatePdf(ticketId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition.builder("inline")
                        .filename("invoice-" + ticketId + ".pdf")
                        .build()
        );

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
