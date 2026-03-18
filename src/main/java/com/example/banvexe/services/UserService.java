package com.example.banvexe.services;

import com.example.banvexe.models.entities.User;
import com.example.banvexe.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Lấy danh sách tất cả người dùng
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Tạo người dùng mới
    public User createUser(User user) {
        return userRepository.save(user);
    }

    // Xóa người dùng
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}