package com.example.banvexe.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Username không được để trống")
    @Size(min = 4, max = 50, message = "Username từ 4 đến 50 ký tự")
    private String username;

    @Column(nullable = false)
    @NotBlank(message = "Password không được để trống")
    @Size(min = 6, message = "Password phải ít nhất 6 ký tự")
    private String password;

    @Size(max = 100, message = "Tên không được quá 100 ký tự")
    private String fullName;

    @Email(message = "Email không hợp lệ")
    @Size(max = 100, message = "Email không quá 100 ký tự")
    private String email;

    @Pattern(
        regexp = "^(0|\\+84)[0-9]{9}$",
        message = "Số điện thoại không hợp lệ (VN)"
    )
    private String phone;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Role không được để trống")
    private Role role;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Ticket> tickets;

    public enum Role {
        CUSTOMER, ADMIN
    }

    public User() {}

        public Long getId() {
        return id;
    }

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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}