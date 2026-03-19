package com.example.banvexe.services;

import com.example.banvexe.models.dto.AdminDashboardDTO;
import com.example.banvexe.models.entities.Trip;
import com.example.banvexe.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AdminService {

    @Autowired
    private RouteRepository routeRepository;
    @Autowired
    private BusRepository busRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TripRepository tripRepository;
    @Autowired
    private TicketRepository ticketRepository;

    public AdminDashboardDTO getDashboardStats() {
        AdminDashboardDTO dto = new AdminDashboardDTO();

        // 1. Đếm tổng số lượng cơ bản
        dto.setTotalRoutes(routeRepository.count());
        dto.setTotalBuses(busRepository.count());
        dto.setTotalUsers(userRepository.count());
        dto.setTotalTrips(tripRepository.count());

        // 2. Thống kê vé
        long totalTickets = ticketRepository.count();
        dto.setTotalTickets(totalTickets);

        // 3. Tính tổng doanh thu
        Double revenue = ticketRepository.getTotalRevenue();
        dto.setTotalRevenue(revenue != null ? revenue : 0.0);

        // 4. Tính tỷ lệ lấp đầy (%)
        // Logic: (Tổng ghế đã đặt / Tổng số ghế thiết kế của các chuyến xe) * 100
        dto.setOccupancyRate(calculateOccupancyRate());

        return dto;
    }

    private double calculateOccupancyRate() {
        // Đếm số vé đã bán (PAID hoặc BOOKED tùy bạn quy định)
        long soldTickets = ticketRepository.count();

        // Lấy tổng số chỗ của tất cả chuyến xe từ DB
        Integer totalCapacity = tripRepository.getTotalSystemCapacity();

        if (totalCapacity == null || totalCapacity == 0) return 0.0;

        double rate = (double) soldTickets / totalCapacity * 100;
        return Math.round(rate * 100.0) / 100.0; // Trả về dạng 85.55
    }
}