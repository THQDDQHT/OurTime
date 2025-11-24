package com.ourtime.service;

import com.ourtime.dto.MomentRequest;
import com.ourtime.dto.MomentResponse;
import com.ourtime.dto.PhotoRequest;
import com.ourtime.dto.PhotoResponse;
import com.ourtime.entity.Moment;
import com.ourtime.entity.Photo;
import com.ourtime.repository.MomentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class MomentService {
    
    @Autowired
    private MomentRepository momentRepository;
    
    @Transactional
    public MomentResponse createMoment(MomentRequest request) {
        Moment moment = new Moment();
        moment.setContent(request.getContent());
        moment.setHappenedAt(request.getHappenedAt() != null ? 
            request.getHappenedAt() : java.time.LocalDateTime.now());
        moment.setLocation(request.getLocation());
        moment.setAlbumId(request.getAlbumId());
        
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
        
        return toResponse(moment);
    }
    
    public Page<MomentResponse> getMoments(Pageable pageable) {
        return momentRepository.findAllByOrderByHappenedAtDesc(pageable)
                .map(this::toResponse);
    }
    
    public MomentResponse getMomentById(Long id) {
        Moment moment = momentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("瞬间不存在"));
        return toResponse(moment);
    }
    
    @Transactional
    public void deleteMoment(Long id) {
        if (!momentRepository.existsById(id)) {
            throw new RuntimeException("瞬间不存在");
        }
        momentRepository.deleteById(id);
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

