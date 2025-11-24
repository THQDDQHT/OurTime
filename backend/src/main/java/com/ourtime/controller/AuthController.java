package com.ourtime.controller;

import com.ourtime.dto.ApiResponse;
import com.ourtime.dto.LoginRequest;
import com.ourtime.dto.LoginResponse;
import com.ourtime.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        if (!authService.validateSecret(request.getSecret())) {
            return ApiResponse.error(401, "暗号错误");
        }
        
        String token = authService.generateToken();
        return ApiResponse.success(new LoginResponse(token));
    }
}

