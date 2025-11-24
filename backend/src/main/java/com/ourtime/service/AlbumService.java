package com.ourtime.service;

import com.ourtime.dto.AlbumRequest;
import com.ourtime.dto.AlbumResponse;
import com.ourtime.entity.Album;
import com.ourtime.repository.AlbumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlbumService {
    
    @Autowired
    private AlbumRepository albumRepository;
    
    public List<AlbumResponse> getAllAlbums() {
        return albumRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    public AlbumResponse getAlbumById(Long id) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("相册不存在"));
        return toResponse(album);
    }
    
    @Transactional
    public AlbumResponse createAlbum(AlbumRequest request) {
        Album album = new Album();
        album.setName(request.getName());
        album.setDescription(request.getDescription());
        album.setCoverUrl(request.getCoverUrl());
        
        album = albumRepository.save(album);
        return toResponse(album);
    }
    
    @Transactional
    public AlbumResponse updateAlbum(Long id, AlbumRequest request) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("相册不存在"));
        
        if (request.getName() != null) {
            album.setName(request.getName());
        }
        if (request.getDescription() != null) {
            album.setDescription(request.getDescription());
        }
        if (request.getCoverUrl() != null) {
            album.setCoverUrl(request.getCoverUrl());
        }
        
        album = albumRepository.save(album);
        return toResponse(album);
    }
    
    @Transactional
    public void deleteAlbum(Long id) {
        if (!albumRepository.existsById(id)) {
            throw new RuntimeException("相册不存在");
        }
        albumRepository.deleteById(id);
    }
    
    private AlbumResponse toResponse(Album album) {
        return new AlbumResponse(
                album.getId(),
                album.getName(),
                album.getDescription(),
                album.getCoverUrl(),
                album.getCreatedAt()
        );
    }
}

