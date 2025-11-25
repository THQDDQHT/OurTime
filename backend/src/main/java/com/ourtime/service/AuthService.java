package com.ourtime.service;

import com.ourtime.dto.LoginResponse;
import com.ourtime.dto.RegisterRequest;
import com.ourtime.entity.User;
import com.ourtime.repository.UserRepository;
import com.ourtime.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserRepository userRepository;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    public LoginResponse register(RegisterRequest request) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 创建新用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.UserRole.USER);
        
        user = userRepository.save(user);
        
        // 生成 token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole().name());
        
        return new LoginResponse(token, user.getId(), user.getUsername(), user.getRole().name());
    }
    
    public LoginResponse login(String username, String password) {
        // 查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));
        
        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 生成 token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole().name());
        
        return new LoginResponse(token, user.getId(), user.getUsername(), user.getRole().name());
    }
}

