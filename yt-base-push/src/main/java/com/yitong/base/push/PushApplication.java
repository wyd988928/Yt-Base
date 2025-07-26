package com.yitong.base.push;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 推送服务启动类
 */
@SpringBootApplication(scanBasePackages = "com.yitong.base")
@EnableDubbo
public class PushApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(PushApplication.class, args);
    }
}