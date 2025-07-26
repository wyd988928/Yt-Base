package com.yitong.base.ocr.service.impl;

import com.baidu.aip.ocr.AipOcr;
import com.yitong.base.api.ocr.dto.OcrRecognitionRequest;
import com.yitong.base.api.ocr.dto.OcrRecognitionResponse;
import com.yitong.base.api.ocr.service.OcrService;
import com.yitong.base.common.exception.BusinessException;
import com.yitong.base.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

/**
 * OCR服务实现类
 */
@Slf4j
@Service
@DubboService
public class OcrServiceImpl implements OcrService {
    
    @Value("${baidu.ocr.app-id:}")
    private String appId;
    
    @Value("${baidu.ocr.api-key:}")
    private String apiKey;
    
    @Value("${baidu.ocr.secret-key:}")
    private String secretKey;
    
    private AipOcr client;
    
    @PostConstruct
    public void init() {
        if (appId != null && !appId.isEmpty()) {
            client = new AipOcr(appId, apiKey, secretKey);
            // 可选：设置网络连接参数
            client.setConnectionTimeoutInMillis(2000);
            client.setSocketTimeoutInMillis(60000);
        }
    }
    
    @Override
    public OcrRecognitionResponse recognize(OcrRecognitionRequest request) {
        long startTime = System.currentTimeMillis();
        
        try {
            byte[] imageBytes = Base64.getDecoder().decode(request.getImageBase64());
            
            JSONObject result;
            switch (request.getRecognitionType()) {
                case 1:
                    result = recognizeIdCardInternal(imageBytes);
                    break;
                case 2:
                    result = recognizeBusinessLicenseInternal(imageBytes);
                    break;
                case 3:
                    result = recognizeBankCardInternal(imageBytes);
                    break;
                case 4:
                    result = recognizeGeneralInternal(imageBytes, request.getReturnPosition());
                    break;
                default:
                    throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "不支持的识别类型");
            }
            
            return parseOcrResult(result, System.currentTimeMillis() - startTime);
            
        } catch (Exception e) {
            log.error("OCR识别失败", e);
            throw new BusinessException(ResultCode.OCR_RECOGNITION_FAILED);
        }
    }
    
    @Override
    public OcrRecognitionResponse recognizeIdCard(String imageBase64) {
        OcrRecognitionRequest request = new OcrRecognitionRequest();
        request.setImageBase64(imageBase64);
        request.setRecognitionType(1);
        return recognize(request);
    }
    
    @Override
    public OcrRecognitionResponse recognizeBusinessLicense(String imageBase64) {
        OcrRecognitionRequest request = new OcrRecognitionRequest();
        request.setImageBase64(imageBase64);
        request.setRecognitionType(2);
        return recognize(request);
    }
    
    @Override
    public OcrRecognitionResponse recognizeBankCard(String imageBase64) {
        OcrRecognitionRequest request = new OcrRecognitionRequest();
        request.setImageBase64(imageBase64);
        request.setRecognitionType(3);
        return recognize(request);
    }
    
    @Override
    public OcrRecognitionResponse recognizeGeneral(String imageBase64) {
        OcrRecognitionRequest request = new OcrRecognitionRequest();
        request.setImageBase64(imageBase64);
        request.setRecognitionType(4);
        return recognize(request);
    }
    
    private JSONObject recognizeIdCardInternal(byte[] imageBytes) {
        if (client == null) {
            return mockOcrResult("身份证识别结果");
        }
        HashMap<String, String> options = new HashMap<>();
        options.put("detect_direction", "true");
        options.put("detect_risk", "false");
        return client.idcard(imageBytes, "front", options);
    }
    
    private JSONObject recognizeBusinessLicenseInternal(byte[] imageBytes) {
        if (client == null) {
            return mockOcrResult("营业执照识别结果");
        }
        return client.businessLicense(imageBytes, new HashMap<>());
    }
    
    private JSONObject recognizeBankCardInternal(byte[] imageBytes) {
        if (client == null) {
            return mockOcrResult("银行卡识别结果");
        }
        return client.bankcard(imageBytes, new HashMap<>());
    }
    
    private JSONObject recognizeGeneralInternal(byte[] imageBytes, Boolean returnPosition) {
        if (client == null) {
            return mockOcrResult("通用文字识别结果");
        }
        HashMap<String, String> options = new HashMap<>();
        options.put("recognize_granularity", "big");
        options.put("language_type", "CHN_ENG");
        options.put("detect_direction", "true");
        options.put("detect_language", "true");
        options.put("vertexes_location", returnPosition ? "true" : "false");
        return client.basicGeneral(imageBytes, options);
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
                    "OCR识别失败：" + result.optString("error_msg"));
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