package com.example.banvexe.models.dto;

import lombok.Data;
import java.util.List;

@Data
public class BookingRequest {

    private Long tripId;

    // ✅ FIX: nhận nhiều ghế đúng kiểu frontend
    private List<String> seatNumbers;

    private String customerName;
    private Double totalAmount;

}