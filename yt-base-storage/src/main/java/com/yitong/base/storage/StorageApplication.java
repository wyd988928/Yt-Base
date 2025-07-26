package com.yitong.base.storage;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 对象存储服务启动类
 */
@SpringBootApplication(scanBasePackages = "com.yitong.base")
@EnableDubbo
public class StorageApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(StorageApplication.class, args);
    }
}