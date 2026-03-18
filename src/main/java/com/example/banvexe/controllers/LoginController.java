package com.example.banvexe.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/myticket")
    public String myticket() {
        return "myticket";
    }

    @GetMapping("/booking")
    public String booking() {
        // Spring sẽ tự tìm file src/main/resources/templates/booking.html
        return "booking";
    }

    @GetMapping("/payment")
    public String payment() {
        return "payment";
    }

}