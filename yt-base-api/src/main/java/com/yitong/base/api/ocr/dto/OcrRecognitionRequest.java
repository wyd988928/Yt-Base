package com.yitong.base.api.ocr.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * OCR识别请求DTO
 */
@Data
public class OcrRecognitionRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 图片Base64编码
     */
    @NotBlank(message = "图片内容不能为空")
    private String imageBase64;
    
    /**
     * 识别类型：1-身份证，2-营业执照，3-银行卡，4-通用文字识别
     */
    @NotNull(message = "识别类型不能为空")
    private Integer recognitionType;
    
    /**
     * 是否返回位置信息
     */
    private Boolean returnPosition = false;
    
    /**
     * 识别语言：zh-中文，en-英文
     */
    private String language = "zh";
}