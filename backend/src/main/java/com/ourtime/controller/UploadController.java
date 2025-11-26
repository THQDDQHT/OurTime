package com.ourtime.controller;

import com.ourtime.dto.ApiResponse;
import com.ourtime.dto.UploadResponse;
import com.ourtime.service.FileUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/v1/upload")
public class UploadController {
    
    @Autowired
    private FileUploadService fileUploadService;
    
    @PostMapping
    public ApiResponse<UploadResponse> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            log.warn("Upload attempt with empty file");
            return ApiResponse.error("文件不能为空");
        }
        
        try {
            String url = fileUploadService.saveFile(file);
            int[] dimensions = fileUploadService.getImageDimensions(file);
            
            UploadResponse response = new UploadResponse(
                url,
                dimensions[0],
                dimensions[1],
                file.getSize()
            );
            
            log.info("File uploaded successfully: {}, size={}, dim={}x{}", url, file.getSize(), dimensions[0], dimensions[1]);
            return ApiResponse.success(response);
        } catch (IOException e) {
            log.error("File upload failed", e);
            return ApiResponse.error("文件上传失败: " + e.getMessage());
        }
    }
}

