package com.example.banvexe.controllers;

import com.example.banvexe.models.entities.Trip;
import com.example.banvexe.services.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page; // Quan trọng: Import của Spring Data
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    @Autowired
    private TripService tripService;

    // SỬA TẠI ĐÂY: Thêm phân trang cho API
    @GetMapping
    public ResponseEntity<Page<Trip>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // Gọi hàm phân trang đã viết trong TripService
        Page<Trip> tripPage = tripService.getAllTripsPaginated(page, size);
        return ResponseEntity.ok(tripPage);
    }

    @PostMapping
    public ResponseEntity<Trip> create(@RequestBody Trip trip) {
        Trip savedTrip = tripService.createTrip(trip);
        return ResponseEntity.ok(savedTrip);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tripService.deleteTrip(id);
        return ResponseEntity.ok().build();
    }
}