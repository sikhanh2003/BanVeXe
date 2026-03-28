package com.example.banvexe.models.dto;

public class LoginRequest {
    private String username;
    private String password;

    // Quan trọng: Phải có Constructor không đối số để Jackson giải mã JSON
    public LoginRequest() {
    }

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getter và Setter (Bắt buộc phải có để Spring lấy được dữ liệu)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}