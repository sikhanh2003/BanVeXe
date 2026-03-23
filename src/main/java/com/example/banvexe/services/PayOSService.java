package com.example.banvexe.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class PayOSService {

    @Value("${payos.clientId}")
    private String clientId;

    @Value("${payos.apiKey}")
    private String apiKey;

    public String createPaymentLink() {

        String url = "https://api-merchant.payos.vn/v2/payment-requests";

        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> body = new HashMap<>();
        body.put("orderCode", System.currentTimeMillis());
        body.put("amount", 50000);
        body.put("description", "Thanh toán vé xe");
        body.put("returnUrl", "http://localhost:8080/payment/success");
        body.put("cancelUrl", "http://localhost:8080/payment/cancel");

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-client-id", clientId);
        headers.set("x-api-key", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(url, request, Map.class);

        // lấy checkoutUrl
        Map data = (Map) response.getBody().get("data");
        return (String) data.get("checkoutUrl");
    }
}