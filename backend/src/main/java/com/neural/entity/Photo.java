package com.neural.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "photos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Photo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moment_id", nullable = false)
    private Moment moment;
    
    @Column(name = "moment_id", insertable = false, updatable = false)
    private Long momentId;
    
    @Column(name = "file_path", nullable = false)
    private String filePath;
    
    private Integer width;
    
    private Integer height;
    
    private Long size;
}

