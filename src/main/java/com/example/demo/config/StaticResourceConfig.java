package com.example.demo.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve files from the uploads directory
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/")
                .setCachePeriod(3600); // Cache for 1 hour

        // Serve team logos and player photos from static/images
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/")
                .setCachePeriod(86400); // Cache for 24 hours

        // Serve league logos
        registry.addResourceHandler("/images/leagues/**")
                .addResourceLocations("classpath:/static/images/leagues/")
                .setCachePeriod(86400); // Cache for 24 hours

        // Serve tournament logos
        registry.addResourceHandler("/images/tournaments/**")
                .addResourceLocations("classpath:/static/images/tournaments/")
                .setCachePeriod(86400); // Cache for 24 hours
    }

    @PostConstruct
    public void init() {
        try {
            Path uploadPath = Paths.get("uploads");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                System.out.println("Created uploads directory at: " + uploadPath.toAbsolutePath());
            } else {
                System.out.println("Uploads directory already exists at: " + uploadPath.toAbsolutePath());
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }
}
