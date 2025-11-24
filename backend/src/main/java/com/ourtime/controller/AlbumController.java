package com.ourtime.controller;

import com.ourtime.dto.AlbumRequest;
import com.ourtime.dto.AlbumResponse;
import com.ourtime.dto.ApiResponse;
import com.ourtime.service.AlbumService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/albums")
public class AlbumController {
    
    @Autowired
    private AlbumService albumService;
    
    @GetMapping
    public ApiResponse<List<AlbumResponse>> getAllAlbums() {
        List<AlbumResponse> albums = albumService.getAllAlbums();
        return ApiResponse.success(albums);
    }
    
    @GetMapping("/{id}")
    public ApiResponse<AlbumResponse> getAlbumById(@PathVariable Long id) {
        AlbumResponse album = albumService.getAlbumById(id);
        return ApiResponse.success(album);
    }
    
    @PostMapping
    public ApiResponse<AlbumResponse> createAlbum(@Valid @RequestBody AlbumRequest request) {
        AlbumResponse album = albumService.createAlbum(request);
        return ApiResponse.success(album);
    }
    
    @PutMapping("/{id}")
    public ApiResponse<AlbumResponse> updateAlbum(@PathVariable Long id, 
                                                  @Valid @RequestBody AlbumRequest request) {
        AlbumResponse album = albumService.updateAlbum(id, request);
        return ApiResponse.success(album);
    }
    
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteAlbum(@PathVariable Long id) {
        albumService.deleteAlbum(id);
        return ApiResponse.success(null);
    }
}

