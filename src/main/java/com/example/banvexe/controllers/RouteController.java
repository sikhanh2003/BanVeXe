package com.example.banvexe.controllers;

import com.example.banvexe.models.entities.Route;
import com.example.banvexe.repositories.RouteProjection;
import com.example.banvexe.repositories.RouteRepository;
import com.example.banvexe.services.RouteService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
@CrossOrigin(origins = "*")
public class RouteController {

    @Autowired
    private RouteService routeService;

    @Autowired
    private RouteRepository routeRepository;

    // 1. LẤY DANH SÁCH KÈM GIÁ (Dùng cho Trang chủ index)
    // Đường dẫn: GET /api/routes
    @GetMapping
    public ResponseEntity<List<RouteProjection>> getPopularRoutes() {
        List<RouteProjection> routes = routeRepository.findAllRoutesWithMinPrice();
        return ResponseEntity.ok(routes);
    }

    // 2. LẤY CHI TIẾT THEO ID
    // Đường dẫn: GET /api/routes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Route> getById(@PathVariable Long id) {
        Route route = routeService.getRouteById(id);
        return route != null ? ResponseEntity.ok(route) : ResponseEntity.notFound().build();
    }

    // 3. TẠO MỚI (Admin)
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Route route) {
        try {
            Route created = routeService.createRoute(route);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi lưu database: " + e.getMessage());
        }
    }

    // 4. CẬP NHẬT (Admin)
    @PutMapping("/{id}")
    public ResponseEntity<Route> update(@Valid @PathVariable Long id, @RequestBody Route route) {
        return ResponseEntity.ok(routeService.updateRoute(id, route));
    }

    // 5. XÓA (Admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        routeService.deleteRoute(id);
        return ResponseEntity.ok().build();
    }
}