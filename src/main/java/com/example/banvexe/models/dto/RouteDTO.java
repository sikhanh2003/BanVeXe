package com.example.banvexe.models.dto;
import lombok.Data;

@Data
public class RouteDTO {
    private Long id;
    private String departureLocation;
    private String arrivalLocation;
    private Double distanceKm;
    private Double minPrice; // Trường này để lưu giá từ bảng Trip

    // Tạo Constructor có đầy đủ tham số
    public RouteDTO(Long id, String departureLocation, String arrivalLocation, Double distanceKm, Double minPrice) {
        this.id = id;
        this.departureLocation = departureLocation;
        this.arrivalLocation = arrivalLocation;
        this.distanceKm = distanceKm;
        this.minPrice = minPrice;
    }
    // Getter/Setter...
}
