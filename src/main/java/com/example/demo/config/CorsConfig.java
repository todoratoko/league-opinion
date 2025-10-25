package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class CorsConfig {

    @Value("${frontend.url}")
    private String frontendUrl;

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Allow credentials (cookies, authorization headers)
        config.setAllowCredentials(true);

        // Build allowed origins list dynamically
        List<String> allowedOrigins = new ArrayList<>();
        allowedOrigins.add("http://localhost:3000");  // Local React development
        allowedOrigins.add("http://localhost:5173");  // Local Vite development

        // Add production frontend URL from environment variable
        if (frontendUrl != null && !frontendUrl.startsWith("http://localhost")) {
            allowedOrigins.add(frontendUrl);
        }

        config.setAllowedOrigins(allowedOrigins);

        // Allow these HTTP methods
        config.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));

        // Allow these headers
        config.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "Accept",
            "Origin",
            "X-Requested-With"
        ));

        // Expose these headers to the frontend
        config.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type"
        ));

        // Cache preflight requests for 1 hour
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
