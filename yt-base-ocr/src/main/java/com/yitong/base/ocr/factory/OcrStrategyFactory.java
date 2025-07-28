package com.yitong.base.ocr.factory;

import com.yitong.base.common.exception.BusinessException;
import com.yitong.base.common.result.ResultCode;
import com.yitong.base.ocr.strategy.OcrStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * OCR策略工厂
 * 负责根据配置创建和管理OCR策略实例
 */
@Slf4j
@Component
public class OcrStrategyFactory {
    
    @Value("${ocr.provider:baidu}")
    private String defaultProvider;
    
    @Autowired
    private List<OcrStrategy> ocrStrategies;
    
    private final Map<String, OcrStrategy> strategyMap = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void init() {
        // 注册所有OCR策略
        for (OcrStrategy strategy : ocrStrategies) {
            strategyMap.put(strategy.getStrategyName(), strategy);
            log.info("注册OCR策略: {}, 可用状态: {}", 
                    strategy.getStrategyName(), strategy.isAvailable());
        }
        
        // 验证默认提供商是否存在
        if (!strategyMap.containsKey(defaultProvider)) {
            log.warn("默认OCR提供商 '{}' 不存在，可用提供商: {}", 
                    defaultProvider, strategyMap.keySet());
        } else {
            log.info("默认OCR提供商设置为: {}", defaultProvider);
        }
    }
    
    /**
     * 获取默认OCR策略
     * @return OCR策略实例
     */
    public OcrStrategy getDefaultStrategy() {
        return getStrategy(defaultProvider);
    }
    
    /**
     * 根据提供商名称获取OCR策略
     * @param providerName 提供商名称
     * @return OCR策略实例
     */
    public OcrStrategy getStrategy(String providerName) {
        if (providerName == null || providerName.trim().isEmpty()) {
            providerName = defaultProvider;
        }
        
        OcrStrategy strategy = strategyMap.get(providerName.toLowerCase());
        if (strategy == null) {
            log.error("未找到OCR提供商: {}, 可用提供商: {}", 
                    providerName, strategyMap.keySet());
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), 
                    "不支持的OCR提供商: " + providerName);
        }
        
        if (!strategy.isAvailable()) {
            log.warn("OCR提供商 '{}' 当前不可用，尝试使用其他可用提供商", providerName);
            
            // 尝试找到一个可用的策略
            for (OcrStrategy availableStrategy : strategyMap.values()) {
                if (availableStrategy.isAvailable()) {
                    log.info("使用备用OCR提供商: {}", availableStrategy.getStrategyName());
                    return availableStrategy;
                }
            }
            
            throw new BusinessException(ResultCode.OCR_RECOGNITION_FAILED.getCode(), 
                    "所有OCR服务提供商都不可用");
        }
        
        return strategy;
    }
    
    /**
     * 获取所有可用的OCR策略名称
     * @return 策略名称列表
     */
    public java.util.Set<String> getAvailableProviders() {
        return strategyMap.keySet();
    }
    
    /**
     * 检查指定提供商是否可用
     * @param providerName 提供商名称
     * @return 是否可用
     */
    public boolean isProviderAvailable(String providerName) {
        OcrStrategy strategy = strategyMap.get(providerName);
        return strategy != null && strategy.isAvailable();
    }
    
    /**
     * 获取当前默认提供商名称
     * @return 默认提供商名称
     */
    public String getDefaultProviderName() {
        return defaultProvider;
    }
}