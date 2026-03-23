package com.example.banvexe.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        MvcRequestMatcher.Builder mvc = new MvcRequestMatcher.Builder(introspector);

        http
            .csrf(csrf -> csrf.disable()) // Tắt để test POST dễ dàng
            .authorizeHttpRequests(auth -> auth
                // 1. Tài nguyên tĩnh
                .requestMatchers(mvc.pattern("/css/**"), mvc.pattern("/js/**"), mvc.pattern("/images/**"), 
                                mvc.pattern("/static/**"), mvc.pattern("/webjars/**"), mvc.pattern("/favicon.ico")).permitAll()
                
                // 2. Các trang công khai & Auth (Thêm Quên mật khẩu vào đây)
                .requestMatchers(
                    mvc.pattern("/"), 
                    mvc.pattern("/login"), 
                    mvc.pattern("/register"), 
                    mvc.pattern("/forgot-password/**"), // Cho phép trang quên mật khẩu
                    mvc.pattern("/reset-password/**"),  // Cho phép trang đặt lại mật khẩu
                    mvc.pattern("/error"), 
                    mvc.pattern("/api/auth/**")
                ).permitAll()
                
                // 3. Các trang xem thông tin chuyến xe
                .requestMatchers(mvc.pattern("/api/routes/**"), mvc.pattern("/booking")).permitAll()
                
                // 4. Phân quyền Admin
                .requestMatchers(mvc.pattern("/admin/**"), mvc.pattern("/api/admin/**")).hasRole("ADMIN")
                
                // 5. Các trang cần login
                .requestMatchers(mvc.pattern("/payment/**"), mvc.pattern("/myticket/**")).authenticated()
                
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(customSuccessHandler()) 
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .successHandler(customSuccessHandler())
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .clearAuthentication(true)
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public HttpFirewall allowSemicolonHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowSemicolon(true);
        return firewall;
    }

    @Bean
    public AuthenticationSuccessHandler customSuccessHandler() {
        return (request, response, authentication) -> {
            var authorities = authentication.getAuthorities();
            for (var authority : authorities) {
                if (authority.getAuthority().equals("ROLE_ADMIN")) {
                    response.sendRedirect("/admin/dashboard");
                    return;
                }
            }
            response.sendRedirect("/");
        };
    }
}