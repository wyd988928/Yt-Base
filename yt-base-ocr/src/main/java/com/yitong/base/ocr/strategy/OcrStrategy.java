package com.yitong.base.ocr.strategy;

import com.yitong.base.api.ocr.dto.OcrRecognitionRequest;
import com.yitong.base.api.ocr.dto.OcrRecognitionResponse;

/**
 * OCR策略接口
 * 定义所有OCR服务提供商需要实现的通用接口
 */
public interface OcrStrategy {
    
    /**
     * 获取策略名称
     * @return 策略名称
     */
    String getStrategyName();
    
    /**
     * 初始化OCR客户端
     */
    void initialize();
    
    /**
     * 身份证识别
     * @param imageBytes 图片字节数组
     * @return 识别结果
     */
    OcrRecognitionResponse recognizeIdCard(byte[] imageBytes);
    
    /**
     * 营业执照识别
     * @param imageBytes 图片字节数组
     * @return 识别结果
     */
    OcrRecognitionResponse recognizeBusinessLicense(byte[] imageBytes);
    
    /**
     * 银行卡识别
     * @param imageBytes 图片字节数组
     * @return 识别结果
     */
    OcrRecognitionResponse recognizeBankCard(byte[] imageBytes);
    
    /**
     * 通用文字识别
     * @param imageBytes 图片字节数组
     * @param returnPosition 是否返回位置信息
     * @return 识别结果
     */
    OcrRecognitionResponse recognizeGeneral(byte[] imageBytes, Boolean returnPosition);
    
    /**
     * 检查服务是否可用
     * @return 是否可用
     */
    boolean isAvailable();
}