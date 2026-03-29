package com.example.banvexe.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "buses")
@Data
@NoArgsConstructor
public class Bus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Biển số xe không được để trống")
    @Size(min = 4, max = 20, message = "Biển số xe phải từ 4 đến 20 ký tự")
    @Column(unique = true) // Đảm bảo không trùng biển số xe trong DB
    private String busNumber;

    @NotBlank(message = "Loại xe không được để trống")
    private String busType; // Ví dụ: Giường nằm, Ghế ngồi

    @NotNull(message = "Sức chứa không được để trống")
    @Min(value = 4, message = "Sức chứa tối thiểu là 4 chỗ")
    @Max(value = 100, message = "Sức chứa tối đa là 100 chỗ")
    private Integer capacity;

    @OneToMany(mappedBy = "bus")
    @JsonIgnore
    private List<Trip> trips;
}