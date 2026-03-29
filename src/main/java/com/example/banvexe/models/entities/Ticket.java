package com.example.banvexe.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
    @JsonIgnoreProperties({ "tickets" })
    private Trip trip;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({ "tickets" })
    private User user;

    private String seats;

    private LocalDateTime bookingTime;

    private LocalDateTime holdExpiresAt;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    private Double totalAmount;

    public enum TicketStatus {
        AVAILABLE, HOLD, BOOKED, PAID, CANCELLED
    }
}