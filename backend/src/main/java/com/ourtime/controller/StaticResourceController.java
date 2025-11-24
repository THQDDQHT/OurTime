package com.ourtime.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/v1/uploads")
public class StaticResourceController {
    
    @Value("${app.upload-path}")
    private String uploadPath;
    
    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            // 构建文件路径
            Path uploadDir = Paths.get(uploadPath);
            if (!uploadDir.isAbsolute()) {
                String userDir = System.getProperty("user.dir");
                if (userDir.endsWith("backend")) {
                    uploadDir = Paths.get(userDir, uploadPath);
                } else {
                    Path backendDir = Paths.get(userDir, "backend");
                    if (Files.exists(backendDir)) {
                        uploadDir = Paths.get(userDir, "backend", uploadPath);
                    } else {
                        uploadDir = Paths.get(userDir, uploadPath);
                    }
                }
            }
            uploadDir = uploadDir.toAbsolutePath().normalize();
            
            Path filePath = uploadDir.resolve(filename).normalize();
            
            // 安全检查：确保文件在上传目录内
            if (!filePath.startsWith(uploadDir)) {
                return ResponseEntity.badRequest().build();
            }
            
            // 检查文件是否存在
            if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
                return ResponseEntity.notFound().build();
            }
            
            // 读取文件
            Resource resource = new FileSystemResource(filePath);
            
            // 获取文件类型
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            
            // 返回文件
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);
                    
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

