package com.example.banvexe.controllers;

import com.example.banvexe.models.entities.Ticket;
import com.example.banvexe.models.entities.Trip;
import com.example.banvexe.services.PayOSService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.example.banvexe.repositories.TicketRepository;
import com.example.banvexe.repositories.TripRepository;
import com.example.banvexe.repositories.UserRepository;
import com.example.banvexe.models.entities.User;
import com.example.banvexe.models.dto.BookingRequest;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.*;

@Controller
public class PaymentController {

    @Autowired
    private PayOSService payOSService;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private TripRepository tripRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/login/payment")
    public String paymentPage() {
        return "payment";
    }

    @PostMapping("/api/payment/create")
    @ResponseBody
    public ResponseEntity<?> createOrder(@Valid @RequestBody Map<String, Object> request,
            Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "Bạn cần đăng nhập để đặt vé"));
        }
        try {
            String paymentMethod = (String) request.get("paymentMethod");
            Long amount = Long.valueOf(request.get("amount").toString());
            String seats = (String) request.get("seats");
            Long tripId = Long.valueOf(request.get("tripId").toString()); // ✅ FIX

            // ✅ LẤY USER
            String username = auth.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow();

            // ✅ LẤY TRIP
            Trip trip = tripRepository.findById(tripId)
                    .orElseThrow();

            int seatCount = seats.split(",").length;
            double total = trip.getPricePerTicket() * seatCount;

            if ("OFFLINE".equals(paymentMethod)) {

                Ticket ticket = new Ticket();
                ticket.setTrip(trip);
                ticket.setUser(user);
                ticket.setSeats(seats);
                ticket.setBookingTime(LocalDateTime.now());
                ticket.setStatus(Ticket.TicketStatus.PAID);
                ticket.setTotalAmount(total);

                ticketRepository.save(ticket);

                return ResponseEntity.ok(Map.of(
                        "message", "Đặt vé thành công",
                        "totalAmount", total));
            } else {
                // ✅ ONLINE PAY (PayOS)
                String url = payOSService.createPaymentLink(
                        total,
                        "Ve xe ghe: " + seats);

                return ResponseEntity.ok(Map.of("checkoutUrl", url));
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Lỗi: " + e.getMessage()));
        }
    }

    @GetMapping("/payment/success")
    public String success() {
        return "success";
    }

    @GetMapping("/payment/cancel")
    public String cancel() {
        return "cancel";
    }

    @PostMapping("/api/payment/pay-later")
    @ResponseBody
    public ResponseEntity<?> payLater(@RequestBody BookingRequest req,
            Authentication auth) {

        try {
            String username = auth.getName();

            User user = userRepository.findByUsername(username)
                    .orElseThrow();

            Trip trip = tripRepository.findById(req.getTripId())
                    .orElseThrow();

            // ✅ lấy đúng từ DTO
            List<String> seats = req.getSeatNumbers();

            if (seats == null || seats.isEmpty()) {
                return ResponseEntity.badRequest().body("Chưa chọn ghế");
            }

            // 🔥 1. CHECK TRÙNG GHẾ
            List<Ticket> tickets = ticketRepository.findByTrip(trip);

            Set<String> bookedSeats = new HashSet<>();

            for (Ticket t : tickets) {
                if (t.getSeats() != null) {
                    // 🔥 FIX: String → List
                    List<String> existingSeats = Arrays.asList(t.getSeats().split(","));
                    bookedSeats.addAll(existingSeats);
                }
            }

            for (String seat : seats) {
                if (bookedSeats.contains(seat)) {
                    return ResponseEntity.badRequest()
                            .body("Ghế " + seat + " đã được đặt!");
                }
            }

            // 🔥 2. TÍNH TIỀN
            double total = trip.getPricePerTicket() * seats.size();

            // 🔥 3. TẠO TICKET
            Ticket ticket = new Ticket();
            ticket.setTrip(trip);
            ticket.setUser(user);

            // 🔥 FIX QUAN TRỌNG NHẤT
            ticket.setSeats(String.join(",", seats));

            ticket.setBookingTime(LocalDateTime.now());
            ticket.setStatus(Ticket.TicketStatus.PAID);
            ticket.setTotalAmount(total);

            ticketRepository.save(ticket);

            return ResponseEntity.ok(Map.of(
                    "message", "Đặt vé thành công",
                    "totalAmount", total));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi: " + e.getMessage());
        }
    }
}