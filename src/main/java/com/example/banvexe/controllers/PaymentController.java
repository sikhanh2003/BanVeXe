package com.example.banvexe.controllers;

import com.example.banvexe.services.PayOSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaymentController {

    @Autowired
    private PayOSService payOSService;

    @GetMapping("/login/payment")
    public String paymentPage() {
        return "payment";
    }

    @GetMapping("/checkout")
    public String checkout() {
        String url = payOSService.createPaymentLink();
        return "redirect:" + url;
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