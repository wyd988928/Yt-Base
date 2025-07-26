package com.yitong.base.api.push.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 推送响应DTO
 */
@Data
public class PushResponse implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 是否成功
     */
    private Boolean success;
    
    /**
     * 消息ID
     */
    private String messageId;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 发送时间
     */
    private Long sendTime;
    
    /**
     * 成功数量
     */
    private Integer successCount;
    
    /**
     * 失败数量
     */
    private Integer failCount;
    
    public static PushResponse success(String messageId) {
        PushResponse response = new PushResponse();
        response.setSuccess(true);
        response.setMessageId(messageId);
        response.setSendTime(System.currentTimeMillis());
        return response;
    }
    
    public static PushResponse fail(String errorMessage) {
        PushResponse response = new PushResponse();
        response.setSuccess(false);
        response.setErrorMessage(errorMessage);
        response.setSendTime(System.currentTimeMillis());
        return response;
    }
}