# 简化版 Dockerfile - 用于已打包好的 JAR 文件
# 使用场景：本地已经构建好 JAR，直接上传到服务器部署

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 从 target 目录复制已构建好的 JAR 文件
COPY target/*.jar app.jar

# 创建上传目录
RUN mkdir -p /app/uploads

# 暴露端口
EXPOSE 8080

# 运行应用
ENTRYPOINT ["java", "-jar", "app.jar"]

