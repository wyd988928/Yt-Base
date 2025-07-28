package com.yitong.base.ocr.strategy.impl;

import com.baidu.aip.ocr.AipOcr;
import com.yitong.base.api.ocr.dto.OcrRecognitionResponse;
import com.yitong.base.common.exception.BusinessException;
import com.yitong.base.common.result.ResultCode;
import com.yitong.base.ocr.strategy.OcrStrategy;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 百度OCR策略实现
 */
@Slf4j
@Component
public class BaiduOcrStrategy implements OcrStrategy {
    
    @Value("${ocr.baidu.app-id:}")
    private String appId;
    
    @Value("${ocr.baidu.api-key:}")
    private String apiKey;
    
    @Value("${ocr.baidu.secret-key:}")
    private String secretKey;
    
    private AipOcr client;
    
    @Override
    public String getStrategyName() {
        return "baidu";
    }
    
    @PostConstruct
    @Override
    public void initialize() {
        if (appId != null && !appId.isEmpty() && apiKey != null && !apiKey.isEmpty()) {
            client = new AipOcr(appId, apiKey, secretKey);
            // 设置网络连接参数
            client.setConnectionTimeoutInMillis(2000);
            client.setSocketTimeoutInMillis(60000);
            log.info("百度OCR客户端初始化成功");
        } else {
            log.warn("百度OCR配置不完整，将使用模拟数据");
        }
    }
    
    @Override
    public boolean isAvailable() {
        return client != null;
    }
    
    @Override
    public OcrRecognitionResponse recognizeIdCard(byte[] imageBytes) {
        long startTime = System.currentTimeMillis();
        
        try {
            JSONObject result;
            if (client != null) {
                HashMap<String, String> options = new HashMap<>();
                options.put("detect_direction", "true");
                options.put("detect_risk", "false");
                result = client.idcard(imageBytes, "front", options);
            } else {
                result = mockOcrResult("身份证识别结果");
            }
            
            return parseOcrResult(result, System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            log.error("百度OCR身份证识别失败", e);
            throw new BusinessException(ResultCode.OCR_RECOGNITION_FAILED);
        }
    }
    
    @Override
    public OcrRecognitionResponse recognizeBusinessLicense(byte[] imageBytes) {
        long startTime = System.currentTimeMillis();
        
        try {
            JSONObject result;
            if (client != null) {
                result = client.businessLicense(imageBytes, new HashMap<>());
            } else {
                result = mockOcrResult("营业执照识别结果");
            }
            
            return parseOcrResult(result, System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            log.error("百度OCR营业执照识别失败", e);
            throw new BusinessException(ResultCode.OCR_RECOGNITION_FAILED);
        }
    }
    
    @Override
    public OcrRecognitionResponse recognizeBankCard(byte[] imageBytes) {
        long startTime = System.currentTimeMillis();
        
        try {
            JSONObject result;
            if (client != null) {
                result = client.bankcard(imageBytes, new HashMap<>());
            } else {
                result = mockOcrResult("银行卡识别结果");
            }
            
            return parseOcrResult(result, System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            log.error("百度OCR银行卡识别失败", e);
            throw new BusinessException(ResultCode.OCR_RECOGNITION_FAILED);
        }
    }
    
    @Override
    public OcrRecognitionResponse recognizeGeneral(byte[] imageBytes, Boolean returnPosition) {
        long startTime = System.currentTimeMillis();
        
        try {
            JSONObject result;
            if (client != null) {
                HashMap<String, String> options = new HashMap<>();
                options.put("recognize_granularity", "big");
                options.put("language_type", "CHN_ENG");
                options.put("detect_direction", "true");
                options.put("detect_language", "true");
                options.put("vertexes_location", returnPosition ? "true" : "false");
                result = client.basicGeneral(imageBytes, options);
            } else {
                result = mockOcrResult("通用文字识别结果");
            }
            
            return parseOcrResult(result, System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            log.error("百度OCR通用文字识别失败", e);
            throw new BusinessException(ResultCode.OCR_RECOGNITION_FAILED);
        }
    }
    
    private JSONObject mockOcrResult(String text) {
        JSONObject result = new JSONObject();
        result.put("words_result_num", 1);
        
        JSONArray wordsResult = new JSONArray();
        JSONObject wordItem = new JSONObject();
        wordItem.put("words", text);
        
        JSONObject location = new JSONObject();
        location.put("left", 100);
        location.put("top", 100);
        location.put("width", 200);
        location.put("height", 50);
        wordItem.put("location", location);
        
        wordsResult.put(wordItem);
        result.put("words_result", wordsResult);
        
        return result;
    }
    
    private OcrRecognitionResponse parseOcrResult(JSONObject result, long duration) {
        OcrRecognitionResponse response = new OcrRecognitionResponse();
        response.setDuration(duration);
        
        if (result.has("error_code")) {
            throw new BusinessException(ResultCode.OCR_RECOGNITION_FAILED.getCode(), 
                    "百度OCR识别失败：" + result.optString("error_msg"));
        }
        
        StringBuilder textBuilder = new StringBuilder();
        List<OcrRecognitionResponse.OcrTextBlock> textBlocks = new ArrayList<>();
        
        if (result.has("words_result")) {
            JSONArray wordsResult = result.getJSONArray("words_result");
            for (int i = 0; i < wordsResult.length(); i++) {
                JSONObject wordItem = wordsResult.getJSONObject(i);
                String words = wordItem.optString("words");
                textBuilder.append(words).append("\n");
                
                OcrRecognitionResponse.OcrTextBlock textBlock = new OcrRecognitionResponse.OcrTextBlock();
                textBlock.setText(words);
                textBlock.setConfidence(wordItem.optDouble("probability", 0.95));
                
                if (wordItem.has("location")) {
                    JSONObject location = wordItem.getJSONObject("location");
                    OcrRecognitionResponse.OcrTextBlock.Position position = 
                            new OcrRecognitionResponse.OcrTextBlock.Position();
                    position.setX(location.optInt("left"));
                    position.setY(location.optInt("top"));
                    position.setWidth(location.optInt("width"));
                    position.setHeight(location.optInt("height"));
                    textBlock.setPosition(position);
                }
                
                textBlocks.add(textBlock);
            }
        }
        
        response.setText(textBuilder.toString().trim());
        response.setTextBlocks(textBlocks);
        response.setConfidence(0.95);
        
        return response;
    }
}