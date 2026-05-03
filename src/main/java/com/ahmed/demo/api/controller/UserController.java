package com.ahmed.demo.api.controller;


import com.ahmed.demo.application.dto.AuthResponse;
import com.ahmed.demo.application.dto.LoginRequest;
import com.ahmed.demo.application.dto.RefreshRequest;
import com.ahmed.demo.application.dto.RegisterRequest;
import com.ahmed.demo.application.security.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class UserController {
    private final AuthService authService;

    @PostMapping("/register")
    public void register(@Valid @RequestBody RegisterRequest request){
        authService.register(request);
    }
    @PostMapping("/login")
    public void login(@Valid @RequestBody LoginRequest request){
        authService.login(request);
    }
    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestBody RefreshRequest request) {
        return authService.refresh(request.refreshToken());
    }

    @PostMapping("/logout")
    public void logout(@RequestBody RefreshRequest request) {
        authService.logout(request.refreshToken());
    }
}
