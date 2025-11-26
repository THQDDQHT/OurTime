package com.ourtime.service;

import com.ourtime.dto.MomentRequest;
import com.ourtime.dto.MomentResponse;
import com.ourtime.dto.PhotoRequest;
import com.ourtime.dto.PhotoResponse;
import com.ourtime.entity.Moment;
import com.ourtime.entity.Photo;
import com.ourtime.repository.MomentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Slf4j
@Service
public class MomentService {
    
    @Autowired
    private MomentRepository momentRepository;
    
    @Transactional
    public MomentResponse createMoment(MomentRequest request, Long userId) {
        int photoCount = request.getPhotos() != null ? request.getPhotos().size() : 0;
        log.info("Creating moment: userId={}, photoCount={}, location={}", userId, photoCount, request.getLocation());
        
        Moment moment = new Moment();
        moment.setContent(request.getContent());
        moment.setHappenedAt(request.getHappenedAt() != null ? 
            request.getHappenedAt() : java.time.LocalDateTime.now());
        moment.setLocation(request.getLocation());
        moment.setAlbumId(request.getAlbumId());
        moment.setUserId(userId);
        
        // 处理照片
        if (request.getPhotos() != null) {
            for (PhotoRequest photoRequest : request.getPhotos()) {
                Photo photo = new Photo();
                photo.setFilePath(photoRequest.getFilePath());
                photo.setWidth(photoRequest.getWidth());
                photo.setHeight(photoRequest.getHeight());
                photo.setSize(photoRequest.getSize());
                photo.setMoment(moment); // 设置关联
                moment.getPhotos().add(photo);
            }
        }
        
        moment = momentRepository.save(moment);
        log.info("Moment created: id={}", moment.getId());
        
        return toResponse(moment);
    }
    
    public Page<MomentResponse> getMoments(Pageable pageable, Long userId, String role, Long albumId) {
        // ... existing logic ...
        if ("ADMIN".equals(role)) {
            if (albumId != null) {
                return momentRepository.findByAlbumIdOrderByHappenedAtDesc(albumId, pageable)
                        .map(this::toResponse);
            }
            return momentRepository.findAllByOrderByHappenedAtDesc(pageable)
                    .map(this::toResponse);
        } else {
            if (albumId != null) {
                return momentRepository.findByAlbumIdAndUserIdOrderByHappenedAtDesc(albumId, userId, pageable)
                        .map(this::toResponse);
            }
            return momentRepository.findByUserIdOrderByHappenedAtDesc(userId, pageable)
                    .map(this::toResponse);
        }
    }
    
    public MomentResponse getMomentById(Long id, Long userId, String role) {
        Moment moment;
        if ("ADMIN".equals(role)) {
            moment = momentRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Moment not found: id={}", id);
                        return new RuntimeException("瞬间不存在");
                    });
        } else {
            moment = momentRepository.findByIdAndUserId(id, userId)
                    .orElseThrow(() -> {
                        log.warn("Moment not found or access denied: id={}, userId={}", id, userId);
                        return new RuntimeException("瞬间不存在或无权访问");
                    });
        }
        return toResponse(moment);
    }
    
    @Transactional
    public void deleteMoment(Long id, Long userId, String role) {
        log.info("Deleting moment: id={}, userId={}, role={}", id, userId, role);
        if ("ADMIN".equals(role)) {
            if (!momentRepository.existsById(id)) {
                log.warn("Moment not found for delete: id={}", id);
                throw new RuntimeException("瞬间不存在");
            }
        } else {
            if (!momentRepository.findByIdAndUserId(id, userId).isPresent()) {
                log.warn("Moment not found or access denied for delete: id={}, userId={}", id, userId);
                throw new RuntimeException("瞬间不存在或无权访问");
            }
        }
        momentRepository.deleteById(id);
        log.info("Moment deleted: id={}", id);
    }
    
    private MomentResponse toResponse(Moment moment) {
        MomentResponse response = new MomentResponse();
        response.setId(moment.getId());
        response.setContent(moment.getContent());
        response.setHappenedAt(moment.getHappenedAt());
        response.setLocation(moment.getLocation());
        response.setAlbumId(moment.getAlbumId());
        response.setCreatedAt(moment.getCreatedAt());
        
        if (moment.getAlbum() != null) {
            response.setAlbumName(moment.getAlbum().getName());
        }
        
        if (moment.getPhotos() != null) {
            response.setPhotos(moment.getPhotos().stream()
                    .map(photo -> new PhotoResponse(
                            photo.getId(),
                            photo.getFilePath(),
                            photo.getWidth(),
                            photo.getHeight(),
                            photo.getSize()
                    ))
                    .collect(Collectors.toList()));
        }
        
        return response;
    }
}

