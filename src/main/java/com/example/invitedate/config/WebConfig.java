package com.example.invitedate.config;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${app.cors.allowed-origins}") private String allowedOrigins;
    @Override public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**").allowedOrigins(Arrays.stream(allowedOrigins.split(",")).map(String::trim).toArray(String[]::new))
                .allowedMethods("GET", "POST", "OPTIONS").allowedHeaders("Content-Type", "Authorization").maxAge(3600);
    }
}
