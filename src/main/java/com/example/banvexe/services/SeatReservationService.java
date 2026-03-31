package com.example.banvexe.services;

import com.example.banvexe.models.entities.*;
import com.example.banvexe.repositories.SeatHoldRepository;
import com.example.banvexe.repositories.TicketRepository;
import com.example.banvexe.repositories.TripRepository;
import com.example.banvexe.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class SeatReservationService {
    private static final long HOLD_MINUTES = 5;

    private final TicketRepository ticketRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final SeatHoldRepository seatHoldRepository;

    public SeatReservationService(TicketRepository ticketRepository, TripRepository tripRepository,
            UserRepository userRepository, SeatHoldRepository seatHoldRepository) {
        this.ticketRepository = ticketRepository;
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
        this.seatHoldRepository = seatHoldRepository;
    }

    public long getHoldMinutes() {
        return HOLD_MINUTES;
    }

    public Map<String, Set<String>> getSeatState(Long tripId) {
        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new RuntimeException("Trip not found"));
        LocalDateTime now = LocalDateTime.now();
        seatHoldRepository.deleteByExpiresAtBefore(now);

        Set<String> bookedSeats = new HashSet<>();
        List<Ticket> committedTickets = ticketRepository.findByTripAndStatusIn(
                trip,
                List.of(Ticket.TicketStatus.PAID, Ticket.TicketStatus.BOOKED));
        for (Ticket ticket : committedTickets) {
            bookedSeats.addAll(parseSeatCsv(ticket.getSeats()));
        }

        Set<String> heldSeats = new HashSet<>();
        List<SeatHold> holdSeats = seatHoldRepository.findByTripAndExpiresAtAfter(trip, now);
        for (SeatHold hold : holdSeats) {
            heldSeats.add(hold.getSeatNumber());
        }

        Set<String> unavailable = new HashSet<>(bookedSeats);
        unavailable.addAll(heldSeats);

        Map<String, Set<String>> state = new HashMap<>();
        state.put("bookedSeats", bookedSeats);
        state.put("heldSeats", heldSeats);
        state.put("unavailableSeats", unavailable);
        return state;
    }

    @Transactional
    public Map<String, Object> holdSeat(Long tripId, String seatNumber, String username) {
        Trip trip = tripRepository.findByIdForUpdate(tripId).orElseThrow(() -> new RuntimeException("Trip not found"));
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        LocalDateTime now = LocalDateTime.now();
        seatHoldRepository.deleteByExpiresAtBefore(now);
        String normalizedSeat = normalizeSeat(seatNumber);

        if (normalizedSeat == null) {
            return Map.of("success", false, "message", "Ghế không hợp lệ");
        }

        Set<String> bookedSeats = extractSeatSet(
                ticketRepository.findByTripAndStatusIn(trip, List.of(Ticket.TicketStatus.PAID, Ticket.TicketStatus.BOOKED)));
        if (bookedSeats.contains(normalizedSeat)) {
            return Map.of("success", false, "message", "Ghế đã được đặt");
        }

        Optional<SeatHold> existingHold = seatHoldRepository.findByTripAndSeatNumberAndExpiresAtAfter(trip, normalizedSeat, now);
        if (existingHold.isPresent()) {
            SeatHold hold = existingHold.get();
            if (hold.getUser() != null && hold.getUser().getId().equals(user.getId())) {
                return Map.of(
                        "success", true,
                        "alreadyHeld", true,
                        "holdUntil", hold.getExpiresAt().toString());
            }
            return Map.of("success", false, "message", "Ghế đang được người khác giữ");
        }

        SeatHold hold = new SeatHold();
        hold.setTrip(trip);
        hold.setUser(user);
        hold.setSeatNumber(normalizedSeat);
        hold.setCreatedAt(now);
        hold.setExpiresAt(now.plusMinutes(HOLD_MINUTES));
        seatHoldRepository.save(hold);

        return Map.of(
                "success", true,
                "alreadyHeld", false,
                "holdUntil", hold.getExpiresAt().toString());
    }

    @Transactional
    public void releaseSeat(Long tripId, String seatNumber, String username) {
        Trip trip = tripRepository.findByIdForUpdate(tripId).orElseThrow(() -> new RuntimeException("Trip not found"));
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        LocalDateTime now = LocalDateTime.now();
        seatHoldRepository.deleteByExpiresAtBefore(now);
        String normalizedSeat = normalizeSeat(seatNumber);

        seatHoldRepository.findByTripAndSeatNumberAndExpiresAtAfter(trip, normalizedSeat, now)
                .filter(hold -> hold.getUser().getId().equals(user.getId()))
                .ifPresent(seatHoldRepository::delete);
    }

    public Optional<String> validateForCheckout(Long tripId, List<String> seats, String username) {
        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new RuntimeException("Trip not found"));
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        LocalDateTime now = LocalDateTime.now();
        seatHoldRepository.deleteByExpiresAtBefore(now);

        Set<String> requested = new HashSet<>();
        for (String seat : seats) {
            String normalized = normalizeSeat(seat);
            if (normalized == null) {
                return Optional.of("Ghế không hợp lệ");
            }
            requested.add(normalized);
        }

        Set<String> bookedSeats = extractSeatSet(
                ticketRepository.findByTripAndStatusIn(trip, List.of(Ticket.TicketStatus.PAID, Ticket.TicketStatus.BOOKED)));
        for (String seat : requested) {
            if (bookedSeats.contains(seat)) {
                return Optional.of("Ghế " + seat + " đã được đặt");
            }
        }

        List<SeatHold> holds = seatHoldRepository.findByTripAndExpiresAtAfter(trip, now);
        for (SeatHold hold : holds) {
            if (hold.getUser() != null && hold.getUser().getId().equals(user.getId())) {
                continue;
            }
            if (requested.contains(hold.getSeatNumber())) {
                return Optional.of("Ghế " + hold.getSeatNumber() + " đang được người khác giữ");
            }
        }
        return Optional.empty();
    }

    @Transactional
    public void releaseMyHolds(Long tripId, List<String> seats, String username) {
        Trip trip = tripRepository.findByIdForUpdate(tripId).orElseThrow(() -> new RuntimeException("Trip not found"));
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        LocalDateTime now = LocalDateTime.now();
        seatHoldRepository.deleteByExpiresAtBefore(now);
        Set<String> requested = new HashSet<>();
        for (String seat : seats) {
            String normalized = normalizeSeat(seat);
            if (normalized != null) {
                requested.add(normalized);
            }
        }

        List<SeatHold> myHolds = seatHoldRepository.findByTripAndUserAndExpiresAtAfter(trip, user, now);
        for (SeatHold hold : myHolds) {
            if (requested.contains(hold.getSeatNumber())) {
                seatHoldRepository.delete(hold);
            }
        }
    }

    private Set<String> extractSeatSet(List<Ticket> tickets) {
        Set<String> result = new HashSet<>();
        for (Ticket ticket : tickets) {
            result.addAll(parseSeatCsv(ticket.getSeats()));
        }
        return result;
    }

    private Set<String> parseSeatCsv(String seatCsv) {
        if (seatCsv == null || seatCsv.isBlank()) {
            return Collections.emptySet();
        }
        Set<String> result = new HashSet<>();
        for (String seat : seatCsv.split(",")) {
            String normalized = normalizeSeat(seat);
            if (normalized != null) {
                result.add(normalized);
            }
        }
        return result;
    }

    private String normalizeSeat(String seat) {
        if (seat == null) {
            return null;
        }
        String trimmed = seat.trim().toUpperCase(Locale.ROOT);
        return trimmed.isEmpty() ? null : trimmed;
    }
}
