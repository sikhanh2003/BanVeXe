package com.example.banvexe.repositories;

import java.time.LocalDate;
import java.util.List;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.banvexe.models.entities.Trip;

public interface TripRepository extends JpaRepository<Trip, Long> {
    
    // Tìm các chuyến xe của một tuyến cụ thể (Có phân trang)
    Page<Trip> findByRouteId(Long routeId, Pageable pageable);

    // Tìm chuyến xe theo ngày (Có phân trang cho trang chủ nếu cần)
    List<Trip> findByRouteDepartureLocationAndRouteArrivalLocationAndDepartureTimeBetween(
            String departure,
            String arrival,
            LocalDateTime start,
            LocalDateTime end);

    // Tìm chuyến xe theo ngày (Dùng cho trang chủ)
    @Query("SELECT t FROM Trip t WHERE CAST(t.departureTime AS LocalDate) = :date")
    Page<Trip> findByDate(@Param("date") LocalDate date, Pageable pageable);

    // Lấy tất cả chuyến xe có phân trang (Dùng cho Admin)
    Page<Trip> findAll(Pageable pageable);

    // Hàm bổ trợ cho AdminService tính tỷ lệ lấp đầy
    @Query("SELECT SUM(b.capacity) FROM Trip t JOIN t.bus b")
    Integer getTotalSystemCapacity();
}

    @Query("SELECT r.id as id, r.departureLocation as departureLocation, " +
            "r.arrivalLocation as arrivalLocation, r.distanceKm as distanceKm, " +
            "MIN(t.pricePerTicket) as minPrice " +
            "FROM Route r LEFT JOIN Trip t ON r.id = t.route.id " +
            "GROUP BY r.id")
    List<Object[]> findRoutesWithMinPrice();
}
