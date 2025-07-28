package com.yitong.base.ocr.strategy.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.yitong.base.api.ocr.dto.OcrRecognitionRequest;
import com.yitong.base.api.ocr.dto.OcrRecognitionResponse;
import com.yitong.base.common.exception.BusinessException;
import com.yitong.base.common.result.ResultCode;
import com.yitong.base.ocr.strategy.OcrStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * 本地自建OCR策略实现
 * 支持调用本地部署的OCR服务
 */
@Slf4j
@Component
public class LocalOcrStrategy implements OcrStrategy {
    
    @Value("${ocr.local.endpoint:}")
    private String endpoint;
    
    @Value("${ocr.local.api-key:}")
    private String apiKey;
    
    @Value("${ocr.local.timeout:30000}")
    private int timeout;
    
    @Value("${ocr.local.enabled:false}")
    private boolean enabled;
    
    private boolean initialized = false;
    private RestTemplate restTemplate;
    
    @Override
    public String getStrategyName() {
        return "local";
    }
    
    @PostConstruct
    @Override
    public void initialize() {
        if (enabled && StringUtils.hasText(endpoint)) {
            try {
                restTemplate = new RestTemplate();
                initialized = true;
                log.info("本地OCR策略初始化成功，服务端点: {}", endpoint);
            } catch (Exception e) {
                log.error("本地OCR策略初始化失败", e);
                initialized = false;
            }
        } else {
            log.warn("本地OCR策略未启用或配置不完整，endpoint: {}, enabled: {}", endpoint, enabled);
        }
    }
    
    @Override
    public boolean isAvailable() {
        return initialized && enabled && StringUtils.hasText(endpoint);
    }
    
    /**
     * 通用识别方法
     * @param imageBytes 图片字节数组
     * @param returnPosition 是否返回位置信息
     * @return 识别结果
     */
    private OcrRecognitionResponse recognize(byte[] imageBytes, Boolean returnPosition) {
        if (!isAvailable()) {
            throw new BusinessException(ResultCode.ERROR.getCode(), "本地OCR服务不可用");
        }
        
        long startTime = System.currentTimeMillis();
        
        try {
            // 将字节数组转换为Base64
            String imageBase64 = java.util.Base64.getEncoder().encodeToString(imageBytes);
            
            // 构建请求
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("image", imageBase64);
            requestBody.put("returnPosition", returnPosition);
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (StringUtils.hasText(apiKey)) {
                headers.set("Authorization", "Bearer " + apiKey);
            }
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            // 发送请求
            ResponseEntity<String> response = restTemplate.postForEntity(
                endpoint , entity, String.class);
            
            long duration = System.currentTimeMillis() - startTime;
            
            if (response.getStatusCode() == HttpStatus.OK) {
                return parseOcrResult(response.getBody(), duration);
            } else {
                throw new BusinessException(ResultCode.ERROR.getCode(), 
                    "本地OCR服务返回错误状态: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("本地OCR识别失败，耗时: {}ms", duration, e);
            throw new BusinessException(ResultCode.ERROR.getCode(), "本地OCR识别失败: " + e.getMessage());
        }
    }
    
    @Override
    public OcrRecognitionResponse recognizeIdCard(byte[] imageBytes) {
        // 身份证识别使用通用识别接口
        return recognize(imageBytes, false);
    }
    
    @Override
    public OcrRecognitionResponse recognizeBankCard(byte[] imageBytes) {
        // 银行卡识别使用通用识别接口
        return recognize(imageBytes, false);
    }
    
    @Override
    public OcrRecognitionResponse recognizeBusinessLicense(byte[] imageBytes) {
        // 营业执照识别使用通用识别接口
        return recognize(imageBytes, false);
    }
    
    @Override
    public OcrRecognitionResponse recognizeGeneral(byte[] imageBytes, Boolean returnPosition) {
        // 通用文字识别
        return recognize(imageBytes, returnPosition);
    }
    
    /**
     * 解析OCR识别结果
     * @param responseBody 响应体
     * @param duration 耗时
     * @return OCR识别响应
     */
    private OcrRecognitionResponse parseOcrResult(String responseBody, long duration) {
        OcrRecognitionResponse response = new OcrRecognitionResponse();
        response.setDuration(duration);
        
        try {
            JSONObject result = JSON.parseObject(responseBody);
            
            if (result.getInteger("errorCode") != 0) {
                // 处理错误响应
                String errorMsg = result.getString("errorMsg");
                log.error("本地OCR服务返回错误: {}", errorMsg);
                response.setSuccess(false);
                response.setMessage(errorMsg);
            } else {
                // 处理成功响应
                response.setSuccess(true);
                response.setMessage("识别成功");
                
                // 提取识别文本
                JSONArray resultArray = (JSONArray)result.getByPath("result.texts");
                if (resultArray != null) {
                    StringBuilder textBuilder = new StringBuilder();
                    resultArray.forEach(item -> {
                        JSONObject itemObj = (JSONObject) item;
                        String text = itemObj.getString("text");
                        if (text != null) {
                            textBuilder.append(text).append(" ");
                        }
                    });
                    response.setText(textBuilder.toString().trim());
                }
                
                // 设置原始结果
                response.setRawResult(responseBody);
            }
            
        } catch (Exception e) {
            log.error("解析本地OCR结果失败", e);
            response.setSuccess(false);
            response.setMessage("解析识别结果失败: " + e.getMessage());
        }
        
        return response;
    }
}