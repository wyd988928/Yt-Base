package com.yitong.base.storage.service.impl;

import com.yitong.base.api.storage.dto.FileInfo;
import com.yitong.base.api.storage.dto.FileUploadRequest;
import com.yitong.base.api.storage.service.StorageService;
import com.yitong.base.common.exception.BusinessException;
import com.yitong.base.common.result.ResultCode;
import io.minio.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 对象存储服务实现类
 */
@Slf4j
@Service
@DubboService
public class StorageServiceImpl implements StorageService {
    
    @Value("${minio.endpoint:http://localhost:9000}")
    private String endpoint;
    
    @Value("${minio.access-key:minioadmin}")
    private String accessKey;
    
    @Value("${minio.secret-key:minioadmin}")
    private String secretKey;
    
    @Value("${minio.bucket-name:yt-base}")
    private String defaultBucketName;
    
    private MinioClient minioClient;
    
    @PostConstruct
    public void init() {
        try {
            minioClient = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();
            
            // 检查默认存储桶是否存在，不存在则创建
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(defaultBucketName)
                    .build());
            
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(defaultBucketName)
                        .build());
                log.info("创建默认存储桶: {}", defaultBucketName);
            }
            
        } catch (Exception e) {
            log.warn("MinIO初始化失败，将使用模拟模式: {}", e.getMessage());
            minioClient = null;
        }
    }
    
    @Override
    public FileInfo uploadFile(FileUploadRequest request) {
        try {
            byte[] fileBytes = Base64.getDecoder().decode(request.getFileContent());
            String fileId = UUID.randomUUID().toString();
            String bucketName = StringUtils.hasText(request.getBucketName()) ? 
                    request.getBucketName() : defaultBucketName;
            String objectName = StringUtils.hasText(request.getFilePath()) ? 
                    request.getFilePath() + "/" + fileId : fileId;
            
            if (minioClient != null) {
                // 上传文件到MinIO
                minioClient.putObject(PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(new ByteArrayInputStream(fileBytes), fileBytes.length, -1)
                        .contentType(request.getContentType())
                        .build());
            }
            
            // 构建文件信息
            FileInfo fileInfo = new FileInfo();
            fileInfo.setFileId(fileId);
            fileInfo.setFileName(request.getFileName());
            fileInfo.setFilePath(objectName);
            fileInfo.setFileUrl(getFileUrl(fileId, 3600L));
            fileInfo.setFileSize((long) fileBytes.length);
            fileInfo.setContentType(request.getContentType());
            fileInfo.setMd5(calculateMD5(fileBytes));
            fileInfo.setBucketName(bucketName);
            fileInfo.setUploadTime(System.currentTimeMillis());
            fileInfo.setTags(request.getTags());
            fileInfo.setDescription(request.getDescription());
            fileInfo.setAccessPermission(1); // 默认私有
            
            log.info("文件上传成功: {}", fileInfo.getFileId());
            return fileInfo;
            
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new BusinessException(ResultCode.FILE_UPLOAD_FAILED);
        }
    }
    
    @Override
    public String downloadFile(String fileId) {
        try {
            if (minioClient != null) {
                // 从MinIO下载文件
                GetObjectResponse response = minioClient.getObject(GetObjectArgs.builder()
                        .bucket(defaultBucketName)
                        .object(fileId)
                        .build());
                
                byte[] fileBytes = response.readAllBytes();
                return Base64.getEncoder().encodeToString(fileBytes);
            } else {
                // 模拟下载
                return Base64.getEncoder().encodeToString("模拟文件内容".getBytes());
            }
            
        } catch (Exception e) {
            log.error("文件下载失败: {}", fileId, e);
            throw new BusinessException(ResultCode.FILE_DOWNLOAD_FAILED);
        }
    }
    
    @Override
    public Boolean deleteFile(String fileId) {
        try {
            if (minioClient != null) {
                minioClient.removeObject(RemoveObjectArgs.builder()
                        .bucket(defaultBucketName)
                        .object(fileId)
                        .build());
            }
            
            log.info("文件删除成功: {}", fileId);
            return true;
            
        } catch (Exception e) {
            log.error("文件删除失败: {}", fileId, e);
            throw new BusinessException(ResultCode.FILE_DELETE_FAILED);
        }
    }
    
    @Override
    public FileInfo getFileInfo(String fileId) {
        try {
            if (minioClient != null) {
                StatObjectResponse stat = minioClient.statObject(StatObjectArgs.builder()
                        .bucket(defaultBucketName)
                        .object(fileId)
                        .build());
                
                FileInfo fileInfo = new FileInfo();
                fileInfo.setFileId(fileId);
                fileInfo.setFileName(fileId);
                fileInfo.setFilePath(fileId);
                fileInfo.setFileSize(stat.size());
                fileInfo.setContentType(stat.contentType());
                fileInfo.setBucketName(defaultBucketName);
                fileInfo.setUploadTime(stat.lastModified().toEpochSecond() * 1000);
                
                return fileInfo;
            } else {
                // 模拟文件信息
                FileInfo fileInfo = new FileInfo();
                fileInfo.setFileId(fileId);
                fileInfo.setFileName("模拟文件.txt");
                fileInfo.setFilePath(fileId);
                fileInfo.setFileSize(1024L);
                fileInfo.setContentType("text/plain");
                fileInfo.setBucketName(defaultBucketName);
                fileInfo.setUploadTime(System.currentTimeMillis());
                
                return fileInfo;
            }
            
        } catch (Exception e) {
            log.error("获取文件信息失败: {}", fileId, e);
            throw new BusinessException(ResultCode.ERROR.getCode(), "获取文件信息失败");
        }
    }
    
    @Override
    public String getFileUrl(String fileId, Long expireTime) {
        try {
            if (minioClient != null) {
                return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(defaultBucketName)
                        .object(fileId)
                        .expiry(expireTime.intValue(), TimeUnit.SECONDS)
                        .build());
            } else {
                return "http://localhost:8083/storage/api/files/" + fileId;
            }
            
        } catch (Exception e) {
            log.error("获取文件URL失败: {}", fileId, e);
            return null;
        }
    }
    
    @Override
    public List<FileInfo> batchUploadFiles(List<FileUploadRequest> requests) {
        List<FileInfo> fileInfos = new ArrayList<>();
        for (FileUploadRequest request : requests) {
            try {
                FileInfo fileInfo = uploadFile(request);
                fileInfos.add(fileInfo);
            } catch (Exception e) {
                log.error("批量上传文件失败: {}", request.getFileName(), e);
            }
        }
        return fileInfos;
    }
    
    @Override
    public Boolean batchDeleteFiles(List<String> fileIds) {
        boolean allSuccess = true;
        for (String fileId : fileIds) {
            try {
                deleteFile(fileId);
            } catch (Exception e) {
                log.error("批量删除文件失败: {}", fileId, e);
                allSuccess = false;
            }
        }
        return allSuccess;
    }
    
    @Override
    public FileInfo copyFile(String sourceFileId, String targetPath) {
        try {
            if (minioClient != null) {
                String targetObjectName = targetPath + "/" + UUID.randomUUID().toString();
                
                minioClient.copyObject(CopyObjectArgs.builder()
                        .bucket(defaultBucketName)
                        .object(targetObjectName)
                        .source(CopySource.builder()
                                .bucket(defaultBucketName)
                                .object(sourceFileId)
                                .build())
                        .build());
                
                return getFileInfo(targetObjectName);
            } else {
                // 模拟复制
                FileInfo fileInfo = getFileInfo(sourceFileId);
                fileInfo.setFileId(UUID.randomUUID().toString());
                fileInfo.setFilePath(targetPath);
                return fileInfo;
            }
            
        } catch (Exception e) {
            log.error("文件复制失败: {} -> {}", sourceFileId, targetPath, e);
            throw new BusinessException(ResultCode.ERROR.getCode(), "文件复制失败");
        }
    }
    
    @Override
    public Boolean moveFile(String fileId, String targetPath) {
        try {
            FileInfo copiedFile = copyFile(fileId, targetPath);
            deleteFile(fileId);
            return true;
            
        } catch (Exception e) {
            log.error("文件移动失败: {} -> {}", fileId, targetPath, e);
            throw new BusinessException(ResultCode.ERROR.getCode(), "文件移动失败");
        }
    }
    
    private String calculateMD5(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(data);
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }
}