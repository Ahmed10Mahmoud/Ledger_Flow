package com.ahmed.demo.application.security;

import com.ahmed.demo.application.dto.AuthResponse;
import com.ahmed.demo.application.dto.LoginRequest;
import com.ahmed.demo.application.dto.RegisterRequest;
import com.ahmed.demo.infrastructure.persistence.RefreshTokenEntity;
import com.ahmed.demo.infrastructure.persistence.RefreshTokenRepository;
import com.ahmed.demo.infrastructure.persistence.UserEntity;
import com.ahmed.demo.infrastructure.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshRepo;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public void register(RegisterRequest req) {

        UserEntity user = UserEntity.builder()
                .email(req.email())
                .passwordHash(encoder.encode(req.password()))
                .role("USER")
                .build();

        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest req) {

        UserEntity user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!encoder.matches(req.password(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        String accessToken = jwtService.generateToken(
                Map.of("role", user.getRole()),
                user.getId().toString(),
                Duration.ofMinutes(15)
        );

        String refreshToken = UUID.randomUUID().toString();

        refreshRepo.save(
                RefreshTokenEntity.builder()
                        .token(refreshToken)
                        .userId(user.getId())
                        .expiresAt(Instant.now().plus(Duration.ofDays(7)))
                        .revoked(false)
                        .build()
        );

        return new AuthResponse(accessToken, refreshToken);
    }
    public AuthResponse refresh(String refreshToken) {

        RefreshTokenEntity token = refreshRepo
                .findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (token.isRevoked() || token.getExpiresAt().isBefore(Instant.now())) {
            throw new RuntimeException("Expired token");
        }

        UserEntity user = userRepository.findById(token.getUserId())
                .orElseThrow();

        String newAccessToken = jwtService.generateToken(
                Map.of("role", user.getRole()),
                user.getId().toString(),
                Duration.ofMinutes(15)
        );

        return new AuthResponse(newAccessToken, refreshToken);
    }

    public void logout(String refreshToken) {

        RefreshTokenEntity token = refreshRepo
                .findByToken(refreshToken)
                .orElseThrow();

        token.setRevoked(true);

        refreshRepo.save(token);
    }

}