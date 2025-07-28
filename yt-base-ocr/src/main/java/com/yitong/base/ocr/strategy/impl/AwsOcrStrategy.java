package com.yitong.base.ocr.strategy.impl;

import com.yitong.base.api.ocr.dto.OcrRecognitionResponse;
import com.yitong.base.common.exception.BusinessException;
import com.yitong.base.common.result.ResultCode;
import com.yitong.base.ocr.strategy.OcrStrategy;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.textract.model.BlockType;
import software.amazon.awssdk.services.textract.model.DetectDocumentTextRequest;
import software.amazon.awssdk.services.textract.model.DetectDocumentTextResponse;
import software.amazon.awssdk.services.textract.model.Document;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * AWS OCR策略实现
 * 使用AWS Textract服务进行OCR识别
 */
@Slf4j
@Component
public class AwsOcrStrategy implements OcrStrategy {
    
    @Value("${ocr.aws.access-key:}")
    private String accessKey;
    
    @Value("${ocr.aws.secret-key:}")
    private String secretKey;
    
    @Value("${ocr.aws.region:us-east-1}")
    private String region;
    
    private boolean initialized = false;

    private TextractClient textractClient;
    
    @Override
    public String getStrategyName() {
        return "aws";
    }
    
    @PostConstruct
    @Override
    public void initialize() {
        if (accessKey != null && !accessKey.isEmpty() && secretKey != null && !secretKey.isEmpty()) {
            // 这里应该初始化AWS Textract客户端
            textractClient = TextractClient.builder()
                .region(Region.of(region))
                .credentialsProvider(() -> AwsBasicCredentials.create(accessKey, secretKey))
                .overrideConfiguration(b -> b.retryPolicy(RetryMode.STANDARD))
                .build();
            // 由于需要AWS SDK依赖，这里先做模拟实现
            initialized = true;
            log.info("AWS OCR客户端初始化成功，区域: {}", region);
        } else {
            log.warn("AWS OCR配置不完整，将使用模拟数据");
        }
    }
    
    @Override
    public boolean isAvailable() {
        return initialized;
    }
    
    @Override
    public OcrRecognitionResponse recognizeIdCard(byte[] imageBytes) {
        long startTime = System.currentTimeMillis();
        
        try {
            // 模拟AWS Textract身份证识别
            OcrRecognitionResponse response = createMockResponse(
                "AWS身份证识别结果\n姓名: 张三\n身份证号: 123456789012345678",
                System.currentTimeMillis() - startTime
            );
            
            log.info("AWS OCR身份证识别完成，耗时: {}ms", response.getDuration());
            return response;
            
        } catch (Exception e) {
            log.error("AWS OCR身份证识别失败", e);
            throw new BusinessException(ResultCode.OCR_RECOGNITION_FAILED);
        }
    }
    
    @Override
    public OcrRecognitionResponse recognizeBusinessLicense(byte[] imageBytes) {
        long startTime = System.currentTimeMillis();
        
        try {
            // 模拟AWS Textract营业执照识别
            OcrRecognitionResponse response = createMockResponse(
                "AWS营业执照识别结果\n公司名称: 示例科技有限公司\n统一社会信用代码: 91110000000000000X",
                System.currentTimeMillis() - startTime
            );
            
            log.info("AWS OCR营业执照识别完成，耗时: {}ms", response.getDuration());
            return response;
            
        } catch (Exception e) {
            log.error("AWS OCR营业执照识别失败", e);
            throw new BusinessException(ResultCode.OCR_RECOGNITION_FAILED);
        }
    }
    
