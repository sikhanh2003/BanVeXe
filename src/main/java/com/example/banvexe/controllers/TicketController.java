package com.example.banvexe.controllers;

import com.example.banvexe.models.entities.Ticket;
import com.example.banvexe.models.entities.Trip;
import com.example.banvexe.services.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page; // QUAN TRỌNG: Thêm dòng này
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.banvexe.repositories.TicketRepository;
import com.example.banvexe.repositories.TripRepository;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
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

    @Autowired
    private TripRepository tripRepository;
    @Autowired
    private TicketRepository ticketRepository;

    // API phân trang
    @GetMapping
    public ResponseEntity<Page<Ticket>> getAllTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ticketService.getAllTicketsPaginated(page, size));
    }

    @PostMapping("/cancel-ticket/{ticketId}")
    @ResponseBody
    public ResponseEntity<?> cancelTicket(@PathVariable Long ticketId, Authentication auth) {

        try {
            Ticket ticket = ticketRepository.findById(ticketId).orElseThrow();

            // ❌ Không cho hủy lại
            if (ticket.getStatus() == Ticket.TicketStatus.CANCELLED) {
                return ResponseEntity.badRequest().body("Vé đã hủy rồi!");
            }

            Trip trip = ticket.getTrip();

            // 🔥 Số ghế cần hoàn
            int seatCount = ticket.getSeats().split(",").length;

            // 🔥 CỘNG GHẾ LẠI
            trip.setAvailableSeats(trip.getAvailableSeats() + seatCount);
            tripRepository.save(trip);

            // 🔥 Update trạng thái vé
            ticket.setStatus(Ticket.TicketStatus.CANCELLED);
            ticketRepository.save(ticket);

            return ResponseEntity.ok("Hủy vé thành công");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi: " + e.getMessage());
        }
    }

    // Lấy 1 ticket
    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
        Ticket ticket = ticketService.getTicketById(id);
        if (ticket != null) {
            return ResponseEntity.ok(ticket);
        }
        return ResponseEntity.notFound().build();
    }

    // Xóa ticket
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }

    // Xuất PDF hóa đơn
    @GetMapping("/invoice/{ticketId}/pdf")
    public ResponseEntity<byte[]> exportPdf(@PathVariable Long ticketId) {

        byte[] pdf = invoiceService.generatePdf(ticketId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header("Content-Disposition", "inline; filename=invoice.pdf")
                .body(pdf);
    }
}
