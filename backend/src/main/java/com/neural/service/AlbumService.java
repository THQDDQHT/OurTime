package com.neural.service;

import com.neural.dto.AlbumRequest;
import com.neural.dto.AlbumResponse;
import com.neural.entity.Album;
import com.neural.repository.AlbumRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AlbumService {
    
    @Autowired
    private AlbumRepository albumRepository;
    
    public List<AlbumResponse> getAllAlbums(Long userId, String role) {
        // ... existing code ...
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
                    .orElseThrow(() -> {
                        log.warn("Album not found: id={}", id);
                        return new RuntimeException("相册不存");
                    });
        } else {
            album = albumRepository.findByIdAndUserId(id, userId)
                    .orElseThrow(() -> {
                        log.warn("Album not found or access denied: id={}, userId={}", id, userId);
                        return new RuntimeException("相册不存在或无权访问");
                    });
        }
        return toResponse(album);
    }
    
    @Transactional
    public AlbumResponse createAlbum(AlbumRequest request, Long userId) {
        log.info("Creating album: name={}, userId={}", request.getName(), userId);
        Album album = new Album();
        album.setName(request.getName());
        album.setDescription(request.getDescription());
        album.setCoverUrl(request.getCoverUrl());
        album.setUserId(userId);
        
        album = albumRepository.save(album);
        log.info("Album created: id={}", album.getId());
        return toResponse(album);
    }
    
    @Transactional
    public AlbumResponse updateAlbum(Long id, AlbumRequest request, Long userId, String role) {
        log.info("Updating album: id={}, userId={}", id, userId);
        Album album;
        if ("ADMIN".equals(role)) {
            album = albumRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Album not found for update: id={}", id);
                        return new RuntimeException("相册不存");
                    });
        } else {
            album = albumRepository.findByIdAndUserId(id, userId)
                    .orElseThrow(() -> {
                        log.warn("Album not found or access denied for update: id={}, userId={}", id, userId);
                        return new RuntimeException("相册不存在或无权访问");
                    });
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
        log.info("Album updated: id={}", album.getId());
        return toResponse(album);
    }
    
    @Transactional
    public void deleteAlbum(Long id, Long userId, String role) {
        log.info("Deleting album: id={}, userId={}, role={}", id, userId, role);
        if ("ADMIN".equals(role)) {
            if (!albumRepository.existsById(id)) {
                log.warn("Album not found for delete: id={}", id);
                throw new RuntimeException("相册不存");
            }
        } else {
            if (!albumRepository.findByIdAndUserId(id, userId).isPresent()) {
                log.warn("Album not found or access denied for delete: id={}, userId={}", id, userId);
                throw new RuntimeException("相册不存在或无权访问");
            }
        }
        albumRepository.deleteById(id);
        log.info("Album deleted: id={}", id);
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

