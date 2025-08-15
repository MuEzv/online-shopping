# 使用 OpenJDK 作为基础镜像
FROM openjdk:17-jdk-slim

# 设置工作目录
WORKDIR /app

# 将生成的 JAR 文件复制到容器中
#COPY target/OrderService-0.0.1-SNAPSHOT.jar app.jar
#CMD ["java", "-jar", "app.jar"]

# 暴露应用的端口（Spring Boot 应用监听的端口）
EXPOSE 8080

# 启动 Spring Boot 应用
ENTRYPOINT ["java", "-jar", "target/OrderService-0.0.1-SNAPSHOT.jar"]
#ENTRYPOINT ["java", "-jar", "app.jar"]
