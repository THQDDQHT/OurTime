package com.neural.controller;

import com.neural.dto.ApiResponse;
import com.neural.dto.MusicRequest;
import com.neural.dto.MusicResponse;
import com.neural.service.MusicService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/music")
@RequiredArgsConstructor
public class MusicController {

    private final MusicService musicService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MusicResponse>>> getMusicList(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        return ResponseEntity.ok(ApiResponse.success(musicService.getAllMusic(userId, role)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MusicResponse>> createMusic(@RequestBody MusicRequest request,
                                                                   HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        return ResponseEntity.ok(ApiResponse.success(musicService.createMusic(request, userId)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMusic(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        musicService.deleteMusic(id, userId, role);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}

