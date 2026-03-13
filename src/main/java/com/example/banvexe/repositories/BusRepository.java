package com.example.banvexe.repositories;

import com.example.banvexe.models.entities.Bus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BusRepository extends JpaRepository<Bus, Long> {
    // Tìm danh sách xe theo số lượng chỗ ngồi (VD: tìm xe 45 chỗ)
    List<Bus> findByCapacity(Integer capacity);
}