package com.example.banvexe.controllers;

import com.example.banvexe.models.entities.Trip;
import com.example.banvexe.models.entities.Ticket; // Import thêm Ticket entity
import com.example.banvexe.services.TripService;
import com.example.banvexe.services.TicketService; // Import thêm TicketService
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private TripService tripService;

    @Autowired
    private TicketService ticketService; // Inject TicketService vào đây

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
    }
}