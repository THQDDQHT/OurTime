package com.ourtime.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhotoRequest {
    private String filePath;
    private Integer width;
    private Integer height;
    private Long size;
}

