package com.yitong.base.push.service.impl;

import com.yitong.base.api.push.dto.EmailSendRequest;
import com.yitong.base.api.push.dto.PushResponse;
import com.yitong.base.api.push.dto.SmsSendRequest;
import com.yitong.base.api.push.service.PushService;
import com.yitong.base.common.exception.BusinessException;
import com.yitong.base.common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import jakarta.mail.internet.MimeMessage;
import java.util.UUID;

/**
 * 推送服务实现类
 */
@Slf4j
@Service
@DubboService
@RequiredArgsConstructor
public class PushServiceImpl implements PushService {
    
    private final JavaMailSender mailSender;
    
    @Override
    public PushResponse sendEmail(EmailSendRequest request) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            // 设置发件人
            helper.setFrom("noreply@yitong.com");
            
            // 设置收件人
            if (!CollectionUtils.isEmpty(request.getToEmails())) {
                helper.setTo(request.getToEmails().toArray(new String[0]));
            }
            
            // 设置抄送
            if (!CollectionUtils.isEmpty(request.getCcEmails())) {
                helper.setCc(request.getCcEmails().toArray(new String[0]));
            }
            
            // 设置密送
            if (!CollectionUtils.isEmpty(request.getBccEmails())) {
                helper.setBcc(request.getBccEmails().toArray(new String[0]));
            }
            
            // 设置主题和内容
            helper.setSubject(request.getSubject());
            helper.setText(request.getContent(), request.getIsHtml());
            
            // 添加附件
            if (!CollectionUtils.isEmpty(request.getAttachments())) {
                for (EmailSendRequest.EmailAttachment attachment : request.getAttachments()) {
                    byte[] fileBytes = java.util.Base64.getDecoder().decode(attachment.getFileContent());
                    helper.addAttachment(attachment.getFileName(), 
                            new jakarta.activation.DataSource() {
                                @Override
                                public java.io.InputStream getInputStream() {
                                    return new java.io.ByteArrayInputStream(fileBytes);
                                }
                                
                                @Override
                                public java.io.OutputStream getOutputStream() {
                                    throw new UnsupportedOperationException();
                                }
                                
                                @Override
                                public String getContentType() {
                                    return attachment.getContentType();
                                }
                                
                                @Override
                                public String getName() {
                                    return attachment.getFileName();
                                }
                            });
                }
            }
            
            mailSender.send(message);
            
            String messageId = UUID.randomUUID().toString();
            log.info("邮件发送成功，messageId: {}", messageId);
            
            return PushResponse.success(messageId);
            
        } catch (Exception e) {
            log.error("邮件发送失败", e);
            throw new BusinessException(ResultCode.EMAIL_SEND_FAILED);
        }
    }
    
    @Override
    public PushResponse sendSms(SmsSendRequest request) {
        try {
            // 这里使用模拟实现，实际项目中需要集成真实的短信服务商
            log.info("发送短信到: {}, 内容: {}", request.getPhoneNumbers(), request.getContent());
            
            String messageId = UUID.randomUUID().toString();
            
            PushResponse response = PushResponse.success(messageId);
            response.setSuccessCount(request.getPhoneNumbers().size());
            response.setFailCount(0);
            
            return response;
            
        } catch (Exception e) {
            log.error("短信发送失败", e);
            throw new BusinessException(ResultCode.SMS_SEND_FAILED);
        }
    }
    
    @Override
    public PushResponse sendVerificationCode(String phoneNumber, String code) {
        SmsSendRequest request = new SmsSendRequest();
        request.setPhoneNumbers(java.util.Arrays.asList(phoneNumber));
        request.setContent("您的验证码是：" + code + "，5分钟内有效。");
        request.setSmsType(1);
        
        return sendSms(request);
    }
    
    @Override
    public PushResponse batchSendEmail(EmailSendRequest request) {
        return sendEmail(request);
    }
    
    @Override
    public PushResponse batchSendSms(SmsSendRequest request) {
        return sendSms(request);
    }
}