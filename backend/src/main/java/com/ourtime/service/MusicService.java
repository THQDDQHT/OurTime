package com.ourtime.service;

import com.ourtime.dto.MusicRequest;
import com.ourtime.dto.MusicResponse;
import com.ourtime.entity.Music;
import com.ourtime.repository.MusicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MusicService {

    private final MusicRepository musicRepository;

    public List<MusicResponse> getAllMusic(Long userId, String role) {
        // ... existing logic ...
        List<Music> musicList;
        if ("ADMIN".equals(role)) {
            musicList = musicRepository.findAllByOrderByCreatedAtDesc();
        } else {
            musicList = musicRepository.findByUserIdOrderByCreatedAtDesc(userId);
        }
        return musicList.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public MusicResponse createMusic(MusicRequest request, Long userId) {
        log.info("Creating music: url={}, userId={}", request.getUrl(), userId);
        Music music = new Music();
        BeanUtils.copyProperties(request, music);
        music.setUserId(userId);
        Music savedMusic = musicRepository.save(music);
        log.info("Music created: id={}", savedMusic.getId());
        return convertToResponse(savedMusic);
    }

    @Transactional
    public void deleteMusic(Long id, Long userId, String role) {
        log.info("Deleting music: id={}, userId={}, role={}", id, userId, role);
        if ("ADMIN".equals(role)) {
            if (!musicRepository.existsById(id)) {
                log.warn("Music not found for delete: id={}", id);
                throw new RuntimeException("音乐不存在");
            }
        } else {
            if (!musicRepository.findByIdAndUserId(id, userId).isPresent()) {
                log.warn("Music not found or access denied for delete: id={}, userId={}", id, userId);
                throw new RuntimeException("音乐不存在或无权访问");
            }
        }
        musicRepository.deleteById(id);
        log.info("Music deleted: id={}", id);
    }

    private MusicResponse convertToResponse(Music music) {
        MusicResponse response = new MusicResponse();
        BeanUtils.copyProperties(music, response);
        return response;
    }
}

