package com.example.banvexe.controllers;

import com.example.banvexe.models.dto.RegisterDTO;
import com.example.banvexe.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    // --- ĐĂNG NHẬP & ĐĂNG KÝ ---

    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; 
    }

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register"; 
    }

    @PostMapping("/register")
    public String handleRegister(@ModelAttribute RegisterDTO dto, Model model) {
        try {
            authService.register(dto);
            return "redirect:/login?success=true";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            return "register";
        }
    }

    // --- QUÊN MẬT KHẨU (PHẦN MỚI THÊM) ---

    // 1. Hiển thị trang nhập Email để nhận link reset
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password"; // Trả về file forgot-password.html
    }

    // 2. Hiển thị trang đặt lại mật khẩu mới (Reset Password)
    // Thường link từ Email sẽ có dạng: /reset-password?token=xyz
    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam(value = "token", required = false) String token, Model model) {
        model.addAttribute("token", token);
        return "reset-password"; // Trả về file reset-password.html
    }
}