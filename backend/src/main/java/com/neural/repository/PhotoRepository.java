package com.neural.repository;

import com.neural.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findByMomentId(Long momentId);
    void deleteByMomentId(Long momentId);
}

