package com.example.banvexe.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    // ĐÃ XÓA login() và register() vì đã có ở AuthController

    @GetMapping("/booking")
    public String booking() {
        return "booking";
    }

    @GetMapping("/payment")
    public String payment() {
        return "payment";
    }

    @GetMapping("/myticket")
    public String myticket() {
        return "myticket";
    }
}