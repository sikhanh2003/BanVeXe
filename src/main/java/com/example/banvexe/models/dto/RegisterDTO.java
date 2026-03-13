package com.example.banvexe.models.dto;

import lombok.Data;

@Data
public class RegisterDTO {
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phone;
}