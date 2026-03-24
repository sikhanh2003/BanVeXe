package com.example.banvexe.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Ánh xạ các file trong static/js ra đường dẫn /js/
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");
    }
}