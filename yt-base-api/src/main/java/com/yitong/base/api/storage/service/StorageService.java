package com.yitong.base.api.storage.service;

import com.yitong.base.api.storage.dto.FileInfo;
import com.yitong.base.api.storage.dto.FileUploadRequest;

import java.util.List;

/**
 * 对象存储服务接口
 */
public interface StorageService {
    
    /**
     * 上传文件
     *
     * @param request 上传请求
     * @return 文件信息
     */
    FileInfo uploadFile(FileUploadRequest request);
    
    /**
     * 下载文件
     *
     * @param fileId 文件ID
     * @return 文件内容（Base64编码）
     */
    String downloadFile(String fileId);
    
    /**
     * 删除文件
     *
     * @param fileId 文件ID
     * @return 是否成功
     */
    Boolean deleteFile(String fileId);
    
    /**
     * 获取文件信息
     *
     * @param fileId 文件ID
     * @return 文件信息
     */
    FileInfo getFileInfo(String fileId);
    
    /**
     * 获取文件访问URL
     *
     * @param fileId 文件ID
     * @param expireTime 过期时间（秒）
     * @return 访问URL
     */
    String getFileUrl(String fileId, Long expireTime);
    
    /**
     * 批量上传文件
     *
     * @param requests 上传请求列表
     * @return 文件信息列表
     */
    List<FileInfo> batchUploadFiles(List<FileUploadRequest> requests);
    
    /**
     * 批量删除文件
     *
     * @param fileIds 文件ID列表
     * @return 是否成功
     */
    Boolean batchDeleteFiles(List<String> fileIds);
    
    /**
     * 复制文件
     *
     * @param sourceFileId 源文件ID
     * @param targetPath 目标路径
     * @return 新文件信息
     */
    FileInfo copyFile(String sourceFileId, String targetPath);
    
    /**
     * 移动文件
     *
     * @param fileId 文件ID
     * @param targetPath 目标路径
     * @return 是否成功
     */
    Boolean moveFile(String fileId, String targetPath);
}