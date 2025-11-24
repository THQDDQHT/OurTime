# OurTime (时光博物馆)

一个专属的私密数字空间，用于记录和保存生活中的美好瞬间。

## 技术栈

### 后端
- Spring Boot 3.x
- Java 21
- PostgreSQL 15
- JWT 认证
- Docker

### 前端
- Vue 3 + TypeScript
- Vite
- Naive UI
- Tailwind CSS
- Pinia
- Axios

## 项目结构

```
our-time/
├── backend/          # Spring Boot 后端
├── frontend/         # Vue 3 前端
├── nginx/            # Nginx 配置
├── docker-compose.yml
├── .env.example      # 环境变量示例
└── docs/             # 文档
```

## 快速开始

### 1. 环境准备

- Docker & Docker Compose
- Node.js 18+ (用于前端开发)
- Maven 3.9+ (用于后端开发，可选)

### 2. 配置环境变量

复制 `.env.example` 为 `.env` 并修改配置：

```bash
cp .env.example .env
```

编辑 `.env` 文件，设置：
- `DB_USER`: 数据库用户名
- `DB_PASS`: 数据库密码
- `JWT_SECRET`: JWT 密钥（生产环境请修改）
- `AUTH_SECRET`: 认证暗号（登录密码）

### 3. 构建前端

```bash
cd frontend
npm install
npm run build
```

### 4. 启动服务

```bash
docker-compose up -d --build
```

服务将在以下地址启动：
- 前端: http://localhost
- 后端 API: http://localhost/api/v1

### 5. 访问应用

打开浏览器访问 http://localhost，使用配置的 `AUTH_SECRET` 作为暗号登录。

## 开发模式

### 后端开发

```bash
cd backend
mvn spring-boot:run
```

后端将在 http://localhost:8080 启动。

### 前端开发

```bash
cd frontend
npm install
npm run dev
```

前端将在 http://localhost:3000 启动，并代理 API 请求到后端。

## 功能特性

- ✅ JWT 认证登录
- ✅ 瞬间发布（文字、图片、日期、地点、相册）
- ✅ 时间轴视图
- ✅ 照片墙视图（瀑布流）
- ✅ 相册管理（CRUD）
- ✅ 图片上传与压缩
- ✅ 响应式设计

## API 接口

### 认证
- `POST /api/v1/auth/login` - 登录

### 上传
- `POST /api/v1/upload` - 上传图片

### 瞬间
- `POST /api/v1/moments` - 创建瞬间
- `GET /api/v1/moments` - 获取瞬间列表（分页）
- `GET /api/v1/moments/{id}` - 获取瞬间详情
- `DELETE /api/v1/moments/{id}` - 删除瞬间

### 相册
- `GET /api/v1/albums` - 获取相册列表
- `POST /api/v1/albums` - 创建相册
- `PUT /api/v1/albums/{id}` - 更新相册
- `DELETE /api/v1/albums/{id}` - 删除相册

## 数据备份

数据存储在 `./data` 目录：
- `./data/postgres/` - PostgreSQL 数据
- `./data/uploads/` - 上传的图片文件

建议定期备份该目录。

## 许可证

MIT

