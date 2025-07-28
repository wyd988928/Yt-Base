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
@RequestMapping("/recognize")
@RequiredArgsConstructor
public class OcrController {
    
    private final OcrService ocrService;
    
    /**
     * 文字识别
     */
    @PostMapping("/text")
    public Result<OcrRecognitionResponse> recognize(@RequestBody @Validated OcrRecognitionRequest request) {
        OcrRecognitionResponse response = ocrService.recognize(request);
        return Result.success(response);
    }
    
    /**
     * 身份证识别
     */
    @PostMapping("/idcard")
    public Result<OcrRecognitionResponse> recognizeIdCard(@RequestBody @Validated OcrRecognitionRequest request) {
        OcrRecognitionResponse response = ocrService.recognizeIdCard(request.getParse());
        return Result.success(response);
    }
    
    /**
     * 营业执照识别
     */
    @PostMapping("/business-license")
    public Result<OcrRecognitionResponse> recognizeBusinessLicense(@RequestBody @Validated OcrRecognitionRequest request) {
        OcrRecognitionResponse response = ocrService.recognizeBusinessLicense(request.getParse());
        return Result.success(response);
    }
    
    /**
     * 银行卡识别
     */
    @PostMapping("/bank-card")
    public Result<OcrRecognitionResponse> recognizeBankCard(@RequestBody @Validated OcrRecognitionRequest request) {
        OcrRecognitionResponse response = ocrService.recognizeBankCard(request.getParse());
        return Result.success(response);
    }
    
    /**
     * 通用文字识别
     */
    @PostMapping("/general")
    public Result<OcrRecognitionResponse> recognizeGeneral(@RequestBody @Validated OcrRecognitionRequest request) {
        OcrRecognitionResponse response = ocrService.recognizeGeneral(request.getParse());
        return Result.success(response);
    }
}