package com.example.banvexe.config;

import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class VNPayConfig {
    public static String vnp_PayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    public static String vnp_ReturnUrl = "http://localhost:8080/payment-callback"; // Trang hứng kết quả
    public static String vnp_TmnCode = "2QXUI4J4"; // Thay bằng mã của Kỳ
    public static String vnp_HashSecret = "NDS8S6DR8G2L4P9E6Y7U1W2I3O4P5A6S"; // Thay bằng secret của Kỳ
    public static String vnp_Version = "2.1.0";
    public static String vnp_Command = "pay";

    public static String hmacSHA512(final String key, final String data) {
        try {
            if (key == null || data == null) throw new NullPointerException();
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    public static String getIpAddress(HttpServletRequest request) {
        String ipAdress;
        try {
            ipAdress = request.getHeader("X-FORWARDED-FOR");
            if (ipAdress == null) ipAdress = request.getRemoteAddr();
        } catch (Exception e) {
            ipAdress = "Invalid IP:" + e.getMessage();
        }
        return ipAdress;
    }
}