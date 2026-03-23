package com.example.banvexe.services;

import com.example.banvexe.models.dto.RegisterDTO;
import com.example.banvexe.models.entities.User;
import com.example.banvexe.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Sửa lại để ném ra Exception nếu có lỗi. 
     * Điều này giúp Controller biết khi nào nên hiện lỗi, khi nào nên chuyển trang.
     */
    public void register(RegisterDTO dto) throws Exception {
        // 1. Kiểm tra trùng tên đăng nhập
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new Exception("Tên đăng nhập đã tồn tại!");
        }
        
        // 2. Kiểm tra trùng Email (nên có để đảm bảo tính duy nhất)
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new Exception("Email này đã được sử dụng!");
        }

        // 3. Map dữ liệu từ DTO sang Entity User
        User user = new User();
        user.setUsername(dto.getUsername());
        
        // MÃ HÓA MẬT KHẨU: Cực kỳ quan trọng để Spring Security nhận diện khi đăng nhập
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        
        // Gán Role mặc định (Đảm bảo Enum Role của bạn có giá trị CUSTOMER)
        user.setRole(User.Role.CUSTOMER); 

        // 4. LƯU XUỐNG MySQL (Lệnh này sẽ ghi dữ liệu vào phpMyAdmin)
        userRepository.save(user);
    }

    /**
     * Lưu ý: Khi dùng Spring Security Form Login, hàm login này 
     * thường không được gọi thủ công từ Controller nữa.
     */
    public Map<String, Object> login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) {
            User user = userOpt.get();
            return Map.of(
                    "status", "success",
                    "role", user.getRole().name(),
                    "username", user.getUsername(),
                    "fullName", user.getFullName());
        }

        return Map.of("status", "error", "message", "Sai tài khoản hoặc mật khẩu");
    }
}