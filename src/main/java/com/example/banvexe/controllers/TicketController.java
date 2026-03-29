package com.example.banvexe.controllers;

import com.example.banvexe.models.entities.Ticket;
import com.example.banvexe.services.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page; // QUAN TRỌNG: Thêm dòng này
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import com.example.banvexe.services.InvoiceService;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "*")
public class TicketController {

    @Autowired
    private TicketService ticketService;
    @Autowired
    private InvoiceService invoiceService;


    @GetMapping
    public ResponseEntity<Page<Ticket>> getAllTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(ticketService.getAllTicketsPaginated(page, size));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    public ResponseEntity<List<Ticket>> getAllTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        try {

            List<Ticket> tickets = ticketService.getAllTickets();
            if (tickets.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(tickets, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) { // Đã sửa lỗi village ở đây
        Ticket ticket = ticketService.getTicketById(id);
        if (ticket != null) {
            return new ResponseEntity<>(ticket, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteTicket(@PathVariable Long id) {
        try {
            ticketService.deleteTicket(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/invoice/{ticketId}/pdf")
    public ResponseEntity<byte[]> exportPdf(@PathVariable Long ticketId) {

        byte[] pdf = invoiceService.generatePdf(ticketId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header("Content-Disposition", "inline; filename=invoice.pdf")
                .body(pdf);
    }
}