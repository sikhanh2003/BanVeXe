package com.example.banvexe.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "trips")
@Data
@NoArgsConstructor
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bus_id")
    @NotNull(message = "Bus không được để trống")
    private Bus bus;

    @ManyToOne
    @JoinColumn(name = "route_id")
    @NotNull(message = "Route không được để trống")
    private Route route;

    @NotNull(message = "Thời gian khởi hành không được để trống")
    @Future(message = "Thời gian khởi hành phải ở tương lai")
    private LocalDateTime departureTime;

    @NotNull(message = "Giá vé không được để trống")
    @Positive(message = "Giá vé phải lớn hơn 0")
    @Max(value = 10000000, message = "Giá vé không hợp lệ")
    @Column(name = "price_per_ticket")
    private Double pricePerTicket;

    @NotNull(message = "Số ghế không được để trống")
    @Min(value = 0, message = "Số ghế không được âm")
    @Max(value = 100, message = "Số ghế vượt quá giới hạn xe")
    private Integer availableSeats;

    @OneToMany(mappedBy = "trip")
    @JsonIgnore
    private List<Ticket> tickets;

    // ✅ Validate logic nâng cao
    @AssertTrue(message = "Số ghế không được vượt quá sức chứa của xe")
    public boolean isSeatValid() {
        if (bus == null || availableSeats == null) return true;

        // giả sử Bus có field capacity
        return availableSeats <= bus.getCapacity();
    }
}