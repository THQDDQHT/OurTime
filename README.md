# OurTime (时光博物馆) 🕰️

> 在这里，没有算法，没有点赞，只有你和你的时光。

一个注重隐私与审美的个人数字空间，用于永久保存生活中的美好瞬间。采用了拟物化设计风格，营造出静谧、沉稳的博物馆氛围。

## ✨ 核心特性

- **🏺 拟物化 UI 设计**：沉浸式的视觉体验，时间轴与卡片细节打磨，宛如翻阅实体相册。
- **🎵 氛围音乐播放器**：内置悬浮式黑胶唱片组件，支持播放背景轻音乐，让回忆更有质感。
- **🔒 绝对私密空间**：无注册机制，仅持“暗号”可进入，数据完全私有化部署。
- **📸 多维记录方式**：支持图片（自动压缩）、长文本、地理位置、日期等多维度记录。
- **👀 双视图展示**：
  - **时间轴**：按时间倒序梳理人生轨迹。
  - **照片墙**：瀑布流布局，自适应美观展示海量照片。
- **📁 相册管理系统**：支持创建相册、自定义封面图（支持上传或默认图标）。

## 🛠️ 技术栈

### Frontend
- **Core**: Vue 3 + TypeScript + Vite
- **UI Framework**: Naive UI (深度定制主题) + Tailwind CSS
- **State Management**: Pinia
- **Utilities**: Axios, Vue Router

### Backend
- **Core**: Spring Boot 3.x + Java 21
- **Database**: PostgreSQL 15
- **Security**: Custom Auth (Secret Key) + JWT
- **Storage**: Local File System (Docker Volume)

## 🚀 快速部署

### 1. 环境准备
- Docker & Docker Compose

### 2. 启动服务
将项目克隆到本地后，直接运行：

```bash
docker-compose up -d --build
```

### 3. 访问应用
打开浏览器访问 `http://localhost`。
- 默认暗号：请在 `.env` 文件中配置 `AUTH_SECRET`（默认为代码中预设值）。

## 💻 本地开发

### 后端 (Backend)
```bash
cd backend
# 需配置本地 PostgreSQL 或修改 application.yml
mvn spring-boot:run
```

### 前端 (Frontend)
```bash
cd frontend
npm install
npm run dev
```

## 📂 目录结构

```
our-time/
├── backend/          # Spring Boot 后端源码
├── frontend/         # Vue 3 前端源码
├── nginx/            # Nginx 网关配置
├── docker-compose.yml # 容器编排文件
├── docs/             # 需求文档
└── README.md         # 项目说明
```

## 📄 许可证

MIT License
