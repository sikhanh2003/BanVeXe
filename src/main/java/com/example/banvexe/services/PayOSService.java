package com.example.banvexe.services;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class PayOSService {
    // Thông tin cấu hình từ PayOS
    private final String clientId = "846e0e16-964f-4ebd-8538-ffc3007c052f";
    private final String apiKey = "206daf4b-9bdb-4976-af92-72f0e46515e1";
    private final String checksumKey = "849676898fff1f52d47315c251f421860b39dbcb4a89c02c0f4dbb6f850a2bf8";

    public String createPaymentLink(int amount, String description) {
        String url = "https://api-merchant.payos.vn/v2/payment-requests";
        RestTemplate restTemplate = new RestTemplate();

        // Tạo body request
        Map<String, Object> body = new HashMap<>();
        body.put("orderCode", System.currentTimeMillis() / 1000);
        body.put("amount", amount);
        body.put("description", description);
        body.put("returnUrl", "http://localhost:8080/payment/success");
        body.put("cancelUrl", "http://localhost:8080/payment/cancel");

        // Header chứa mã xác thực
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-client-id", clientId);
        headers.set("x-api-key", apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            Map<String, Object> resBody = response.getBody();
            Map<String, Object> data = (Map<String, Object>) resBody.get("data");
            return data.get("checkoutUrl").toString();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}