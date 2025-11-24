package com.ourtime.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileUploadService {
    
    @Value("${app.upload-path}")
    private String uploadPath;
    
    private Path getUploadDirectory() {
        Path path = Paths.get(uploadPath);
        // 如果是相对路径，转换为绝对路径
        if (!path.isAbsolute()) {
            // 获取当前工作目录
            String userDir = System.getProperty("user.dir");
            Path basePath;
            
            // 判断当前目录是否是 backend 目录
            if (userDir.endsWith("backend")) {
                basePath = Paths.get(userDir);
            } else {
                // 尝试查找 backend 目录
                Path backendDir = Paths.get(userDir, "backend");
                if (Files.exists(backendDir)) {
                    basePath = backendDir;
                } else {
                    // 如果找不到 backend 目录，使用当前目录
                    basePath = Paths.get(userDir);
                }
            }
            
            // 构建完整路径
            path = basePath.resolve(uploadPath);
        }
        return path.toAbsolutePath().normalize();
    }
    
    public String saveFile(MultipartFile file) throws IOException {
        // 确保上传目录存在
        Path uploadDir = getUploadDirectory();
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        
        // 生成 UUID 文件名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String newFilename = IdUtil.simpleUUID() + extension;
        
        // 保存文件 - 使用 Files.copy 更可靠
        Path filePath = uploadDir.resolve(newFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // 返回API路径（通过Controller访问）
        return "/api/v1/uploads/" + newFilename;
    }
    
    public int[] getImageDimensions(MultipartFile file) throws IOException {
        BufferedImage image = ImageIO.read(file.getInputStream());
        if (image == null) {
            return new int[]{0, 0};
        }
        return new int[]{image.getWidth(), image.getHeight()};
    }
}

