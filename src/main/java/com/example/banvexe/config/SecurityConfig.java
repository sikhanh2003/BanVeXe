package com.example.banvexe.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. Bảo mật cơ bản
                .csrf(csrf -> csrf.disable()) // Tắt CSRF nếu đang phát triển API/Test

                // 2. Cấu hình phân quyền (Authorize Requests)
                .authorizeHttpRequests(auth -> auth
                        // Mở cổng cho API login/register và các file giao diện
                        .requestMatchers("/", "/login", "/register", "/css/**", "/js/**", "/api/auth/**").permitAll()

                        // API Admin phải có quyền ADMIN
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Trang Admin (HTML) phải có quyền ADMIN
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        .anyRequest().authenticated())

                // 3. Cấu hình Form Login (Dành cho tài khoản hệ thống)
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(customSuccessHandler()) // Phân luồng sau khi login thành công
                        .permitAll())

                // 4. Cấu hình OAuth2 Login (Google)
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .successHandler(customSuccessHandler()) // Dùng chung phân luồng với Form Login
                )

                // 5. Cấu hình Logout
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll());

        return http.build();
    }

    /**
     * Bộ điều hướng thông minh sau khi đăng nhập thành công.
     * Nếu là ADMIN -> Vào Dashboard quản trị.
     * Nếu là USER (CUSTOMER) -> Về trang chủ mua vé.
     */
    @Bean
    public AuthenticationSuccessHandler customSuccessHandler() {
        return (request, response, authentication) -> {
            var authorities = authentication.getAuthorities();

            for (var authority : authorities) {
                String role = authority.getAuthority();
                // Spring Security mặc định thêm ROLE_ vào trước tên Role
                if (role.equals("ROLE_ADMIN")) {
                    response.sendRedirect("/admin/dashboard");
                    return;
                }
            }
            response.sendRedirect("/");
        };
    }
}