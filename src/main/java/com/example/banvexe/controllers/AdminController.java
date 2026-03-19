package com.example.banvexe.controllers;

import com.example.banvexe.models.dto.AdminDashboardDTO;
import com.example.banvexe.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller quản lý khu vực Admin
 * Đã tích hợp Service để lấy dữ liệu thực tế từ Database
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // ==========================================
    // 1. CÁC ROUTE TRẢ VỀ GIAO DIỆN (HTML)
    // ==========================================

    @GetMapping("/dashboard")
    public String dashboardPage() {
        // Trả về file: src/main/resources/templates/admin/dashboard.html
        return "admin/dashboard";
    }

    @GetMapping("/routes")
    public String routesPage() {
        return "admin/routes";
    }

    @GetMapping("/buses")
    public String busesPage() {
        return "admin/buses";
    }

    @GetMapping("/trips")
    public String tripsPage() {
        return "admin/trips";
    }

    @GetMapping("/users")
    public String usersPage() {
        return "admin/users";
    }

    @GetMapping("/tickets")
    public String ticketsPage() {
        return "admin/tickets";
    }

    // ==========================================
    // 2. CÁC API TRẢ VỀ DỮ LIỆU (JSON)
    // ==========================================

    /**
     * API lấy toàn bộ số liệu thống kê cho Dashboard
     * Trả về JSON để JavaScript (fetch) xử lý hiển thị
     */
    @GetMapping("/api/admin/stats")
    @ResponseBody // Bắt buộc phải có để trả về JSON, tránh lỗi Unexpected token '<'
    public ResponseEntity<AdminDashboardDTO> getDashboardStats() {
        try {
            AdminDashboardDTO stats = adminService.getDashboardStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            // Log lỗi nếu cần thiết
            return ResponseEntity.internalServerError().build();
        }
    }
}