package com.example.banvexe.config;

import com.example.banvexe.models.entities.User; // Đảm bảo đúng path tới model User của bạn
import com.example.banvexe.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DataInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            String adminEmail = "phanthuky12@gmail.com";
            
            // Kiểm tra xem admin đã tồn tại chưa để tránh trùng lặp khi restart app
            if (userRepository.findByUsername(adminEmail).isEmpty()) {
                User admin = new User();
                admin.setUsername(adminEmail);
                admin.setEmail(adminEmail);
                // Mã hóa mật khẩu giống hệt email như bạn yêu cầu
                admin.setPassword(passwordEncoder.encode(adminEmail)); 
                
                // Giả sử logic Role của bạn lưu bằng String hoặc Set<String>
                // Bạn hãy điều chỉnh setRole/setRoles tùy theo cấu trúc model User của bạn
                admin.setRole("ADMIN"); 
                
                userRepository.save(admin);
                System.out.println(">>> Đã khởi tạo tài khoản Admin mặc định: " + adminEmail);
            }
        };
    }
}