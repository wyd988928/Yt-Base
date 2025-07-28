package com.yitong.base.ocr.controller;

import com.yitong.base.common.result.Result;
import com.yitong.base.ocr.factory.OcrStrategyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * OCR管理控制器
 * 提供OCR服务提供商的管理功能
 */
@Slf4j
@RestController
@RequestMapping("/management")
public class OcrManagementController {
    
    @Autowired
    private OcrStrategyFactory ocrStrategyFactory;
    
    /**
     * 获取OCR服务状态信息
     * @return 服务状态
     */
    @GetMapping("/status")
    public Result<Map<String, Object>> getOcrStatus() {
        Map<String, Object> status = new HashMap<>();
        
        // 当前默认提供商
        String defaultProvider = ocrStrategyFactory.getDefaultProviderName();
        status.put("defaultProvider", defaultProvider);
        
        // 所有可用提供商
        Set<String> availableProviders = ocrStrategyFactory.getAvailableProviders();
        status.put("availableProviders", availableProviders);
        
        // 各提供商的可用状态
        Map<String, Boolean> providerStatus = new HashMap<>();
        for (String provider : availableProviders) {
            providerStatus.put(provider, ocrStrategyFactory.isProviderAvailable(provider));
        }
        status.put("providerStatus", providerStatus);
        
        log.info("获取OCR服务状态: 默认提供商={}, 可用提供商={}", defaultProvider, availableProviders);
        
        return Result.success(status);
    }
    
    /**
     * 检查指定提供商是否可用
     * @param provider 提供商名称
     * @return 可用状态
     */
    @GetMapping("/check/{provider}")
    public Result<Map<String, Object>> checkProvider(@PathVariable String provider) {
        Map<String, Object> result = new HashMap<>();
        
        boolean isAvailable = ocrStrategyFactory.isProviderAvailable(provider);
        result.put("provider", provider);
        result.put("available", isAvailable);
        
        if (isAvailable) {
            result.put("message", "提供商可用");
        } else {
            result.put("message", "提供商不可用或配置不完整");
        }
        
        log.info("检查OCR提供商 '{}' 状态: {}", provider, isAvailable ? "可用" : "不可用");
        
        return Result.success(result);
    }
    
    /**
     * 获取所有可用的OCR提供商列表
     * @return 提供商列表
     */
    @GetMapping("/providers")
    public Result<Set<String>> getAvailableProviders() {
        Set<String> providers = ocrStrategyFactory.getAvailableProviders();
        log.info("获取可用OCR提供商列表: {}", providers);
        return Result.success(providers);
    }
}