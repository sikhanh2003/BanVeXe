package com.example.banvexe.controllers;

import com.example.banvexe.models.entities.Trip;
import com.example.banvexe.services.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    @Autowired
    private TripService tripService;

    @GetMapping
    public List<Trip> getAll() {
        return tripService.getAllTrips();
    }

    @PostMapping
    public ResponseEntity<Trip> create(@RequestBody Trip trip) {
        // Spring sẽ tự map JSON (availableSeats, pricePerTicket) vào Object Trip
        Trip savedTrip = tripService.createTrip(trip);
        return ResponseEntity.ok(savedTrip);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tripService.deleteTrip(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
        public ResponseEntity<List<Trip>> search(@RequestParam String from, 
                                                @RequestParam String to, 
                                                @RequestParam String date) {
            List<Trip> results = tripService.searchTrips(from, to, date);
            return ResponseEntity.ok(results);
        }

        

}