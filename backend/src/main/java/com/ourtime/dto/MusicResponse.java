package com.ourtime.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MusicResponse {
    private Long id;
    private String url;
    private String coverUrl;
    private LocalDateTime createdAt;
}
