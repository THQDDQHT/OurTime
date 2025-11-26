# NEURAL (神经元) 🧠

> _The Operating System for your memory._  
> _为你记忆打造的操作系统。_

---

**Neural** 是一个私有部署的、极致审美的数字记忆中枢。它不仅仅是一个相册，而是你大脑的延伸。在这里，有你、你的数据以及对抗遗忘的意志。

## ✨ 核心特性

- **🌌 赛博科技 UI 设计 (Cyberpunk UI)**：
  - **神经主控台 (Neural Console)**：采用深空背景、扫描线与霓虹光效的全新桌面端主页。
  - **HUD 交互**：全息风格的抬头显示界面，极具未来感。
- **🧬 记忆双螺旋 (Memory Helix)**：
  - **3D 可视化**：记忆不再是平铺的列表，而是以 DNA 双螺旋结构在三维空间中旋转展示。
  - **动态交互**：自动旋转浏览，点击节点即可展开记忆详情。
- **⚛️ 量子反应堆播放器 (Quantum Reactor)**：
  - **硬核科技风**：取代传统播放器，采用核心脉冲与能量光环设计。
  - **沉浸听感**：支持背景轻音乐播放，伴随反应堆的律动，为回忆注入听觉维度。
- **🔒 绝对数据主权**：
  - 数据完全存储在你自己的硬盘上（Docker Volume），不依赖任何云服务商。
- **📸 多维感知记录**：
  - 支持高保真图片存储（自动优化）。
  - 支持长文本日记与地理位置标记。

## 🛠️ 技术栈

### Frontend

- **Core**: Vue 3 + TypeScript + Vite
- **UI Framework**: Naive UI (定制主题) + Tailwind CSS
- **Visuals**: CSS 3D Transforms, SVG Animations
- **State Management**: Pinia

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

### 3. 接入中枢

打开浏览器访问 `http://localhost`。

- 默认暗号：请在 `.env` 或 `docker-compose.yml` 中配置 `AUTH_SECRET`（默认为代码中预设值 `neural_secret_key`）。

## 💻 本地开发

### 后端 (Backend)

```bash
cd backend
# 确保本地 PostgreSQL 已启动并在 application.yml 中配置正确
mvn spring-boot:run
```

### 前端 (Frontend)

```bash
cd frontend
npm install
npm run dev
```

## 📅 进化路线图 (Evolution Roadmap)

详见 [Project Vision 2025](docs/VISION_2025.md)。

### v4.0 - 感知重构 (Sensory Reconstruction)

- [ ] **声音波纹**: 自动匹配环境白噪音，支持语音胶囊。
- [ ] **生物体征**: 叠加心率与健康数据。

### v5.0 - AI 幽灵 (The Ghost)

- [ ] **时光对话**: 与基于你过去日记训练的 AI 对话。
- [ ] **神经搜索**: 基于语义的模糊记忆检索。

## 📂 目录结构

```
neural/
├── backend/          # Spring Boot 后端源码
├── frontend/         # Vue 3 前端源码
├── nginx/            # Nginx 网关配置
├── docker-compose.yml # 容器编排文件
├── docs/             # 愿景与需求文档
└── README.md         # 说明文档
```

## 📄 许可证

MIT License
