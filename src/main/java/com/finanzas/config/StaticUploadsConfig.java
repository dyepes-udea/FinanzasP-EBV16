package com.finanzas.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

@Configuration
public class StaticUploadsConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadsPath = Path.of("src", "main", "resources", "static", "uploads").toAbsolutePath().toUri().toString();
        if (!uploadsPath.endsWith("/")) {
            uploadsPath = uploadsPath + "/";
        }

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadsPath);
    }
}
