package com.yitong.base.api.push.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 短信发送请求DTO
 */
@Data
public class SmsSendRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 手机号列表
     */
    @NotEmpty(message = "手机号不能为空")
    private List<@Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确") String> phoneNumbers;
    
    /**
     * 短信内容
     */
    @NotBlank(message = "短信内容不能为空")
    private String content;
    
    /**
     * 短信模板ID
     */
    private String templateId;
    
    /**
     * 模板参数
     */
    private Map<String, Object> templateParams;
    
    /**
     * 短信签名
     */
    private String signName;
    
    /**
     * 发送时间（定时发送）
     */
    private Long sendTime;
    
    /**
     * 短信类型：1-验证码，2-通知，3-营销
     */
    private Integer smsType = 2;
}