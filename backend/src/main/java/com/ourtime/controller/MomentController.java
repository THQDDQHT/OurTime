package com.ourtime.controller;

import com.ourtime.dto.ApiResponse;
import com.ourtime.dto.MomentRequest;
import com.ourtime.dto.MomentResponse;
import com.ourtime.service.MomentService;
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
    public ApiResponse<MomentResponse> createMoment(@Valid @RequestBody MomentRequest request) {
        MomentResponse response = momentService.createMoment(request);
        return ApiResponse.success(response);
    }
    
    @GetMapping
    public ApiResponse<Page<MomentResponse>> getMoments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MomentResponse> moments = momentService.getMoments(pageable);
        return ApiResponse.success(moments);
    }
    
    @GetMapping("/{id}")
    public ApiResponse<MomentResponse> getMomentById(@PathVariable Long id) {
        MomentResponse response = momentService.getMomentById(id);
        return ApiResponse.success(response);
    }
    
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteMoment(@PathVariable Long id) {
        momentService.deleteMoment(id);
        return ApiResponse.success(null);
    }
}

