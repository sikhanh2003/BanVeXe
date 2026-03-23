package com.example.banvexe.repositories;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.banvexe.models.entities.Trip;

import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {
    // Tìm các chuyến xe của một tuyến cụ thể
    List<Trip> findByRouteId(Long routeId);

    List<Trip> findByRouteDepartureLocationAndRouteArrivalLocationAndDepartureTimeBetween(
        String departure, 
        String arrival, 
        LocalDateTime start, 
        LocalDateTime end
    );

    // Tìm chuyến xe theo ngày (Dùng cho trang chủ)
    @Query("SELECT t FROM Trip t WHERE CAST(t.departureTime AS LocalDate) = :date")
    List<Trip> findByDate(@Param("date") LocalDate date);

    // Hàm bổ trợ cho AdminService tính tỷ lệ lấp đầy
    @Query("SELECT SUM(b.capacity) FROM Trip t JOIN t.bus b")
    Integer getTotalSystemCapacity();

}
