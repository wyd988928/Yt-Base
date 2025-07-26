package com.yitong.base.common.exception;

import com.yitong.base.common.result.ResultCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 业务异常类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 错误码
     */
    private Integer code;
    
    /**
     * 错误信息
     */
    private String message;
    
    public BusinessException() {
        super();
    }
    
    public BusinessException(String message) {
        super(message);
        this.message = message;
        this.code = ResultCode.ERROR.getCode();
    }
    
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
    
    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
        this.code = ResultCode.ERROR.getCode();
    }
}