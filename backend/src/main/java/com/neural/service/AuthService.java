package com.neural.service;

import com.neural.dto.LoginResponse;
import com.neural.dto.RegisterRequest;
import com.neural.entity.User;
import com.neural.repository.UserRepository;
import com.neural.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthService {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserRepository userRepository;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    public LoginResponse register(RegisterRequest request) {
        log.info("Registering new user: {}", request.getUsername());
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Username already exists: {}", request.getUsername());
            throw new RuntimeException("用户名已存在");
        }
        
        // 创建新用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.UserRole.USER);
        
        user = userRepository.save(user);
        log.info("User registered successfully: id={}, username={}", user.getId(), user.getUsername());
        
        // 生成 token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole().name());
        
        return new LoginResponse(token, user.getId(), user.getUsername(), user.getRole().name());
    }
    
    public LoginResponse login(String username, String password) {
        // 查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Login failed - User not found: {}", username);
                    return new RuntimeException("用户名或密码错误");
                });
        
        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("Login failed - Invalid password for user: {}", username);
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 生成 token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole().name());
        log.info("User logged in successfully: id={}, username={}", user.getId(), user.getUsername());
        
        return new LoginResponse(token, user.getId(), user.getUsername(), user.getRole().name());
    }
}

