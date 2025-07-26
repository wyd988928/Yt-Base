package com.yitong.base.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 返回状态码枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode {
    
    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),
    
    /**
     * 失败
     */
    ERROR(500, "操作失败"),
    
    /**
     * 参数错误
     */
    PARAM_ERROR(400, "参数错误"),
    
    /**
     * 未授权
     */
    UNAUTHORIZED(401, "未授权"),
    
    /**
     * 禁止访问
     */
    FORBIDDEN(403, "禁止访问"),
    
    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),
    
    /**
     * 服务不可用
     */
    SERVICE_UNAVAILABLE(503, "服务不可用"),
    
    /**
     * OCR识别失败
     */
    OCR_RECOGNITION_FAILED(1001, "OCR识别失败"),
    
    /**
     * 推送失败
     */
    PUSH_FAILED(2001, "推送失败"),
    
    /**
     * 邮件发送失败
     */
    EMAIL_SEND_FAILED(2002, "邮件发送失败"),
    
    /**
     * 短信发送失败
     */
    SMS_SEND_FAILED(2003, "短信发送失败"),
    
    /**
     * 文件上传失败
     */
    FILE_UPLOAD_FAILED(3001, "文件上传失败"),
    
    /**
     * 文件下载失败
     */
    FILE_DOWNLOAD_FAILED(3002, "文件下载失败"),
    
    /**
     * 文件删除失败
     */
    FILE_DELETE_FAILED(3003, "文件删除失败");
    
    private final Integer code;
    private final String message;
}