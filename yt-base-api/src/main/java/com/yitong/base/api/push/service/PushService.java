package com.yitong.base.api.push.service;

import com.yitong.base.api.push.dto.EmailSendRequest;
import com.yitong.base.api.push.dto.PushResponse;
import com.yitong.base.api.push.dto.SmsSendRequest;

/**
 * 推送服务接口
 */
public interface PushService {
    
    /**
     * 发送邮件
     *
     * @param request 邮件发送请求
     * @return 发送结果
     */
    PushResponse sendEmail(EmailSendRequest request);
    
    /**
     * 发送短信
     *
     * @param request 短信发送请求
     * @return 发送结果
     */
    PushResponse sendSms(SmsSendRequest request);
    
    /**
     * 发送验证码短信
     *
     * @param phoneNumber 手机号
     * @param code 验证码
     * @return 发送结果
     */
    PushResponse sendVerificationCode(String phoneNumber, String code);
    
    /**
     * 批量发送邮件
     *
     * @param request 邮件发送请求
     * @return 发送结果
     */
    PushResponse batchSendEmail(EmailSendRequest request);
    
    /**
     * 批量发送短信
     *
     * @param request 短信发送请求
     * @return 发送结果
     */
    PushResponse batchSendSms(SmsSendRequest request);
}