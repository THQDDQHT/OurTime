package com.ourtime.repository;

import com.ourtime.entity.Moment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MomentRepository extends JpaRepository<Moment, Long> {
    Page<Moment> findAllByOrderByHappenedAtDesc(Pageable pageable);
}

