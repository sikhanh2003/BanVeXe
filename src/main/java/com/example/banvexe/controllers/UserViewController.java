package com.example.banvexe.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.util.Map;

@Controller
public class UserViewController {

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
        if (authentication == null || !authentication.isAuthenticated()) return "redirect:/login";
        
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

    @GetMapping("/history")
    public String historyPage(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) return "redirect:/login";
        
        String name = authentication.getName();
        if (authentication.getPrincipal() instanceof OAuth2User oauth2User) name = oauth2User.getAttribute("name");
        
        model.addAttribute("userName", name);
        model.addAttribute("tickets", List.of(
            Map.of("id", "FUTA123", "departure", "TP. HCM", "destination", "Đà Lạt", "startTime", "22:00 20/03/2026", "price", 300000.0, "seatNumber", "A05")
        ));
        return "myticket"; 
    }
}