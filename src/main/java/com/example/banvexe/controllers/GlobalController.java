package com.example.banvexe.controllers;

import com.example.banvexe.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;


@ControllerAdvice
public class GlobalController {

    @Autowired
    private UserRepository userRepository;

    @ModelAttribute
    public void addUserToModel(Model model, Principal principal) {
        if (principal != null) {
            String username = principal.getName();

            model.addAttribute("userName", username);
            model.addAttribute("userAvatar",
                "https://ui-avatars.com/api/?name=" + username);
        }
    }
}