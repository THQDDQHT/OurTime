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
    
    public List<AlbumResponse> getAllAlbums(Long userId, String role) {
        List<Album> albums;
        if ("ADMIN".equals(role)) {
            albums = albumRepository.findAll();
        } else {
            albums = albumRepository.findByUserId(userId);
        }
        return albums.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    public AlbumResponse getAlbumById(Long id, Long userId, String role) {
        Album album;
        if ("ADMIN".equals(role)) {
            album = albumRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("相册不存在"));
        } else {
            album = albumRepository.findByIdAndUserId(id, userId)
                    .orElseThrow(() -> new RuntimeException("相册不存在或无权访问"));
        }
        return toResponse(album);
    }
    
    @Transactional
    public AlbumResponse createAlbum(AlbumRequest request, Long userId) {
        Album album = new Album();
        album.setName(request.getName());
        album.setDescription(request.getDescription());
        album.setCoverUrl(request.getCoverUrl());
        album.setUserId(userId);
        
        album = albumRepository.save(album);
        return toResponse(album);
    }
    
    @Transactional
    public AlbumResponse updateAlbum(Long id, AlbumRequest request, Long userId, String role) {
        Album album;
        if ("ADMIN".equals(role)) {
            album = albumRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("相册不存在"));
        } else {
            album = albumRepository.findByIdAndUserId(id, userId)
                    .orElseThrow(() -> new RuntimeException("相册不存在或无权访问"));
        }
        
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
    public void deleteAlbum(Long id, Long userId, String role) {
        if ("ADMIN".equals(role)) {
            if (!albumRepository.existsById(id)) {
                throw new RuntimeException("相册不存在");
            }
        } else {
            if (!albumRepository.findByIdAndUserId(id, userId).isPresent()) {
                throw new RuntimeException("相册不存在或无权访问");
            }
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

