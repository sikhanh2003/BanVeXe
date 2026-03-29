package com.example.banvexe.repositories;

import com.example.banvexe.models.dto.RouteDTO;
import com.example.banvexe.models.entities.Route;
import com.example.banvexe.models.entities.Trip;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface RouteRepository extends JpaRepository<Route, Long> {
    // 1. Tìm tuyến đường theo điểm đi và điểm đến chính xác (Dùng cho ô tìm kiếm)
    List<Route> findByDepartureLocationAndArrivalLocation(String departureLocation, String arrivalLocation);

    // 2. Tìm các tuyến đường có điểm đi cụ thể
    List<Route> findByDepartureLocation(String departureLocation);

    // 3. Lấy danh sách các tuyến phổ biến (Ví dụ: Tuyến có nhiều chuyến xe nhất
    // hoặc được đặt nhiều)
    // Nếu bạn chưa có trường 'isPopular', có thể lấy ngẫu nhiên hoặc lấy Top 6
    @Query("SELECT r FROM Route r")
    List<Route> findTopRoutes();

    @Query("SELECT r.id as id, r.departureLocation as departureLocation, " +
            "r.arrivalLocation as arrivalLocation, r.distanceKm as distanceKm, " +
            "MIN(t.pricePerTicket) as minPrice " +
            "FROM Route r LEFT JOIN Trip t ON t.route.id = r.id " +
            "GROUP BY r.id, r.departureLocation, r.arrivalLocation, r.distanceKm")
    List<RouteProjection> findAllRoutesWithMinPrice();

    // 4. Tìm kiếm gần đúng (Dùng cho chức năng gợi ý - Suggestion)
    List<Route> findByDepartureLocationContainingIgnoreCase(String departureLocation);

    @Query("SELECT DISTINCT r.departureLocation FROM Route r")
    List<String> findDistinctDepartureLocations();

    @Query("SELECT DISTINCT r.arrivalLocation FROM Route r")
    List<String> findDistinctArrivalLocations();

    @Query("SELECT t FROM Trip t WHERE " +
            "LOWER(t.route.departureLocation) LIKE LOWER(CONCAT('%', :from, '%')) AND " +
            "LOWER(t.route.arrivalLocation) LIKE LOWER(CONCAT('%', :to, '%')) AND " +
            "t.departureTime >= :start AND t.departureTime < :end")
    List<Trip> searchTrips(
            @Param("from") String from,
            @Param("to") String to,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}
