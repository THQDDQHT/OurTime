package com.ourtime.repository;

import com.ourtime.entity.Music;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MusicRepository extends JpaRepository<Music, Long> {
    List<Music> findAllByOrderByCreatedAtDesc();
}

