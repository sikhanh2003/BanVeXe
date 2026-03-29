package com.example.banvexe.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "routes")
@Data
@NoArgsConstructor
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Điểm đi không được để trống")
    @Size(max = 100, message = "Điểm đi tối đa 100 ký tự")
    private String departureLocation;

    @NotBlank(message = "Điểm đến không được để trống")
    @Size(max = 100, message = "Điểm đến tối đa 100 ký tự")
    private String arrivalLocation;

    @NotNull(message = "Khoảng cách không được để trống")
    @Positive(message = "Khoảng cách phải lớn hơn 0")
    @Max(value = 10000, message = "Khoảng cách không hợp lệ (quá lớn)")
    private Double distanceKm;
}