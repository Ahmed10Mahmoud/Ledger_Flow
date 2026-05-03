package com.ahmed.demo.api.filter;

import com.ahmed.demo.application.service.IdempotencyService;
import com.ahmed.demo.domain.model.IdempotencyStatus;
import com.ahmed.demo.infrastructure.persistence.IdempotencyKey;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class IdempotencyFilter extends OncePerRequestFilter {
    private final IdempotencyService service;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String key = request.getHeader("X-Idempotency-Key");

        if (key == null || key.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        UUID userId = getUserId(); // 🔥 from security later

        var existing = service.check(userId, key);

        // ✅ 1. If COMPLETED → return cached response
        if (existing.isPresent() &&
                existing.get().getStatus() == IdempotencyStatus.COMPLETED) {

            response.setStatus(existing.get().getHttpStatus());
            response.getWriter().write(existing.get().getResponseBody());
            return;
        }

        // ✅ 2. If PROCESSING → reject duplicate in-flight
        if (existing.isPresent() &&
                existing.get().getStatus() == IdempotencyStatus.PROCESSING) {

            response.setStatus(409);
            response.getWriter().write("Request already in progress");
            return;
        }

        // ✅ 3. Start processing
        IdempotencyKey record = service.startProcessing(userId, key);

        try {
            // wrap response
            CachedBodyHttpServletResponse wrapped =
                    new CachedBodyHttpServletResponse(response);

            filterChain.doFilter(request, wrapped);

            String body = wrapped.getBody();

            service.markCompleted(record, body, wrapped.getStatus());

            response.getWriter().write(body);

        } catch (Exception ex) {
            service.markFailed(record);
            throw ex;
        }
    }

    private UUID getUserId() {
        return UUID.fromString("00000000-0000-0000-0000-000000000001");
    }
}
