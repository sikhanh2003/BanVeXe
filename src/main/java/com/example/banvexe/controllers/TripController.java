package com.example.banvexe.controllers;

import com.example.banvexe.models.entities.Trip;
import com.example.banvexe.services.TripService;
import com.example.banvexe.services.SeatReservationService;
import com.example.banvexe.repositories.TripRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page; // Quan trọng: Import của Spring Data
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.Map;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/trips")
@CrossOrigin(origins = "*")
public class TripController {

    @Autowired
    private TripService tripService;
    @Autowired
    private TripRepository tripRepository;
    @Autowired
    private SeatReservationService seatReservationService;

    // SỬA TẠI ĐÂY: Thêm phân trang cho API
    @GetMapping
    public ResponseEntity<Page<Trip>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        // Gọi hàm phân trang đã viết trong TripService
        Page<Trip> tripPage = tripService.getAllTripsPaginated(page, size);
        return ResponseEntity.ok(tripPage);

    }

    // Lấy chi tiết một chuyến xe (Dùng để load giá vé và thông tin ở trang Booking)
    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTripById(@PathVariable Long id) {
        return tripRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Tạo mới chuyến xe
    @PostMapping
    public ResponseEntity<Trip> create(@Valid @RequestBody Trip trip) {
        Trip savedTrip = tripService.createTrip(trip);
        return ResponseEntity.ok(savedTrip);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Trip> update(
            @PathVariable Long id,
            @RequestBody Trip trip) {
        return ResponseEntity.ok(tripService.updateTrip(id, trip));
    }

    // Xóa chuyến xe
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tripService.deleteTrip(id);
        return ResponseEntity.ok().build();
    }

    // Tìm kiếm chuyến xe theo Điểm đi, Điểm đến và Ngày
    @GetMapping("/search")
    public ResponseEntity<List<Trip>> search(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam String date) {
        List<Trip> results = tripService.searchTrips(from, to, date);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{id}/seats/unavailable")
    public ResponseEntity<?> getUnavailableSeats(@PathVariable Long id) {
        Map<String, Set<String>> state = seatReservationService.getSeatState(id);
        return ResponseEntity.ok(Map.of(
                "bookedSeats", state.get("bookedSeats"),
                "heldSeats", state.get("heldSeats"),
                "unavailableSeats", state.get("unavailableSeats"),
                "holdMinutes", seatReservationService.getHoldMinutes()));
    }

    @PostMapping("/{id}/seats/hold")
    public ResponseEntity<?> holdSeat(@PathVariable Long id, @RequestBody Map<String, String> body,
            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Bạn cần đăng nhập"));
        }
        String seatNumber = body.get("seatNumber");
        Map<String, Object> result = seatReservationService.holdSeat(id, seatNumber, authentication.getName());
        if (Boolean.TRUE.equals(result.get("success"))) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
    }

    @DeleteMapping("/{id}/seats/hold")
    public ResponseEntity<?> releaseSeat(@PathVariable Long id, @RequestParam String seatNumber,
            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Bạn cần đăng nhập"));
        }
        seatReservationService.releaseSeat(id, seatNumber, authentication.getName());
        return ResponseEntity.ok(Map.of("success", true));
    }
}
