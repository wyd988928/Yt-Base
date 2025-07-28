package com.yitong.base.ocr;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * OCR服务启动类
 */
@SpringBootApplication(scanBasePackages = "com.yitong.base")
@EnableDubbo
@EnableDiscoveryClient
public class OcrApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(OcrApplication.class, args);
    }
}