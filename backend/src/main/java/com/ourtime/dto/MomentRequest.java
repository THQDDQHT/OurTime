package com.ourtime.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class MomentRequest {
    private String content;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime happenedAt;
    
    private String location;
    
    private Long albumId;
    
    private List<PhotoRequest> photos = new ArrayList<>();
}

