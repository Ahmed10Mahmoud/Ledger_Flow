package com.ahmed.demo.application.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken
) {
}
