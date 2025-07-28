# OCR模块多环境配置说明

## 概述

OCR模块支持多环境配置，通过Spring Profile机制实现不同环境的配置隔离。

## 支持的环境

- **默认环境**: 开发环境配置（application.yml）
- **test**: 测试环境配置（application-test.yml）
- **t2**: T2环境配置（application-t2.yml）
- **uat**: UAT环境配置（application-uat.yml）
- **prod**: 生产环境配置（application-prod.yml）

## 配置文件说明

### 主要配置项

每个环境配置文件都包含以下主要配置：

1. **服务配置**
   - 服务端口：8081
   - 上下文路径：/ocr

2. **数据库配置**
   - 数据库连接地址
   - 用户名和密码
   - 连接池配置

3. **Redis配置**
   - Redis Sentinel集群配置
   - 连接池配置

4. **Nacos配置**
   - Nacos注册中心地址
   - 认证用户名和密码

5. **Dubbo配置**
   - 服务注册与发现
   - 超时和重试配置

6. **OCR配置**
   - 支持百度OCR和AWS OCR
   - 各环境独立的API密钥配置

7. **日志配置**
   - 不同环境的日志级别
   - 生产环境支持文件日志

### 环境特定配置

#### 开发环境（默认）
- 详细的调试日志
- 本地数据库和Redis配置

#### 测试环境（test）
- 测试数据库和Redis
- 调试级别日志
- 测试环境的OCR API密钥

#### T2环境（t2）
- T2环境专用的基础设施配置
- 信息级别日志

#### UAT环境（uat）
- UAT环境的数据库和Redis配置
- 信息级别日志
- UAT环境的OCR API密钥

#### 生产环境（prod）
- 生产级别的性能配置
- 错误级别日志
- 文件日志输出
- 环境变量方式配置敏感信息
- 更大的连接池和超时配置

## 使用方法

### 1. 启动时指定Profile

```bash
# 启动测试环境
java -jar yt-base-ocr.jar --spring.profiles.active=test

# 启动UAT环境
java -jar yt-base-ocr.jar --spring.profiles.active=uat

# 启动生产环境
java -jar yt-base-ocr.jar --spring.profiles.active=prod
```

### 2. Maven启动时指定Profile

```bash
# 启动测试环境
mvn spring-boot:run -Dspring-boot.run.profiles=test

# 启动T2环境
mvn spring-boot:run -Dspring-boot.run.profiles=t2
```

### 3. 环境变量方式

```bash
export SPRING_PROFILES_ACTIVE=prod
java -jar yt-base-ocr.jar
```

### 4. application.properties中指定

```properties
spring.profiles.active=uat
```

## 生产环境注意事项

### 环境变量配置

生产环境使用环境变量配置敏感信息：

```bash
# 百度OCR配置
export BAIDU_OCR_APP_ID=your-app-id
export BAIDU_OCR_API_KEY=your-api-key
export BAIDU_OCR_SECRET_KEY=your-secret-key

# AWS OCR配置
export AWS_OCR_ACCESS_KEY=your-access-key
export AWS_OCR_SECRET_KEY=your-secret-key
export AWS_OCR_REGION=us-east-1
```

### 日志配置

生产环境日志输出到文件：
- 日志文件路径：`/var/log/yt-base-ocr/application.log`
- 单个文件最大：100MB
- 保留历史：30天

### 性能配置

生产环境针对性能进行了优化：
- Redis连接池：最大16个连接
- Dubbo超时：5秒
- 重试次数：1次
- 关闭SQL日志输出

## 配置验证

启动后可以通过以下方式验证配置：

1. **检查服务状态**
   ```bash
   curl http://localhost:8081/ocr/management/status
   ```

2. **检查OCR提供商**
   ```bash
   curl http://localhost:8081/ocr/management/providers
   ```

3. **查看应用日志**
   - 确认使用的Profile
   - 确认Nacos连接状态
   - 确认数据库连接状态

## 故障排查

### 常见问题

1. **Nacos连接失败**
   - 检查nacos.address配置
   - 检查nacos.username和nacos.password
   - 确认Nacos服务是否启动

2. **数据库连接失败**
   - 检查数据库地址和端口
   - 检查用户名和密码
   - 确认数据库服务是否启动

3. **Redis连接失败**
   - 检查Redis Sentinel配置
   - 检查Redis密码配置
   - 确认Redis服务是否启动

4. **OCR服务不可用**
   - 检查OCR API密钥配置
   - 检查网络连接
   - 查看OCR服务商状态