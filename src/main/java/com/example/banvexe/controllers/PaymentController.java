package com.example.banvexe.controllers;

import com.example.banvexe.models.entities.Ticket;
import com.example.banvexe.models.entities.Trip;
import com.example.banvexe.services.PayOSService;
import com.example.banvexe.services.SeatReservationService;

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
    @Autowired
    private SeatReservationService seatReservationService;

    @PostMapping("/api/payment/create")
    @ResponseBody
    public ResponseEntity<?> createOrder(@Valid @RequestBody Map<String, Object> request,
            Authentication auth) {

        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "Bạn cần đăng nhập"));
        }

        try {
            String paymentMethod = (String) request.get("paymentMethod");
            String seats = (String) request.get("seats");
            Long tripId = Long.valueOf(request.get("tripId").toString());

            String username = auth.getName();
            User user = userRepository.findByUsername(username).orElseThrow();

            Trip trip = tripRepository.findById(tripId).orElseThrow();
            List<String> seatList = Arrays.asList(seats.split(","));
            Optional<String> seatValidationError = seatReservationService.validateForCheckout(tripId, seatList, username);
            if (seatValidationError.isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("error", seatValidationError.get()));
            }

            // 🔥 FIX CHUẨN
            double price = trip.getPricePerTicket();
            int seatCount = seats.split(",").length;
            double total = price * seatCount;

            if ("OFFLINE".equals(paymentMethod)) {

                Ticket ticket = new Ticket();
                ticket.setTrip(trip);
                ticket.setUser(user);
                ticket.setSeats(seats);
                ticket.setBookingTime(LocalDateTime.now());

                // 🔥 QUAN TRỌNG NHẤT
                ticket.setPriceAtBooking(price);

                ticket.setStatus(Ticket.TicketStatus.PAID);

                // 🔥 dùng giá lúc đặt
                ticket.setTotalAmount(price * seatCount);

                ticketRepository.save(ticket);
                trip.setAvailableSeats(trip.getAvailableSeats() - seatCount);
                tripRepository.save(trip);
                seatReservationService.releaseMyHolds(tripId, seatList, username);

                return ResponseEntity.ok(Map.of(
                        "message", "Đặt vé thành công",
                        "totalAmount", total));
            } else {
                String url = payOSService.createPaymentLink(
                        total,
                        "Ve xe ghe: " + seats);

                return ResponseEntity.ok(Map.of("checkoutUrl", url));
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/api/payment/pay-later")
    @ResponseBody
    public ResponseEntity<?> payLater(@RequestBody BookingRequest req,
            Authentication auth) {

        try {
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(401).body("Bạn cần đăng nhập");
            }

            String username = auth.getName();

            User user = userRepository.findByUsername(username).orElseThrow();
            Trip trip = tripRepository.findById(req.getTripId()).orElseThrow();

            List<String> seats = req.getSeatNumbers();

            if (seats == null || seats.isEmpty()) {
                return ResponseEntity.badRequest().body("Chưa chọn ghế");
            }

            Optional<String> seatValidationError = seatReservationService.validateForCheckout(req.getTripId(), seats, username);
            if (seatValidationError.isPresent()) {
                return ResponseEntity.badRequest().body(seatValidationError.get());
            }

            // 🔥 FIX CHUẨN
            double price = trip.getPricePerTicket();
            int seatCount = seats.size();

            Ticket ticket = new Ticket();
            ticket.setTrip(trip);
            ticket.setUser(user);
            ticket.setSeats(String.join(",", seats));
            ticket.setBookingTime(LocalDateTime.now());

            // 🔥 QUAN TRỌNG
            ticket.setPriceAtBooking(price);

            ticket.setStatus(Ticket.TicketStatus.PAID);

            // 🔥 dùng giá lúc đặt
            ticket.setTotalAmount(price * seatCount);

            ticketRepository.save(ticket);
            trip.setAvailableSeats(trip.getAvailableSeats() - seatCount);
            tripRepository.save(trip);
            seatReservationService.releaseMyHolds(req.getTripId(), seats, username);

            return ResponseEntity.ok(Map.of(
                    "message", "Đặt vé thành công",
                    "totalAmount", price * seatCount));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi: " + e.getMessage());
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
}