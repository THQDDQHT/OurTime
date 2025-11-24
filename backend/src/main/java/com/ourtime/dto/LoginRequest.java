package com.ourtime.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "暗号不能为空")
    private String secret;
}

