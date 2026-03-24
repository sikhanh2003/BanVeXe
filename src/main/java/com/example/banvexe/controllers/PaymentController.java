package com.example.banvexe.controllers;

import com.example.banvexe.services.PayOSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@Controller
public class PaymentController {

    @Autowired
    private PayOSService payOSService;

    @GetMapping("/login/payment")
    public String paymentPage() {
        return "payment";
    }

    @PostMapping("/api/payment/create")
    @ResponseBody
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> request) {
        try {
            String paymentMethod = (String) request.get("paymentMethod");
            // Chuyển đổi amount an toàn hơn
            Long amount = Long.valueOf(request.get("amount").toString());
            String seats = (String) request.get("seats");

            if ("OFFLINE".equals(paymentMethod)) {
                // TODO: Gọi Service lưu vào Database với trạng thái "Chờ thanh toán"
                return ResponseEntity.ok(Map.of("message", "Đặt vé thành công, vui lòng thanh toán tại quầy!"));
            } else {
                // Thanh toán qua PayOS
                String url = payOSService.createPaymentLink(amount.intValue(), "Ve xe ghe: " + seats);
                return ResponseEntity.ok(Map.of("checkoutUrl", url));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Lỗi: " + e.getMessage()));
        }
    }

    @PostMapping("/api/payment/pay-later")
    @ResponseBody
    public ResponseEntity<?> payLater(@RequestBody Map<String, Object> request) {
        try {
            // Lấy dữ liệu từ frontend gửi lên
            String seats = (String) request.get("seats");
            Object amountObj = request.get("amount");

            // Log ra console để kiểm tra dữ liệu đã sang tới chưa
            System.out.println("Đang xử lý đặt vé tại quầy cho ghế: " + seats);
            System.out.println("Số tiền: " + amountObj);

            // TODO: Tại đây bạn hãy gọi TicketService để lưu thông tin vé vào Database
            // Ví dụ: ticketService.saveBooking(seats, amount, "PENDING");

            Map<String, String> response = new HashMap<>();
            response.put("message", "Đã ghi nhận đơn hàng thành công!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi server: " + e.getMessage());
        }
    }

    @GetMapping("/payment/success")
    public String success() {
        return "success";
    }

    @GetMapping("/payment/cancel")
    public String cancel() {
        return "cancel";
    }
}