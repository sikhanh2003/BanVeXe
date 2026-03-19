package com.example.banvexe.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminDashboardDTO {
    // Số liệu cơ bản
    private long totalRoutes;
    private long totalBuses;
    private long totalUsers;
    private long totalTrips;
    
    // Số liệu kinh doanh (Mới)
    private Double totalRevenue;
    private long totalTickets;
    private Double occupancyRate;
}