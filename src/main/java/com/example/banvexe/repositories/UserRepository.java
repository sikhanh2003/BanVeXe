package com.example.banvexe.repositories;

import com.example.banvexe.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Tìm người dùng bằng username để kiểm tra lúc đăng nhập
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    
    
}