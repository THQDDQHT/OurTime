package com.ourtime.service;

import com.ourtime.dto.MusicRequest;
import com.ourtime.dto.MusicResponse;
import com.ourtime.entity.Music;
import com.ourtime.repository.MusicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MusicService {

    private final MusicRepository musicRepository;

    public List<MusicResponse> getAllMusic(Long userId, String role) {
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
        Music music = new Music();
        BeanUtils.copyProperties(request, music);
        music.setUserId(userId);
        Music savedMusic = musicRepository.save(music);
        return convertToResponse(savedMusic);
    }

    @Transactional
    public void deleteMusic(Long id, Long userId, String role) {
        if ("ADMIN".equals(role)) {
            if (!musicRepository.existsById(id)) {
                throw new RuntimeException("音乐不存在");
            }
        } else {
            if (!musicRepository.findByIdAndUserId(id, userId).isPresent()) {
                throw new RuntimeException("音乐不存在或无权访问");
            }
        }
        musicRepository.deleteById(id);
    }

    private MusicResponse convertToResponse(Music music) {
        MusicResponse response = new MusicResponse();
        BeanUtils.copyProperties(music, response);
        return response;
    }
}

