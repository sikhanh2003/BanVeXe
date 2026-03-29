package com.example.banvexe.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import com.example.banvexe.config.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // Sửa tên Constructor cho đúng với tên Class
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector)
            throws Exception {
        MvcRequestMatcher.Builder mvc = new MvcRequestMatcher.Builder(introspector);

        http
            .csrf(csrf -> csrf.disable()) 
            .authorizeHttpRequests(auth -> auth
                // 1. Tài nguyên tĩnh
                .requestMatchers(mvc.pattern("/css/**"), mvc.pattern("/js/**"), mvc.pattern("/images/**"), 
                                mvc.pattern("/static/**"), mvc.pattern("/webjars/**"), mvc.pattern("/favicon.ico")).permitAll()
                
                // 2. Các trang công khai & Auth
                .requestMatchers(
                    mvc.pattern("/"), 
                    mvc.pattern("/login"), 
                    mvc.pattern("/register"), 
                    mvc.pattern("/forgot-password/**"),
                    mvc.pattern("/reset-password/**"),
                    mvc.pattern("/error"), 
                    mvc.pattern("/api/auth/**")
                ).permitAll()
                
                // 3. Cho phép truy cập công khai để TEST PHÂN TRANG (Quan trọng)
                .requestMatchers(
                    mvc.pattern("/admin/**"),     // Cho phép vào trang giao diện admin
                    mvc.pattern("/api/admin/**"), // Cho phép các API admin
                    mvc.pattern("/api/trips/**"), // Cho phép API lấy dữ liệu chuyến xe
                    mvc.pattern("/api/routes/**"),
                    mvc.pattern("/api/buses/**"),
                    mvc.pattern("/booking")
                ).permitAll() 
                
                // 4. Các trang cần login thực sự
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
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Các tài nguyên tĩnh và Auth API cho phép truy cập tự do
                        .requestMatchers(mvc.pattern("/login"), mvc.pattern("/register"), mvc.pattern("/api/auth/**"))
                        .permitAll()
                        .requestMatchers(mvc.pattern("/css/**"), mvc.pattern("/js/**"), mvc.pattern("/images/**"))
                        .permitAll()

                        // Cho phép xem lịch trình, vé công khai
                        .requestMatchers(mvc.pattern("/api/tickets/**"), mvc.pattern("/api/buses/**"),
                                mvc.pattern("/api/routes/**"))
                        .permitAll()
                        .requestMatchers(mvc.pattern("/")).permitAll()

                        // Phân quyền Admin
                        .requestMatchers(mvc.pattern("/admin/**")).hasRole("ADMIN")

                        // Các yêu cầu khác phải xác thực (bằng Form hoặc bằng JWT)
                        .anyRequest().authenticated())

                // THÊM DÒNG NÀY: Chèn bộ lọc JWT vào trước bộ lọc mặc định của Spring
                .addFilterBefore(jwtAuthenticationFilter,
                        org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)

                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(customSuccessHandler())
                        .failureUrl("/login?error=true")
                        .permitAll())
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .successHandler(customSuccessHandler()))
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/?logout=true")
                        .deleteCookies("JSESSIONID")
                        .permitAll());
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public HttpFirewall allowSemicolonHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowSemicolon(true);
        return firewall;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
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