    @Override
    public OcrRecognitionResponse recognizeBankCard(byte[] imageBytes) {
        long startTime = System.currentTimeMillis();
        
        try {
            // 模拟AWS Textract银行卡识别
            OcrRecognitionResponse response = createMockResponse(
                "AWS银行卡识别结果\n卡号: 6222 **** **** 1234\n银行: 中国工商银行",
                System.currentTimeMillis() - startTime
            );
            
            log.info("AWS OCR银行卡识别完成，耗时: {}ms", response.getDuration());
            return response;
            
        } catch (Exception e) {
            log.error("AWS OCR银行卡识别失败", e);
            throw new BusinessException(ResultCode.OCR_RECOGNITION_FAILED);
        }
    }
    
    @Override
    public OcrRecognitionResponse recognizeGeneral(byte[] imageBytes, Boolean returnPosition) {
        long startTime = System.currentTimeMillis();
        
        try {
            SdkBytes bytes = SdkBytes.fromByteArray(imageBytes);
            // 构建请求对象
            DetectDocumentTextRequest request = DetectDocumentTextRequest.builder()
                .document(Document.builder().bytes(bytes).build())
                .build();
            DetectDocumentTextResponse ret = textractClient.detectDocumentText(request);
            OcrRecognitionResponse response = parseOcrResult(ret, System.currentTimeMillis() - startTime);
            log.info("AWS OCR通用文字识别完成，耗时: {}ms", response.getDuration());
            return response;
            
        } catch (Exception e) {
            log.error("AWS OCR通用文字识别失败", e);
            throw new BusinessException(ResultCode.OCR_RECOGNITION_FAILED);
        }
    }

    /**
     * 解析OCR识别结果
     * @param responseBody 响应体
     * @param duration 耗时
     * @return OCR识别响应
     */
    private OcrRecognitionResponse parseOcrResult(DetectDocumentTextResponse responseBody, long duration) {
        OcrRecognitionResponse response = new OcrRecognitionResponse();
        response.setDuration(duration);
        
        try {
            if (!responseBody.sdkHttpResponse().isSuccessful()) {
                // 处理错误响应
                String errorMsg = responseBody.sdkHttpResponse().statusText().orElse("未知异常");
                log.error("aws OCR服务返回错误: {}", errorMsg);
                response.setSuccess(false);
                response.setMessage(errorMsg);
            } else {
                // 处理成功响应
                response.setSuccess(true);
                response.setMessage("识别成功");
                
                // 提取识别文本
                if (responseBody.hasBlocks()) {
                    StringBuilder textBuilder = new StringBuilder();
                    responseBody.blocks().forEach(block -> {
                        if (block.blockType().equals(BlockType.LINE)) {
                            textBuilder.append(block.text()).append(" ");
                        }
                    });
                    response.setText(textBuilder.toString().trim());
                }
                
                // 设置原始结果
                response.setRawResult(null);
            }
            
        } catch (Exception e) {
            log.error("解析本地OCR结果失败", e);
            response.setSuccess(false);
            response.setMessage("解析识别结果失败: " + e.getMessage());
        }
        
        return response;
    }
    
    private OcrRecognitionResponse createMockResponse(String text, long duration) {
        OcrRecognitionResponse response = new OcrRecognitionResponse();
        response.setText(text);
        response.setDuration(duration);
        response.setConfidence(0.92); // AWS通常有较高的识别准确率
        
        // 创建文本块
        List<OcrRecognitionResponse.OcrTextBlock> textBlocks = new ArrayList<>();
        String[] lines = text.split("\n");
        
        for (int i = 0; i < lines.length; i++) {
            OcrRecognitionResponse.OcrTextBlock textBlock = new OcrRecognitionResponse.OcrTextBlock();
            textBlock.setText(lines[i]);
            textBlock.setConfidence(0.92);
            
            // 模拟位置信息
            OcrRecognitionResponse.OcrTextBlock.Position position = 
                    new OcrRecognitionResponse.OcrTextBlock.Position();
            position.setX(50);
            position.setY(50 + i * 30);
            position.setWidth(300);
            position.setHeight(25);
            textBlock.setPosition(position);
            
            textBlocks.add(textBlock);
        }
        
        response.setTextBlocks(textBlocks);
        return response;
    }
}