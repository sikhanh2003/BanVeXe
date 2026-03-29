package com.example.banvexe.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.banvexe.models.entities.Ticket;
import com.example.banvexe.models.entities.Trip;
import com.example.banvexe.repositories.TicketRepository;
import com.example.banvexe.repositories.UserRepository;
import com.example.banvexe.models.entities.User;
import java.util.List;
import java.util.Map;
import com.example.banvexe.services.TripService;
import com.example.banvexe.repositories.RouteRepository;

import org.springframework.beans.factory.annotation.Autowired;


@Controller
public class UserViewController {

    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final TripService tripService;
    @Autowired    private RouteRepository routeRepository;

    public UserViewController(UserRepository userRepository, TicketRepository ticketRepository,
            TripService tripService) {
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
        this.tripService = tripService;
    }

    @GetMapping("/")
    public String homePage(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            String name = authentication.getName();
            // Mặc định avatar lấy theo tên nếu không có ảnh
            String avatar = "https://ui-avatars.com/api/?name=" + name;

            if (authentication.getPrincipal() instanceof OAuth2User oauth2User) {
                name = oauth2User.getAttribute("name");
                avatar = oauth2User.getAttribute("picture");
            }

            model.addAttribute("userName", name);
            model.addAttribute("userAvatar", avatar);
        }
        return "index";
    }

    @GetMapping("/profile")
    public String profilePage(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated())
            return "redirect:/login";

        String name = authentication.getName();
        String email = "chua-co-email@gmail.com";
        String avatar = "https://cdn-icons-png.flaticon.com/512/149/149071.png";

        if (authentication.getPrincipal() instanceof OAuth2User oauth2User) {
            name = oauth2User.getAttribute("name");
            email = oauth2User.getAttribute("email");
            avatar = oauth2User.getAttribute("picture");
        }

        model.addAttribute("userName", name);
        model.addAttribute("userEmail", email);
        model.addAttribute("userAvatar", avatar);
        return "profile";
    }

    @GetMapping("/ticket/{id}")
    public String ticketDetail(@PathVariable Long id, Authentication auth, Model model) {

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow();

        String username = auth.getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        if (!ticket.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Không có quyền truy cập");
        }

        model.addAttribute("ticket", ticket);
        return "ticket-detail";
    }

    @GetMapping("/history")
    public String historyPage(Authentication authentication, Model model) {
        // 1. Kiểm tra đăng nhập
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String identifier;

        // 2. Lấy định danh (Username hoặc Email)
        Object principal = authentication.getPrincipal();
        if (principal instanceof OAuth2User oAuth2User) {
            // Ưu tiên lấy email từ Google/Facebook
            identifier = oAuth2User.getAttribute("email");
        } else {
            identifier = authentication.getName();
        }

        // 3. Tìm User và lấy danh sách vé
        // Nên sử dụng một custom exception thay vì RuntimeException
        User user = userRepository.findByUsername(identifier)
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại trong hệ thống"));

        List<Ticket> tickets = ticketRepository.findByUser(user);

        for (Ticket t : tickets) {
            if (t.getTotalAmount() == null && t.getTrip() != null && t.getSeats() != null) {
                int seatCount = t.getSeats().split(",").length;
                double total = t.getTrip().getPricePerTicket() * seatCount;
                t.setTotalAmount(total);
            }
        }

        // 4. Đưa dữ liệu ra View
        model.addAttribute("tickets", tickets);
        model.addAttribute("user", user); // Truyền nguyên object để bên Thymeleaf lấy được nhiều thông tin hơn

        return "myticket";
    }

    @GetMapping("/api/trips/search/{from}/{to}/{date}")
    public List<Trip> searchTrips(
            @PathVariable String from,
            @PathVariable String to,
            @PathVariable String date) {
        return tripService.searchTrips(from, to, date);
    }
}