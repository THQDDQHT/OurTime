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

    public List<MusicResponse> getAllMusic() {
        return musicRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public MusicResponse createMusic(MusicRequest request) {
        Music music = new Music();
        BeanUtils.copyProperties(request, music);
        Music savedMusic = musicRepository.save(music);
        return convertToResponse(savedMusic);
    }

    @Transactional
    public void deleteMusic(Long id) {
        musicRepository.deleteById(id);
    }

    private MusicResponse convertToResponse(Music music) {
        MusicResponse response = new MusicResponse();
        BeanUtils.copyProperties(music, response);
        return response;
    }
}

