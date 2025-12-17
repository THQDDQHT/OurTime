# Neural Backend (神经元后端) 🧠

> Neural 项目的后端服务，基于 Spring Boot 构建的数字记忆中枢 API 服务。

## 📋 目录

- [技术栈](#技术栈)
- [环境要求](#环境要求)
- [快速开始](#快速开始)
- [配置说明](#配置说明)
- [API 文档](#api-文档)
- [开发指南](#开发指南)
- [部署说明](#部署说明)
- [常见问题](#常见问题)

## 🛠️ 技术栈

- **框架**: Spring Boot 3.2.0
- **语言**: Java 21
- **构建工具**: Maven
- **数据库**: PostgreSQL 15
- **ORM**: Spring Data JPA + Hibernate
- **安全**: JWT (JSON Web Token)
- **工具库**: 
  - Lombok (简化代码)
  - Hutool (工具类库)
  - Spring Security Crypto (密码加密)

## 📦 环境要求

### 必需环境

- **JDK**: 21 或更高版本
- **Maven**: 3.6+ 
- **PostgreSQL**: 15 或更高版本
- **内存**: 至少 512MB 可用内存

### 推荐 IDE

- IntelliJ IDEA (推荐)
- Eclipse
- VS Code (需安装 Java 扩展)

## 🚀 快速开始

### 1. 克隆项目

```bash
cd backend
```

### 2. 配置数据库

确保 PostgreSQL 已安装并运行，然后创建数据库：

```sql
CREATE DATABASE neural_db;
CREATE USER neural_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE neural_db TO neural_user;
```

### 3. 配置环境变量

复制环境变量模板（如果存在）或直接编辑 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/neural_db
    username: neural_user
    password: your_password

jwt:
  secret: your_jwt_secret_key_at_least_32_characters_long
  expiration: 86400000  # 24小时

app:
  upload-path: ./uploads
  root-password: your_root_password
```

### 4. 构建项目

```bash
# 清理并编译
mvn clean compile

# 打包（跳过测试）
mvn clean package -DskipTests
```

### 5. 运行项目

#### 方式一：使用 Maven 运行

```bash
mvn spring-boot:run
```

#### 方式二：运行 JAR 文件

```bash
# 先打包
mvn clean package -DskipTests

# 运行 JAR
java -jar target/neural-backend-1.0.0.jar
```

#### 方式三：使用 IDE

在 IntelliJ IDEA 中：
1. 右键点击 `NeuralApplication.java`
2. 选择 `Run 'NeuralApplication'`

### 6. 验证运行

访问健康检查端点：

```bash
curl http://localhost:8080/api/v1/hello
```

应该返回：`Hello Love`

## ⚙️ 配置说明

### 数据库配置

在 `application.yml` 中配置：

```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:neural_db}
    username: ${SPRING_DATASOURCE_USERNAME:neural_user}
    password: ${SPRING_DATASOURCE_PASSWORD:your_password}
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: update  # 自动更新数据库表结构
    show-sql: true      # 显示 SQL 语句（开发环境）
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
```

### JWT 配置

```yaml
jwt:
  secret: ${JWT_SECRET:your_secret_key}  # 至少 32 个字符
  expiration: 86400000  # Token 过期时间（毫秒），默认 24 小时
```

### 文件上传配置

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 100MB      # 单个文件最大大小
      max-request-size: 100MB   # 请求最大大小

app:
  upload-path: ${APP_UPLOAD_PATH:./uploads}  # 文件上传目录
```

### 服务器配置

```yaml
server:
  port: 8080                    # 服务端口
  servlet:
    context-path: /api          # API 上下文路径
```

### 日志配置

日志配置文件位于 `src/main/resources/logback-spring.xml`，日志文件默认保存在 `logs/` 目录。

## 📚 API 文档

### 基础信息

- **Base URL**: `http://localhost:8080/api`
- **认证方式**: JWT Bearer Token
- **Content-Type**: `application/json`

### API 端点

#### 认证相关 (`/v1/auth`)

| 方法 | 路径 | 说明 | 需要认证 |
|------|------|------|----------|
| POST | `/v1/auth/register` | 用户注册 | ❌ |
| POST | `/v1/auth/login` | 用户登录 | ❌ |

#### 相册相关 (`/v1/albums`)

| 方法 | 路径 | 说明 | 需要认证 |
|------|------|------|----------|
| GET | `/v1/albums` | 获取所有相册 | ✅ |
| GET | `/v1/albums/{id}` | 获取相册详情 | ✅ |
| POST | `/v1/albums` | 创建相册 | ✅ |
| PUT | `/v1/albums/{id}` | 更新相册 | ✅ |
| DELETE | `/v1/albums/{id}` | 删除相册 | ✅ |

#### 记忆相关 (`/v1/moments`)

| 方法 | 路径 | 说明 | 需要认证 |
|------|------|------|----------|
| GET | `/v1/moments` | 获取记忆列表（分页） | ✅ |
| GET | `/v1/moments/{id}` | 获取记忆详情 | ✅ |
| POST | `/v1/moments` | 创建记忆 | ✅ |
| PUT | `/v1/moments/{id}` | 更新记忆 | ✅ |
| DELETE | `/v1/moments/{id}` | 删除记忆 | ✅ |

#### 音乐相关 (`/v1/music`)

| 方法 | 路径 | 说明 | 需要认证 |
|------|------|------|----------|
| GET | `/v1/music` | 获取音乐列表 | ✅ |
| POST | `/v1/music` | 创建音乐 | ✅ |
| DELETE | `/v1/music/{id}` | 删除音乐 | ✅ |

#### 文件上传 (`/v1/upload`)

| 方法 | 路径 | 说明 | 需要认证 |
|------|------|------|----------|
| POST | `/v1/upload` | 上传文件 | ✅ |

#### 静态资源 (`/uploads/**`)

| 方法 | 路径 | 说明 | 需要认证 |
|------|------|------|----------|
| GET | `/uploads/**` | 访问上传的文件 | ❌ |

### 请求示例

#### 用户登录

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "your_password"
  }'
```

响应：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": 1,
    "username": "admin",
    "role": "ROOT"
  }
}
```

#### 获取相册列表（需要认证）

```bash
curl -X GET http://localhost:8080/api/v1/albums \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 💻 开发指南

### 项目结构

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/neural/
│   │   │   ├── aspect/          # AOP 切面（日志等）
│   │   │   ├── config/          # 配置类
│   │   │   ├── controller/      # REST 控制器
│   │   │   ├── dto/             # 数据传输对象
│   │   │   ├── entity/          # 实体类（JPA）
│   │   │   ├── filter/          # 过滤器（JWT 认证等）
│   │   │   ├── repository/     # 数据访问层
│   │   │   ├── service/        # 业务逻辑层
│   │   │   ├── util/           # 工具类
│   │   │   └── NeuralApplication.java  # 主启动类
│   │   └── resources/
│   │       ├── application.yml  # 应用配置
│   │       └── logback-spring.xml  # 日志配置
│   └── test/                    # 测试代码
├── target/                      # 编译输出目录
├── uploads/                     # 文件上传目录
├── logs/                        # 日志文件目录
├── pom.xml                      # Maven 配置
└── Dockerfile                   # Docker 构建文件
```

### 开发流程

1. **创建新功能**
   - 在 `entity` 中定义实体类
   - 在 `repository` 中创建数据访问接口
   - 在 `service` 中实现业务逻辑
   - 在 `controller` 中创建 REST 端点
   - 在 `dto` 中定义请求/响应对象

2. **代码规范**
   - 使用 Lombok 简化代码
   - 遵循 RESTful API 设计规范
   - 使用 `@Valid` 进行参数验证
   - 统一使用 `ApiResponse` 包装响应

3. **测试**

```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=AlbumServiceTest
```

### 常用 Maven 命令

```bash
# 清理编译产物
mvn clean

# 编译项目
mvn compile

# 运行测试
mvn test

# 打包项目
mvn package

# 跳过测试打包
mvn package -DskipTests

# 运行应用
mvn spring-boot:run

# 查看依赖树
mvn dependency:tree
```

## 🐳 部署说明

### Docker 部署

#### 1. 构建 Docker 镜像

```bash
# 先打包 JAR
mvn clean package -DskipTests

# 构建镜像
docker build -t neural-backend:1.0.0 .
```

#### 2. 运行容器

```bash
docker run -d \
  --name neural-backend \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/neural_db \
  -e SPRING_DATASOURCE_USERNAME=neural_user \
  -e SPRING_DATASOURCE_PASSWORD=your_password \
  -e JWT_SECRET=your_jwt_secret \
  -e APP_UPLOAD_PATH=/app/uploads \
  -v $(pwd)/uploads:/app/uploads \
  neural-backend:1.0.0
```

### 使用 Docker Compose

参考项目根目录的 `docker-compose.yml` 文件。

### 生产环境部署

1. **修改配置**
   - 将 `ddl-auto` 改为 `validate` 或 `none`
   - 关闭 `show-sql`
   - 使用环境变量管理敏感信息

2. **性能优化**
   - 配置数据库连接池
   - 启用 JPA 二级缓存
   - 配置日志级别为 INFO 或 WARN

3. **安全建议**
   - 使用强密码
   - JWT Secret 至少 32 个字符
   - 启用 HTTPS
   - 配置防火墙规则

## ❓ 常见问题

### Q: 启动时提示数据库连接失败？

**A**: 检查以下几点：
1. PostgreSQL 服务是否运行
2. 数据库连接配置是否正确
3. 数据库用户权限是否足够
4. 防火墙是否开放 5432 端口

### Q: 文件上传失败？

**A**: 检查：
1. `uploads` 目录是否存在且有写权限
2. 文件大小是否超过配置的限制（默认 100MB）
3. 磁盘空间是否充足

### Q: JWT Token 验证失败？

**A**: 确保：
1. Token 未过期（默认 24 小时）
2. JWT Secret 配置一致
3. 请求头格式正确：`Authorization: Bearer <token>`

### Q: 如何查看日志？

**A**: 日志文件位于 `logs/` 目录：
- `info.log` - 信息日志
- `error.log` - 错误日志
- `info-YYYY-MM-DD.log` - 按日期归档的日志

### Q: 如何初始化管理员账户？

**A**: 应用启动时会自动创建 ROOT 用户，默认密码在 `application.yml` 的 `app.root-password` 中配置。首次登录后请立即修改密码。

## 📝 许可证

本项目为私有项目，仅供个人使用。

## 🤝 贡献

如有问题或建议，请提交 Issue 或 Pull Request。

---

**Happy Coding! 🚀**

