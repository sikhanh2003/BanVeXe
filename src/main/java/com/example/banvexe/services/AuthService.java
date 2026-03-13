package com.example.banvexe.services;

import com.example.banvexe.models.dto.RegisterDTO;
import com.example.banvexe.models.entities.User;
import com.example.banvexe.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Sửa hàm này để nhận RegisterDTO thay vì User
    public String register(RegisterDTO dto) {
        // 1. Kiểm tra trùng tên đăng nhập
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            return "Lỗi: Username đã tồn tại!";
        }

        // 2. Map dữ liệu từ DTO sang Entity User
        User user = new User();
        user.setUsername(dto.getUsername());
        // QUAN TRỌNG: Mã hóa mật khẩu trước khi lưu
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setRole(User.Role.CUSTOMER); // Mặc định đăng ký là khách hàng

        // 3. Lưu xuống MySQL
        userRepository.save(user);
        return "Đăng ký thành công cho: " + dto.getUsername();
    }

    public String login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        
        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) {
            return "Đăng nhập thành công! Chào " + userOpt.get().getFullName();
        }
        return "Sai tài khoản hoặc mật khẩu!";
    }
}