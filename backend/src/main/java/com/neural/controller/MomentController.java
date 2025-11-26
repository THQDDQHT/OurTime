package com.neural.controller;

import com.neural.dto.ApiResponse;
import com.neural.dto.MomentRequest;
import com.neural.dto.MomentResponse;
import com.neural.service.MomentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/moments")
public class MomentController {
    
    @Autowired
    private MomentService momentService;
    
    @PostMapping
    public ApiResponse<MomentResponse> createMoment(@Valid @RequestBody MomentRequest request,
                                                     HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        MomentResponse response = momentService.createMoment(request, userId);
        return ApiResponse.success(response);
    }
    
    @GetMapping
    public ApiResponse<Page<MomentResponse>> getMoments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long albumId,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        Pageable pageable = PageRequest.of(page, size);
        Page<MomentResponse> moments = momentService.getMoments(pageable, userId, role, albumId);
        return ApiResponse.success(moments);
    }
    
    @GetMapping("/{id}")
    public ApiResponse<MomentResponse> getMomentById(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        MomentResponse response = momentService.getMomentById(id, userId, role);
        return ApiResponse.success(response);
    }
    
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteMoment(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        momentService.deleteMoment(id, userId, role);
        return ApiResponse.success(null);
    }
}

