package com.ourtime.service;

import com.ourtime.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Value("${app.auth-secret}")
    private String authSecret;
    
    public boolean validateSecret(String secret) {
        return authSecret.equals(secret);
    }
    
    public String generateToken() {
        return jwtUtil.generateToken();
    }
}

