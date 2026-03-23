package com.huy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bus")
public class BusController {

    @Autowired
    private TripService tripService;

    @Autowired
    private TicketService ticketService;

    // API Tìm chuyến: /api/bus/search?from=HaNoi&to=HaiPhong&date=2026-03-20
    @GetMapping("/search")
    public List<Trip> search(@RequestParam String from, @RequestParam String to, @RequestParam String date) {
        return tripService.searchTrips(from, to, date);
    }

    // API Giữ ghế: /api/bus/hold/1
    @PostMapping("/hold/{ticketId}")
    public ResponseEntity<String> hold(@PathVariable Long ticketId) {
        String result = ticketService.holdSeat(ticketId);
        return ResponseEntity.ok(result);
    }
}