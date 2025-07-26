package com.yitong.base.api.storage.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 文件信息DTO
 */
@Data
public class FileInfo implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 文件ID
     */
    private String fileId;
    
    /**
     * 文件名
     */
    private String fileName;
    
    /**
     * 文件路径
     */
    private String filePath;
    
    /**
     * 文件URL
     */
    private String fileUrl;
    
    /**
     * 文件大小（字节）
     */
    private Long fileSize;
    
    /**
     * 文件类型
     */
    private String contentType;
    
    /**
     * 文件MD5
     */
    private String md5;
    
    /**
     * 存储桶名称
     */
    private String bucketName;
    
    /**
     * 上传时间
     */
    private Long uploadTime;
    
    /**
     * 文件标签
     */
    private String tags;
    
    /**
     * 文件描述
     */
    private String description;
    
    /**
     * 访问权限：1-私有，2-公开读，3-公开读写
     */
    private Integer accessPermission;
}