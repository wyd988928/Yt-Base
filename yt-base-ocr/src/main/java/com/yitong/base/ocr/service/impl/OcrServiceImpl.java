package com.yitong.base.ocr.service.impl;

import com.yitong.base.api.ocr.dto.OcrRecognitionRequest;
import com.yitong.base.api.ocr.dto.OcrRecognitionResponse;
import com.yitong.base.api.ocr.service.OcrService;
import com.yitong.base.common.exception.BusinessException;
import com.yitong.base.common.result.ResultCode;
import com.yitong.base.ocr.factory.OcrStrategyFactory;
import com.yitong.base.ocr.strategy.OcrStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;

/**
 * OCR服务实现类
 * 使用策略模式支持多种OCR服务提供商
 */
@Slf4j
@Service
@DubboService
public class OcrServiceImpl implements OcrService {
    
    @Autowired
    private OcrStrategyFactory ocrStrategyFactory;
    
    @Override
    public OcrRecognitionResponse recognize(OcrRecognitionRequest request) {
        try {
            byte[] imageBytes = Base64.getDecoder().decode(request.getImageBase64());
            OcrStrategy strategy = ocrStrategyFactory.getDefaultStrategy();
            
            log.info("使用OCR提供商: {} 进行识别，类型: {}", 
                    strategy.getStrategyName(), request.getRecognitionType());
            
            switch (request.getRecognitionType()) {
                case 1:
                    return strategy.recognizeIdCard(imageBytes);
                case 2:
                    return strategy.recognizeBusinessLicense(imageBytes);
                case 3:
                    return strategy.recognizeBankCard(imageBytes);
                case 4:
                    return strategy.recognizeGeneral(imageBytes, request.getReturnPosition());
                default:
                    throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "不支持的识别类型");
            }
            
        } catch (Exception e) {
            log.error("OCR识别失败", e);
            throw new BusinessException(ResultCode.OCR_RECOGNITION_FAILED);
        }
    }
    
    @Override
    public OcrRecognitionResponse recognizeIdCard(String imageBase64) {
        try {
            byte[] imageBytes = Base64.getDecoder().decode(imageBase64);
            OcrStrategy strategy = ocrStrategyFactory.getDefaultStrategy();
            return strategy.recognizeIdCard(imageBytes);
        } catch (Exception e) {
            log.error("身份证识别失败", e);
            throw new BusinessException(ResultCode.OCR_RECOGNITION_FAILED);
        }
    }
    
    @Override
    public OcrRecognitionResponse recognizeBusinessLicense(String imageBase64) {
        try {
            byte[] imageBytes = Base64.getDecoder().decode(imageBase64);
            OcrStrategy strategy = ocrStrategyFactory.getDefaultStrategy();
            return strategy.recognizeBusinessLicense(imageBytes);
        } catch (Exception e) {
            log.error("营业执照识别失败", e);
            throw new BusinessException(ResultCode.OCR_RECOGNITION_FAILED);
        }
    }
    
    @Override
    public OcrRecognitionResponse recognizeBankCard(String imageBase64) {
        try {
            byte[] imageBytes = Base64.getDecoder().decode(imageBase64);
            OcrStrategy strategy = ocrStrategyFactory.getDefaultStrategy();
            return strategy.recognizeBankCard(imageBytes);
        } catch (Exception e) {
            log.error("银行卡识别失败", e);
            throw new BusinessException(ResultCode.OCR_RECOGNITION_FAILED);
        }
    }
    
    @Override
    public OcrRecognitionResponse recognizeGeneral(String imageBase64) {
        try {
            byte[] imageBytes = Base64.getDecoder().decode(imageBase64);
            OcrStrategy strategy = ocrStrategyFactory.getDefaultStrategy();
            return strategy.recognizeGeneral(imageBytes, false);
        } catch (Exception e) {
            log.error("通用文字识别失败", e);
            throw new BusinessException(ResultCode.OCR_RECOGNITION_FAILED);
        }
    }
}