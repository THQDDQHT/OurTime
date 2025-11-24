package com.ourtime.controller;

import com.ourtime.dto.ApiResponse;
import com.ourtime.dto.MusicRequest;
import com.ourtime.dto.MusicResponse;
import com.ourtime.service.MusicService;
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
    public ResponseEntity<ApiResponse<List<MusicResponse>>> getMusicList() {
        return ResponseEntity.ok(ApiResponse.success(musicService.getAllMusic()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MusicResponse>> createMusic(@RequestBody MusicRequest request) {
        return ResponseEntity.ok(ApiResponse.success(musicService.createMusic(request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMusic(@PathVariable Long id) {
        musicService.deleteMusic(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}

