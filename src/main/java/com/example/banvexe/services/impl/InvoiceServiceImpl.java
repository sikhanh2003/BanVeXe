package com.example.banvexe.services.impl;

import com.example.banvexe.models.entities.Invoice;
import com.example.banvexe.models.entities.Ticket;
import com.example.banvexe.repositories.InvoiceRepository;
import com.example.banvexe.repositories.TicketRepository;
import com.example.banvexe.services.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.UUID;

// iText 7
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.PdfEncodings;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final TicketRepository ticketRepository;

    @Override
    public Invoice createInvoice(Long ticketId) {

        return invoiceRepository.findByTicketId(ticketId)
                .orElseGet(() -> {
                    Ticket ticket = ticketRepository.findById(ticketId)
                            .orElseThrow(() -> new RuntimeException("Ticket not found"));

                    return invoiceRepository.save(
                            Invoice.builder()
                                    .invoiceCode("INV-" + UUID.randomUUID().toString().substring(0, 8))
                                    .customerName(
                                            ticket.getUser() != null
                                                    ? ticket.getUser().getFullName()
                                                    : "Khách vãng lai")
                                    .route(
                                            ticket.getTrip() != null && ticket.getTrip().getRoute() != null
                                                    ? ticket.getTrip().getRoute().getDepartureLocation()
                                                            + " -> "
                                                            + ticket.getTrip().getRoute().getArrivalLocation()
                                                    : "Không xác định")
                                    .seats(ticket.getSeats())
                                    .totalAmount(
                                            ticket.getTotalAmount() != null
                                                    ? ticket.getTotalAmount()
                                                    : calculateTotal(ticket))
                                    .ticket(ticket)
                                    .build());
                });
    }

    @Override
    public Invoice getByTicketId(Long ticketId) {
        return invoiceRepository.findByTicketId(ticketId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
    }

    private Double calculateTotal(Ticket ticket) {
        if (ticket.getTrip() == null || ticket.getTrip().getPricePerTicket() == null)
            return 0.0;

        Double price = ticket.getTrip().getPricePerTicket(); // ❌ KHÔNG dùng ticket.getTotalAmount()

        int seatCount = 1;
        if (ticket.getSeats() != null && !ticket.getSeats().isEmpty()) {
            seatCount = ticket.getSeats().split(",").length;
        }

        return price * seatCount;
    }

    @Override
    public byte[] generatePdf(Long ticketId) {

        // ✅ Tự tạo invoice nếu chưa có
        Invoice invoice = invoiceRepository.findByTicketId(ticketId)
                .orElseGet(() -> createInvoice(ticketId));

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // ✅ Load font Unicode chuẩn (chạy được cả jar)
            String fontPath = getClass()
                    .getClassLoader()
                    .getResource("fonts/arial/arial.ttf")
                    .toURI()
                    .getPath();

            PdfFont font = PdfFontFactory.createFont(
                    fontPath,
                    PdfEncodings.IDENTITY_H,
                    pdf);

            document.setFont(font);

            // ===== TITLE =====
            document.add(new Paragraph("HÓA ĐƠN VÉ XE")
                    .setBold()
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph(" "));

            // ===== CONTENT =====
            document.add(new Paragraph("Mã hóa đơn: " + invoice.getInvoiceCode()));
            document.add(new Paragraph("Khách hàng: " + invoice.getCustomerName()));
            document.add(new Paragraph("Tuyến đường: " + invoice.getRoute()));
            document.add(new Paragraph("Ghế: " + invoice.getSeats()));
            document.add(new Paragraph("Tổng tiền: " + invoice.getTotalAmount() + " VND"));
            document.add(new Paragraph("Ngày tạo: " + invoice.getCreatedAt()));

            document.add(new Paragraph(" "));

            // ===== FOOTER =====
            document.add(new Paragraph("Cảm ơn bạn đã sử dụng dịch vụ!")
                    .setItalic()
                    .setTextAlignment(TextAlignment.CENTER));

            document.close();

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }
}
