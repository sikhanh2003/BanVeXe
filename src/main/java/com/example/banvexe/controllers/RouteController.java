package com.example.banvexe.controllers;

import com.example.banvexe.models.entities.Route;
import com.example.banvexe.repositories.RouteRepository;
import com.example.banvexe.services.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    @Autowired
    private RouteService routeService;

    @Autowired
    private RouteRepository routeRepository;

    @GetMapping
    public List<Route> getAll() {
        return routeService.getAllRoutes();
    }

    @GetMapping("/{id}")
    public Route getById(@PathVariable Long id) {
        return routeService.getRouteById(id);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Route route) {
        try {
            Route created = routeService.createRoute(route);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            // Trả về nội dung lỗi cụ thể để bạn thấy được ở Console trình duyệt
            return ResponseEntity.status(500).body("Lỗi lưu database: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Route update(@PathVariable Long id, @RequestBody Route route) {
        return routeService.updateRoute(id, route);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        routeService.deleteRoute(id);
    }

    @GetMapping("/popular")
    public List<Route> getPopularRoutes() {
        return routeRepository.findAll(); // Hoặc lấy top 3 tùy ý
    }

    
}