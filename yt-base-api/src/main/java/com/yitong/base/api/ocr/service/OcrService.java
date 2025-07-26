package com.yitong.base.api.ocr.service;

import com.yitong.base.api.ocr.dto.OcrRecognitionRequest;
import com.yitong.base.api.ocr.dto.OcrRecognitionResponse;

/**
 * OCR服务接口
 */
public interface OcrService {
    
    /**
     * 文字识别
     *
     * @param request 识别请求
     * @return 识别结果
     */
    OcrRecognitionResponse recognize(OcrRecognitionRequest request);
    
    /**
     * 身份证识别
     *
     * @param imageBase64 图片Base64编码
     * @return 识别结果
     */
    OcrRecognitionResponse recognizeIdCard(String imageBase64);
    
    /**
     * 营业执照识别
     *
     * @param imageBase64 图片Base64编码
     * @return 识别结果
     */
    OcrRecognitionResponse recognizeBusinessLicense(String imageBase64);
    
    /**
     * 银行卡识别
     *
     * @param imageBase64 图片Base64编码
     * @return 识别结果
     */
    OcrRecognitionResponse recognizeBankCard(String imageBase64);
    
    /**
     * 通用文字识别
     *
     * @param imageBase64 图片Base64编码
     * @return 识别结果
     */
    OcrRecognitionResponse recognizeGeneral(String imageBase64);
}