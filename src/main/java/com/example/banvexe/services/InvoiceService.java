package com.example.banvexe.services;

import com.example.banvexe.models.entities.Invoice;

public interface InvoiceService {

    Invoice createInvoice(Long ticketId);

    Invoice getByTicketId(Long ticketId);

    byte[] generatePdf(Long ticketId);
}