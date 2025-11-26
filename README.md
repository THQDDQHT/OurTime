# OurTime (时光博物馆) 🕰️

> 在这里，没有算法，没有点赞，只有你和你的时光。

一个注重隐私与审美的个人数字空间，用于永久保存生活中的美好瞬间。现已全新升级 **v3.0 赛博科技版**，融合了拟物化与未来科幻风格，打造穿越时空的沉浸式体验。

## ✨ 核心特性

- **🌌 赛博科技 UI 设计**：
  - **Desktop Home**：全新的桌面端主页，采用深空背景、扫描线与霓虹光效，营造科幻指挥舱氛围。
  - **HUD 交互**：全息风格的抬头显示界面，极具未来感。
- **🧬 记忆双螺旋 (Memory Helix)**：
  - **3D 可视化**：相册以 DNA 双螺旋结构在三维空间中旋转展示。
  - **动态交互**：自动旋转浏览，点击节点即可展开记忆详情。
- **⚛️ 量子反应堆播放器 (Quantum Reactor)**：
  - **硬核科技风**：取代传统黑胶，采用核心脉冲与能量光环设计的音乐播放器。
  - **沉浸听感**：支持背景轻音乐播放，伴随反应堆的律动，让回忆更有质感。
- **🔒 绝对私密空间**：无注册机制，仅持“暗号”可进入，数据完全私有化部署。
- **📸 多维记录方式**：支持图片（自动压缩）、长文本、地理位置、日期等多维度记录。
- **👀 双视图展示**：
  - **时间轴**：按时间倒序梳理人生轨迹。
  - **照片墙**：瀑布流布局，自适应美观展示海量照片。
- **📁 相册管理系统**：支持创建相册、自定义封面图。

## 🛠️ 技术栈

### Frontend

- **Core**: Vue 3 + TypeScript + Vite
- **UI Framework**: Naive UI (定制主题) + Tailwind CSS
- **Visuals**: CSS 3D Transforms, SVG Animations
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

- 默认暗号：请在 `.env` 或 `docker-compose.yml` 中配置 `AUTH_SECRET`（默认为代码中预设值 `ourtime_secret_key`）。

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

## 📅 路线图 (Roadmap)

### v4.0 规划 - 意识上传

- [ ] **声音胶囊**: 支持为瞬间添加录音或环境白噪音。
- [ ] **世界足迹**: 自动解析位置信息，在 3D 地球上点亮去过的地方。
- [ ] **时光信箱**: 写给未来的一封信，到达指定日期自动解封。
- [ ] **AI 记忆助手**: 智能分析照片内容，生成回忆摘要。

## 📂 目录结构

```
our-time/
├── backend/          # Spring Boot 后端源码
├── frontend/         # Vue 3 前端源码
├── nginx/            # Nginx 网关配置
├── docker-compose.yml # 容器编排文件
├── docs/             # 需求文档 (PRD)
└── README.md         # 项目说明
```

## 📄 许可证

MIT License
