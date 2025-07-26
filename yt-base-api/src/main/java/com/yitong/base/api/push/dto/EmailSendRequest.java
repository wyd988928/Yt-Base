package com.yitong.base.api.push.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 邮件发送请求DTO
 */
@Data
public class EmailSendRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 收件人邮箱列表
     */
    @NotEmpty(message = "收件人不能为空")
    private List<String> toEmails;
    
    /**
     * 抄送邮箱列表
     */
    private List<String> ccEmails;
    
    /**
     * 密送邮箱列表
     */
    private List<String> bccEmails;
    
    /**
     * 邮件主题
     */
    @NotBlank(message = "邮件主题不能为空")
    private String subject;
    
    /**
     * 邮件内容
     */
    @NotBlank(message = "邮件内容不能为空")
    private String content;
    
    /**
     * 是否HTML格式
     */
    private Boolean isHtml = false;
    
    /**
     * 附件列表
     */
    private List<EmailAttachment> attachments;
    
    /**
     * 模板参数
     */
    private Map<String, Object> templateParams;
    
    /**
     * 模板ID
     */
    private String templateId;
    
    @Data
    public static class EmailAttachment implements Serializable {
        
        private static final long serialVersionUID = 1L;
        
        /**
         * 附件名称
         */
        private String fileName;
        
        /**
         * 附件内容（Base64编码）
         */
        private String fileContent;
        
        /**
         * 附件类型
         */
        private String contentType;
    }
}