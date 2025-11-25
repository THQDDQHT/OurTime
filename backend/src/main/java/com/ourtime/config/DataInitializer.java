package com.ourtime.config;

import com.ourtime.entity.Album;
import com.ourtime.entity.Moment;
import com.ourtime.entity.Music;
import com.ourtime.entity.User;
import com.ourtime.repository.AlbumRepository;
import com.ourtime.repository.MomentRepository;
import com.ourtime.repository.MusicRepository;
import com.ourtime.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
public class DataInitializer implements ApplicationRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AlbumRepository albumRepository;
    
    @Autowired
    private MomentRepository momentRepository;
    
    @Autowired
    private MusicRepository musicRepository;
    
    @Value("${app.root-password:root123456}")
    private String rootPassword;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        log.info("=== 开始初始化数据 ===");
        
        // 1. 检查并创建 root 账号
        User rootUser = userRepository.findByUsername("root").orElse(null);
        if (rootUser == null) {
            log.info("创建 root 管理员账号...");
            rootUser = new User();
            rootUser.setUsername("root");
            rootUser.setPassword(passwordEncoder.encode(rootPassword));
            rootUser.setRole(User.UserRole.ADMIN);
            rootUser = userRepository.save(rootUser);
            log.info("root 账号创建成功，ID: {}", rootUser.getId());
        } else {
            log.info("root 账号已存在，ID: {}", rootUser.getId());
        }
        
        // 2. 迁移现有数据到 root 账号
        Long rootUserId = rootUser.getId();
        
        // 迁移相册数据
        List<Album> albumsWithoutUser = albumRepository.findAll().stream()
                .filter(album -> album.getUserId() == null)
                .toList();
        if (!albumsWithoutUser.isEmpty()) {
            log.info("发现 {} 个未绑定用户的相册，正在迁移到 root 账号...", albumsWithoutUser.size());
            for (Album album : albumsWithoutUser) {
                album.setUserId(rootUserId);
            }
            albumRepository.saveAll(albumsWithoutUser);
            log.info("相册数据迁移完成");
        }
        
        // 迁移瞬间数据
        List<Moment> momentsWithoutUser = momentRepository.findAll().stream()
                .filter(moment -> moment.getUserId() == null)
                .toList();
        if (!momentsWithoutUser.isEmpty()) {
            log.info("发现 {} 个未绑定用户的瞬间，正在迁移到 root 账号...", momentsWithoutUser.size());
            for (Moment moment : momentsWithoutUser) {
                moment.setUserId(rootUserId);
            }
            momentRepository.saveAll(momentsWithoutUser);
            log.info("瞬间数据迁移完成");
        }
        
        // 迁移音乐数据
        List<Music> musicWithoutUser = musicRepository.findAll().stream()
                .filter(music -> music.getUserId() == null)
                .toList();
        if (!musicWithoutUser.isEmpty()) {
            log.info("发现 {} 个未绑定用户的音乐，正在迁移到 root 账号...", musicWithoutUser.size());
            for (Music music : musicWithoutUser) {
                music.setUserId(rootUserId);
            }
            musicRepository.saveAll(musicWithoutUser);
            log.info("音乐数据迁移完成");
        }
        
        log.info("=== 数据初始化完成 ===");
    }
}

