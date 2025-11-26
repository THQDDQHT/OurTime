package com.neural.dto;

import lombok.Data;

@Data
public class AlbumRequest {
    private String name;
    private String description;
    private String coverUrl;
}

