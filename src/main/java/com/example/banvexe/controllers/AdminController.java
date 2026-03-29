package com.example.banvexe.controllers;

import com.example.banvexe.models.entities.Trip;
import com.example.banvexe.models.entities.Ticket; // Import thêm Ticket entity
import com.example.banvexe.services.TripService;
import com.example.banvexe.services.TicketService; // Import thêm TicketService
import com.example.banvexe.models.dto.AdminDashboardDTO;
import com.example.banvexe.models.entities.Ticket;
import com.example.banvexe.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.ResponseBody;
import java.util.List;
import com.example.banvexe.repositories.TicketRepository;

/**
 *  * Controller quản lý khu vực Admin
 *  * Đã tích hợp Service để lấy dữ liệu thực tế từ Database
 *  
 */
@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private TripService tripService;

    @Autowired
    private TicketService ticketService; // Inject TicketService vào đây
    private AdminService adminService;
    @Autowired
    private TicketRepository ticketRepository;

    // 1. Trang Thống kê
    @GetMapping({"", "/", "/dashboard"})
    public String dashboardPage() {
        return "admin/dashboard";
    }

    // 2. Trang Quản lý Tuyến xe
    @GetMapping("/routes")
    public String routesPage() {
        return "admin/routes";
    }

    // 3. Trang Quản lý Xe khách
    @GetMapping("/buses")
    public String busesPage() {
        return "admin/buses";
    }

    // 4. Trang Quản lý Chuyến xe (Đã OK)
    @GetMapping("/trips")
    public String tripsPage(
            Model model, 
            @RequestParam(defaultValue = "0") int page
    ) {
        int pageSize = 10;
        Page<Trip> tripPage = tripService.getAllTripsPaginated(page, pageSize);
        model.addAttribute("trips", tripPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", tripPage.getTotalPages());
        return "admin/trips";
    }

    // 5. SỬA LẠI: Trang Quản lý Vé đã đặt (Thêm phân trang và truyền dữ liệu)
    @GetMapping("/tickets")
    public String ticketsPage(
            Model model,
            @RequestParam(defaultValue = "0") int page
    ) {
        int pageSize = 10;
        // Gọi hàm phân trang từ TicketService mà chúng ta vừa tạo lúc nãy
        Page<Ticket> ticketPage = ticketService.getAllTicketsPaginated(page, pageSize);
        
        // Đẩy dữ liệu sang file admin/tickets.html
        model.addAttribute("tickets", ticketPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", ticketPage.getTotalPages());
        
        return "admin/tickets";
    }

    // 6. Trang Quản lý Người dùng
    @GetMapping("/users")
    public String usersPage() {
        return "admin/users";
    public String ticketPage() {
        return "admin/tickets";
    }

    // @GetMapping("/tickets")
    // public ResponseEntity<List<Ticket>> getAllTickets() {
    //     List<Ticket> tickets = ticketRepository.findAllWithDetails();
    //     return ResponseEntity.ok(tickets);
    // }

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