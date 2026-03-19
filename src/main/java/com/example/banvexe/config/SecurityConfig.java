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
                        // Công khai: Static files, trang chủ, đăng nhập, đăng ký
                        .requestMatchers("/", "/login/**", "/register/**", "/css/**", "/js/**", "/images/**", "/error")
                        .permitAll()
                        .requestMatchers("/api/auth/**").permitAll() // API auth thì cho phép mọi người truy cập (để
                                                                     // đăng nhập/đăng ký)
                        .requestMatchers("/api/routes/**").permitAll()
                        .requestMatchers("/api/buses/**").permitAll()
                        .requestMatchers("/api/trips/**").permitAll()

                        .requestMatchers("/api/admin/**").hasRole("ADMIN") // Chỉ ADMIN mới được truy cập API admin
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Phân quyền: Chỉ ADMIN mới được vào các link quản trị
                        // (Thay .permitAll() cũ bằng .hasRole("ADMIN"))
                        .requestMatchers("/admin/**", "/api/admin/**").hasRole("ADMIN")

                        // Mọi request khác (bao gồm đặt vé của User) phải đăng nhập
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

            // Kiểm tra quyền của người dùng
            for (var authority : authorities) {
                String role = authority.getAuthority();

                // Lưu ý: Spring Security thường thêm tiền tố "ROLE_" tự động
                if (role.equals("ROLE_ADMIN") || role.equals("ADMIN")) {
                    response.sendRedirect("/admin/dashboard");
                    return;
                }
            }

            // Nếu không phải Admin thì mặc định về trang chủ cho Customer
            response.sendRedirect("/");
        };
    }
}