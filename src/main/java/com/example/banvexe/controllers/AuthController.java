package com.example.banvexe.controllers;

import com.example.banvexe.models.dto.LoginRequest;
import com.example.banvexe.models.dto.RegisterDTO;
import com.example.banvexe.repositories.UserRepository;
import com.example.banvexe.services.AuthService;
import com.example.banvexe.utils.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    // ============================================================
    // 1. CÁC API TRẢ VỀ JSON/JWT (Dành cho Mobile App / Postman)
    // ============================================================

    @PostMapping("/api/auth/login")
    @ResponseBody
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Xác thực từ username/password gửi lên
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // Lưu vào Context (tùy chọn, vì mình dùng Stateless)
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Tạo Token "thần thánh"
            String jwt = jwtUtils.generateToken(authentication.getName());

            // Trả về chuỗi JWT cho Client
            return ResponseEntity.ok(Map.of(
                    "token", jwt,
                    "type", "Bearer",
                    "username", authentication.getName()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Sai tài khoản hoặc mật khẩu!"));
        }
    }

    @PostMapping("/api/auth/register")
    @ResponseBody
    public ResponseEntity<?> handleRegisterApi(@Valid @RequestBody RegisterDTO dto) {
        try {
            authService.register(dto);
            return ResponseEntity.ok(Map.of("message", "Đăng ký thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/api/auth/me")
    @ResponseBody
    public ResponseEntity<?> getCurrentUser(Principal principal) {
        if (principal == null)
            return ResponseEntity.status(401).body("Chưa đăng nhập");
            
        return userRepository.findByUsername(principal.getName())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).build());
    }

    // ============================================================
    // 2. CÁC ROUTE TRẢ VỀ HTML (Dành cho giao diện Web/Thymeleaf)
    // ============================================================

    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // Trả về file login.html
    }

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register"; // Trả về file register.html
    }

    @PostMapping("/register")
    public String handleRegister(@Valid @ModelAttribute RegisterDTO dto, Model model) {
        try {
            authService.register(dto);
            return "redirect:/login?success=true";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            return "register";
        }
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam(value = "token", required = false) String token, Model model) {
        model.addAttribute("token", token);
        return "reset-password";
    }
}