package com.ahmed.demo.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;
import java.util.UUID;

@Configuration
public class AuditorConfig {
    @Bean
    public AuditorAware<UUID> auditorProvider() {
        return () -> {
            // 🔥 Replace later with Spring Security
            // For now, return dummy user or system user

            return Optional.of(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        };
    }
}
