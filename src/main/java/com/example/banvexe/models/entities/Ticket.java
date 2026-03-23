package com.example.banvexe.models.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@Data
@NoArgsConstructor
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Integer seatNumber;
    private LocalDateTime bookingTime;

    // 1. Thêm field này để lưu thời gian hết hạn giữ chỗ
    private LocalDateTime holdExpiresAt; 

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    // 2. Cập nhật Enum để có đủ các trạng thái logic trong Service
    public enum TicketStatus {
        AVAILABLE, HOLD, BOOKED, PAID, CANCELLED
    }
}