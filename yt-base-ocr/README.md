# OCR服务模块

## 概述

OCR服务模块支持多个第三方OCR服务提供商，采用策略模式设计，可以通过配置文件灵活切换不同的OCR服务。

## 支持的OCR提供商

- **百度OCR** (baidu) - 百度智能云OCR服务
- **AWS OCR** (aws) - Amazon Textract服务
- **本地OCR** (local) - 本地自建OCR服务

## 配置说明

### 基本配置

在 `application.yml` 中配置OCR服务：

```yaml
ocr:
  # 默认OCR提供商 (baidu, aws, local)
  provider: baidu
  
  # 百度OCR配置
  baidu:
    app-id: your-app-id
    api-key: your-api-key
    secret-key: your-secret-key
  
  # AWS OCR配置
  aws:
    access-key: your-access-key
    secret-key: your-secret-key
    region: us-east-1
  
  # 本地自建OCR配置
  local:
    enabled: false
    endpoint: http://localhost:8082
    api-key: your-local-api-key
    timeout: 30000
```

### 多套部署配置

对于不同的部署环境，可以通过不同的配置文件指定不同的OCR提供商：

#### 生产环境 - 使用百度OCR
```yaml
# application-prod.yml
ocr:
  provider: baidu
  baidu:
    app-id: ${BAIDU_OCR_APP_ID}
    api-key: ${BAIDU_OCR_API_KEY}
    secret-key: ${BAIDU_OCR_SECRET_KEY}
```

#### 测试环境 - 使用AWS OCR
```yaml
# application-test.yml
ocr:
  provider: aws
  aws:
    access-key: ${AWS_ACCESS_KEY}
    secret-key: ${AWS_SECRET_KEY}
    region: us-west-2
```

#### 开发环境 - 使用本地OCR
```yaml
# application-dev.yml
ocr:
  provider: local
  local:
    enabled: true
    endpoint: ${LOCAL_OCR_ENDPOINT:http://localhost:8082}
    api-key: ${LOCAL_OCR_API_KEY:dev-api-key}
    timeout: ${LOCAL_OCR_TIMEOUT:30000}
```

## API接口

### OCR识别接口

所有OCR识别接口保持不变，系统会自动使用配置的默认提供商：

- `POST /ocr/recognize` - 通用识别接口
- `POST /ocr/idcard` - 身份证识别
- `POST /ocr/business-license` - 营业执照识别
- `POST /ocr/bank-card` - 银行卡识别
- `POST /ocr/general` - 通用文字识别

### 管理接口

新增的管理接口用于查看和管理OCR服务状态：

#### 获取OCR服务状态
```http
GET /ocr/management/status
```

响应示例：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "defaultProvider": "baidu",
    "availableProviders": ["baidu", "aws"],
    "providerStatus": {
      "baidu": true,
      "aws": false
    }
  }
}
```

#### 检查指定提供商状态
```http
GET /ocr/management/check/{provider}
```

#### 获取可用提供商列表
```http
GET /ocr/management/providers
```

## 部署指南

### 单一提供商部署

1. 选择一个OCR提供商（如百度）
2. 配置相应的认证信息
3. 设置 `ocr.provider` 为对应的提供商名称
4. 启动服务

### 多套服务部署

#### 方案一：不同环境使用不同提供商

```bash
# 生产环境 - 使用百度OCR
java -jar yt-base-ocr.jar --spring.profiles.active=prod

# 测试环境 - 使用AWS OCR
java -jar yt-base-ocr.jar --spring.profiles.active=test
```

#### 方案二：通过环境变量指定提供商

```bash
# 部署实例1 - 百度OCR
export OCR_PROVIDER=baidu
export BAIDU_OCR_APP_ID=your-app-id
export BAIDU_OCR_API_KEY=your-api-key
export BAIDU_OCR_SECRET_KEY=your-secret-key
java -jar yt-base-ocr.jar

# 部署实例2 - AWS OCR
export OCR_PROVIDER=aws
export AWS_ACCESS_KEY=your-access-key
export AWS_SECRET_KEY=your-secret-key
export AWS_REGION=us-east-1
java -jar yt-base-ocr.jar
```

对应的配置文件：
```yaml
ocr:
  provider: ${OCR_PROVIDER:baidu}
  baidu:
    app-id: ${BAIDU_OCR_APP_ID:}
    api-key: ${BAIDU_OCR_API_KEY:}
    secret-key: ${BAIDU_OCR_SECRET_KEY:}
  aws:
    access-key: ${AWS_ACCESS_KEY:}
    secret-key: ${AWS_SECRET_KEY:}
    region: ${AWS_REGION:us-east-1}
```

## 扩展新的OCR提供商

要添加新的OCR提供商，需要：

1. 实现 `OcrStrategy` 接口
2. 添加 `@Component` 注解
3. 在配置文件中添加相应的配置项
4. 重启服务

示例：

```java
@Component
public class TencentOcrStrategy implements OcrStrategy {
    
    @Value("${ocr.tencent.secret-id:}")
    private String secretId;
    
    @Value("${ocr.tencent.secret-key:}")
    private String secretKey;
    
    @Override
    public String getStrategyName() {
        return "tencent";
    }
    
    // 实现其他接口方法...
}
```

## 故障转移

系统具备自动故障转移功能：

1. 当配置的默认提供商不可用时，系统会自动尝试使用其他可用的提供商
2. 如果所有提供商都不可用，会抛出相应的异常
3. 可以通过管理接口实时查看各提供商的状态

## 注意事项

1. 确保至少配置一个OCR提供商的完整认证信息
2. 不同提供商的识别结果格式可能略有差异，但接口返回格式保持统一
3. 建议在生产环境中配置多个提供商以提高服务可用性
4. 定期检查各提供商的配额和费用使用情况