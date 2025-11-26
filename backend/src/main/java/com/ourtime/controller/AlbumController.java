package com.ourtime.controller;

import com.ourtime.dto.AlbumRequest;
import com.ourtime.dto.AlbumResponse;
import com.ourtime.dto.ApiResponse;
import com.ourtime.service.AlbumService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/albums")
public class AlbumController {
    
    @Autowired
    private AlbumService albumService;
    
    @GetMapping
    public ApiResponse<List<AlbumResponse>> getAllAlbums(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        log.debug("GetAllAlbums request - userId: {}, role: {}", userId, role);
        List<AlbumResponse> albums = albumService.getAllAlbums(userId, role);
        log.debug("Query result size: {}", albums.size());
        return ApiResponse.success(albums);
    }
    
    @GetMapping("/{id}")
    public ApiResponse<AlbumResponse> getAlbumById(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        AlbumResponse album = albumService.getAlbumById(id, userId, role);
        return ApiResponse.success(album);
    }
    
    @PostMapping
    public ApiResponse<AlbumResponse> createAlbum(@Valid @RequestBody AlbumRequest request, 
                                                   HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        AlbumResponse album = albumService.createAlbum(request, userId);
        return ApiResponse.success(album);
    }
    
    @PutMapping("/{id}")
    public ApiResponse<AlbumResponse> updateAlbum(@PathVariable Long id, 
                                                  @Valid @RequestBody AlbumRequest request,
                                                  HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        String role = (String) httpRequest.getAttribute("role");
        AlbumResponse album = albumService.updateAlbum(id, request, userId, role);
        return ApiResponse.success(album);
    }
    
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteAlbum(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        albumService.deleteAlbum(id, userId, role);
        return ApiResponse.success(null);
    }
}

