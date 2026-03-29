package com.example.banvexe.models.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterDTO {

    @NotBlank(message = "Username không được để trống")
    @Size(min = 4, max = 50, message = "Username từ 4-50 ký tự")
    private String username;

    @NotBlank(message = "Password không được để trống")
    @Size(min = 6, message = "Password phải >= 6 ký tự")
    private String password;

    @Size(max = 100, message = "Tên không quá 100 ký tự")
    private String fullName;

    @Email(message = "Email không hợp lệ")
    private String email;

    @Pattern(
        regexp = "^(0|\\+84)[0-9]{9}$",
        message = "SĐT không hợp lệ"
    )
    private String phone;
}