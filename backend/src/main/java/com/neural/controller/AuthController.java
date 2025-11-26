package com.neural.controller;

import com.neural.dto.ApiResponse;
import com.neural.dto.LoginRequest;
import com.neural.dto.LoginResponse;
import com.neural.dto.RegisterRequest;
import com.neural.service.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/v1/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/register")
    public ApiResponse<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            LoginResponse response = authService.register(request);
            return ApiResponse.success(response);
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }
    
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.debug("Received login request - username: {}", request.getUsername());
        try {
            LoginResponse response = authService.login(request.getUsername(), request.getPassword());
            log.debug("Login successful - token generated userId: {}", response.getUserId());
            return ApiResponse.success(response);
        } catch (RuntimeException e) {
            log.warn("Login failed: {}", e.getMessage());
            return ApiResponse.error(401, e.getMessage());
        }
    }
}

