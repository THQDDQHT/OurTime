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

# 🚀 快速部署

## 📋 部署前准备

### 1. 本地打包文件

#### 前端打包

```bash
cd frontend
npm install
npm run build
```

打包完成后会生成 `frontend/dist` 目录

#### 后端打包

```bash
cd backend
mvn clean package -DskipTests
```

打包完成后会生成 `backend/target/neural-backend-1.0.0.jar`

## 🚀 部署步骤

### 使用外部数据库部署（推荐）

适用于：已有 PostgreSQL 数据库，只部署应用

## 📁 上传文件清单

需要上传到服务器的文件/目录：

```
NEURAL/
├── backend/
│   ├── target/
│   │   └── neural-backend-1.0.0.jar  ✅ 必需
│   └── Dockerfile             ✅ 必需
├── frontend/
│   └── dist/                          ✅ 必需（整个目录）
├── nginx/
│   └── default.conf                   ✅ 必需
├── data/
│   └── uploads/
├── docker-compose.yml
├── .env                               ✅ 必需（根据模板创建）
```

## ⚙️ 环境变量配置

编辑 `.env` 文件：

```bash
# 数据库配置
DB_HOST=host.docker.internal    # 使用 Docker 容器名称连接数据库（推荐）
DB_PORT=5432
DB_NAME=neural_db
DB_USER=neural_user
DB_PASS=强密码123456           # ⚠️ 修改为强密码

# 管理员密码
ROOT_PASSWORD=admin123456        # ⚠️ 修改为强密码
```

## 启动容器

```bash
docker compose up -d --build
```
