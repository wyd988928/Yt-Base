package com.yitong.base.api.storage.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 文件上传请求DTO
 */
@Data
public class FileUploadRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 文件名
     */
    @NotBlank(message = "文件名不能为空")
    private String fileName;
    
    /**
     * 文件内容（Base64编码）
     */
    @NotBlank(message = "文件内容不能为空")
    private String fileContent;
    
    /**
     * 文件类型
     */
    private String contentType;
    
    /**
     * 存储桶名称
     */
    private String bucketName;
    
    /**
     * 文件路径
     */
    private String filePath;
    
    /**
     * 是否覆盖同名文件
     */
    private Boolean overwrite = false;
    
    /**
     * 文件标签
     */
    private String tags;
    
    /**
     * 文件描述
     */
    private String description;
}