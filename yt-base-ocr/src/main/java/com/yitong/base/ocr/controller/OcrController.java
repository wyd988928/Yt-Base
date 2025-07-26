package com.yitong.base.ocr.controller;

import com.yitong.base.api.ocr.dto.OcrRecognitionRequest;
import com.yitong.base.api.ocr.dto.OcrRecognitionResponse;
import com.yitong.base.api.ocr.service.OcrService;
import com.yitong.base.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * OCR控制器
 */
@RestController
@RequestMapping("/api/ocr")
@RequiredArgsConstructor
public class OcrController {
    
    private final OcrService ocrService;
    
    /**
     * 文字识别
     */
    @PostMapping("/recognize")
    public Result<OcrRecognitionResponse> recognize(@RequestBody @Validated OcrRecognitionRequest request) {
        OcrRecognitionResponse response = ocrService.recognize(request);
        return Result.success(response);
    }
    
    /**
     * 身份证识别
     */
    @PostMapping("/recognize/idcard")
    public Result<OcrRecognitionResponse> recognizeIdCard(@RequestParam String imageBase64) {
        OcrRecognitionResponse response = ocrService.recognizeIdCard(imageBase64);
        return Result.success(response);
    }
    
    /**
     * 营业执照识别
     */
    @PostMapping("/recognize/business-license")
    public Result<OcrRecognitionResponse> recognizeBusinessLicense(@RequestParam String imageBase64) {
        OcrRecognitionResponse response = ocrService.recognizeBusinessLicense(imageBase64);
        return Result.success(response);
    }
    
    /**
     * 银行卡识别
     */
    @PostMapping("/recognize/bank-card")
    public Result<OcrRecognitionResponse> recognizeBankCard(@RequestParam String imageBase64) {
        OcrRecognitionResponse response = ocrService.recognizeBankCard(imageBase64);
        return Result.success(response);
    }
    
    /**
     * 通用文字识别
     */
    @PostMapping("/recognize/general")
    public Result<OcrRecognitionResponse> recognizeGeneral(@RequestParam String imageBase64) {
        OcrRecognitionResponse response = ocrService.recognizeGeneral(imageBase64);
        return Result.success(response);
    }
}