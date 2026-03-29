package com.example.banvexe.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "ticket_id")
    @NotNull(message = "Ticket không được để trống")
    private Ticket ticket;

    @NotBlank(message = "Phương thức thanh toán không được để trống")
    @Size(max = 50, message = "Phương thức thanh toán tối đa 50 ký tự")
    private String paymentMethod;

    @NotNull(message = "Số tiền không được để trống")
    @Positive(message = "Số tiền phải lớn hơn 0")
    private Double amount;

    @NotNull(message = "Ngày giao dịch không được để trống")
    @PastOrPresent(message = "Ngày giao dịch không hợp lệ")
    private LocalDateTime transactionDate;

    @NotBlank(message = "Trạng thái thanh toán không được để trống")
    @Pattern(
        regexp = "PENDING|SUCCESS|FAILED",
        message = "Trạng thái phải là PENDING, SUCCESS hoặc FAILED"
    )
    private String paymentStatus;
}