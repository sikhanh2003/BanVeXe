package com.example.banvexe.controllers;

import com.example.banvexe.models.entities.Trip;
import com.example.banvexe.services.TripService;
import com.example.banvexe.repositories.TripRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page; // Quan trọng: Import của Spring Data
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trips")
// Thêm CrossOrigin nếu bạn chạy Frontend và Backend khác Port (ví dụ Live
// Server 5500)
@CrossOrigin(origins = "*")
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
    @Autowired
    private TripRepository tripRepository;

    @GetMapping
    public ResponseEntity<List<Trip>> getAll() {
        // Chỉnh sửa Service để khi lấy Trip sẽ tự động Join luôn bảng Route
        List<Trip> trips = tripService.getAllTrips();
        return ResponseEntity.ok(trips);
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
    public ResponseEntity<Trip> create(@RequestBody Trip trip) {
    public ResponseEntity<Trip> create(@Valid @RequestBody Trip trip) {
        Trip savedTrip = tripService.createTrip(trip);
        return ResponseEntity.ok(savedTrip);
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
}