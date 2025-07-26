# Yt-Base 易通基础服务平台

## 项目简介

Yt-Base是一个基于SpringBoot3+Dubbo3的微服务架构基础服务平台，为公司其他业务系统提供统一的基础服务支持。

## 技术栈

- **框架**: Spring Boot 3.2.0
- **微服务**: Dubbo 3.2.8
- **注册中心**: Nacos 2.3.0
- **数据库**: MySQL 8.0
- **缓存**: Redis
- **对象存储**: MinIO / 阿里云OSS
- **构建工具**: Maven 3.8+
- **JDK版本**: JDK 17

## 服务模块

### 1. yt-base-common
公共模块，包含通用工具类、异常处理、统一返回结果等。

### 2. yt-base-api
API接口定义模块，包含所有服务的接口定义和DTO。

### 3. yt-base-gateway
API网关，统一入口，负责路由转发、负载均衡等。
- 端口: 8080

### 4. yt-base-ocr
OCR识别服务，提供文字识别功能。
- 端口: 8081
- 支持身份证、营业执照、银行卡、通用文字识别

### 5. yt-base-push
推送服务，提供邮件和短信发送功能。
- 端口: 8082
- 支持邮件发送、短信发送、批量推送

### 6. yt-base-storage
对象存储服务，提供文件上传、下载、管理功能。
- 端口: 8083
- 支持MinIO、阿里云OSS

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+
- Nacos 2.3.0+
- MinIO (可选)

### 启动步骤

1. **启动基础服务**
   ```bash
   # 启动MySQL
   # 启动Redis
   # 启动Nacos
   docker run -d --name nacos -p 8848:8848 -e MODE=standalone nacos/nacos-server:v2.3.0
   
   # 启动MinIO (可选)
   docker run -d --name minio -p 9000:9000 -p 9001:9001 \
     -e MINIO_ROOT_USER=minioadmin \
     -e MINIO_ROOT_PASSWORD=minioadmin \
     minio/minio server /data --console-address ":9001"
   ```

2. **创建数据库**
   ```sql
   CREATE DATABASE yt_base_ocr DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   CREATE DATABASE yt_base_push DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   CREATE DATABASE yt_base_storage DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

3. **编译项目**
   ```bash
   mvn clean compile
   ```

4. **启动服务**
   ```bash
   # 启动网关
   cd yt-base-gateway && mvn spring-boot:run
   
   # 启动OCR服务
   cd yt-base-ocr && mvn spring-boot:run
   
   # 启动推送服务
   cd yt-base-push && mvn spring-boot:run
   
   # 启动存储服务
   cd yt-base-storage && mvn spring-boot:run
   ```

## API文档

### OCR服务 API

#### 通用文字识别
```http
POST /api/ocr/recognize/general
Content-Type: application/x-www-form-urlencoded

imageBase64=base64编码的图片内容
```

#### 身份证识别
```http
POST /api/ocr/recognize/idcard
Content-Type: application/x-www-form-urlencoded

imageBase64=base64编码的图片内容
```

### 推送服务 API

#### 发送邮件
```http
POST /api/push/email/send
Content-Type: application/json

{
  "toEmails": ["user@example.com"],
  "subject": "邮件主题",
  "content": "邮件内容",
  "isHtml": false
}
```

#### 发送短信
```http
POST /api/push/sms/send
Content-Type: application/json

{
  "phoneNumbers": ["13800138000"],
  "content": "短信内容"
}
```

### 存储服务 API

#### 上传文件
```http
POST /api/storage/upload
Content-Type: application/json

{
  "fileName": "test.txt",
  "fileContent": "base64编码的文件内容",
  "contentType": "text/plain"
}
```

## 配置说明

### 数据库配置
修改各服务的 `application.yml` 文件中的数据库连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/数据库名
    username: 用户名
    password: 密码
```

### 第三方服务配置

#### 百度OCR配置
```yaml
baidu:
  ocr:
    app-id: your-app-id
    api-key: your-api-key
    secret-key: your-secret-key
```

#### 邮件服务配置
```yaml
spring:
  mail:
    host: smtp.qq.com
    username: your-email@qq.com
    password: your-email-password
```

#### MinIO配置
```yaml
minio:
  endpoint: http://localhost:9000
  access-key: minioadmin
  secret-key: minioadmin
```

## 开发指南

### 添加新服务

1. 在根目录 `pom.xml` 中添加新模块
2. 创建新模块目录和 `pom.xml`
3. 实现服务接口
4. 在网关中配置路由

### 代码规范

- 使用统一的返回结果格式 `Result<T>`
- 使用统一的异常处理 `BusinessException`
- 遵循RESTful API设计规范
- 使用Lombok简化代码

## 部署说明

### Docker部署

每个服务都可以打包成Docker镜像：

```dockerfile
FROM openjdk:17-jre-slim
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Kubernetes部署

提供完整的K8s部署配置文件，支持服务发现、负载均衡、健康检查等。

## 监控与运维

- 集成Spring Boot Actuator健康检查
- 支持Prometheus监控指标
- 统一日志格式，便于日志收集分析

## 联系方式

- 项目负责人: [负责人姓名]
- 邮箱: [邮箱地址]
- 文档地址: [文档地址]