package com.ahmed.demo.application.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.sql.Date;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static io.jsonwebtoken.Jwts.claims;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    public String generateToken(Map<String, Object> claims, String subject, Duration ttl){
        Instant now = Instant.now();
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(ttl)))
                .signWith(privateKey,Jwts.SIG.RS256)
                .compact();
    }
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token);

            return true;

        } catch (Exception e) {
            return false;
        }
    }
    public UUID extractUserId(String token) {
        String sub = claims(token).getPayload().getSubject();
        return UUID.fromString(sub);
    }
    public String extractRole(String token) {
        return claims(token).getPayload().get("role", String.class);
    }

    private Jws<Claims> claims(String token) {
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token);
    }
}
