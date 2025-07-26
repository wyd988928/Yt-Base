-- 创建数据库
CREATE DATABASE IF NOT EXISTS `yt_base_ocr` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `yt_base_push` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `yt_base_storage` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `nacos` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用OCR数据库
USE `yt_base_ocr`;

-- OCR识别记录表
CREATE TABLE IF NOT EXISTS `ocr_recognition_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `recognition_id` varchar(64) NOT NULL COMMENT '识别ID',
  `recognition_type` int NOT NULL COMMENT '识别类型：1-身份证，2-营业执照，3-银行卡，4-通用文字识别',
  `image_url` varchar(500) DEFAULT NULL COMMENT '图片URL',
  `recognition_result` text COMMENT '识别结果',
  `confidence` decimal(5,4) DEFAULT NULL COMMENT '识别置信度',
  `duration` bigint DEFAULT NULL COMMENT '识别耗时（毫秒）',
  `status` tinyint DEFAULT '1' COMMENT '状态：1-成功，0-失败',
  `error_message` varchar(500) DEFAULT NULL COMMENT '错误信息',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_recognition_id` (`recognition_id`),
  KEY `idx_recognition_type` (`recognition_type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='OCR识别记录表';

-- 使用推送数据库
USE `yt_base_push`;

-- 邮件发送记录表
CREATE TABLE IF NOT EXISTS `email_send_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `message_id` varchar(64) NOT NULL COMMENT '消息ID',
  `to_emails` text NOT NULL COMMENT '收件人邮箱列表',
  `cc_emails` text COMMENT '抄送邮箱列表',
  `bcc_emails` text COMMENT '密送邮箱列表',
  `subject` varchar(200) NOT NULL COMMENT '邮件主题',
  `content` longtext NOT NULL COMMENT '邮件内容',
  `is_html` tinyint DEFAULT '0' COMMENT '是否HTML格式',
  `template_id` varchar(64) DEFAULT NULL COMMENT '模板ID',
  `send_status` tinyint DEFAULT '0' COMMENT '发送状态：0-待发送，1-发送成功，2-发送失败',
  `error_message` varchar(500) DEFAULT NULL COMMENT '错误信息',
  `send_time` datetime DEFAULT NULL COMMENT '发送时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_message_id` (`message_id`),
  KEY `idx_send_status` (`send_status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='邮件发送记录表';

-- 短信发送记录表
CREATE TABLE IF NOT EXISTS `sms_send_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `message_id` varchar(64) NOT NULL COMMENT '消息ID',
  `phone_numbers` text NOT NULL COMMENT '手机号列表',
  `content` varchar(500) NOT NULL COMMENT '短信内容',
  `template_id` varchar(64) DEFAULT NULL COMMENT '模板ID',
  `sms_type` tinyint DEFAULT '2' COMMENT '短信类型：1-验证码，2-通知，3-营销',
  `send_status` tinyint DEFAULT '0' COMMENT '发送状态：0-待发送，1-发送成功，2-发送失败',
  `success_count` int DEFAULT '0' COMMENT '成功数量',
  `fail_count` int DEFAULT '0' COMMENT '失败数量',
  `error_message` varchar(500) DEFAULT NULL COMMENT '错误信息',
  `send_time` datetime DEFAULT NULL COMMENT '发送时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_message_id` (`message_id`),
  KEY `idx_send_status` (`send_status`),
  KEY `idx_sms_type` (`sms_type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='短信发送记录表';

-- 使用存储数据库
USE `yt_base_storage`;

-- 文件信息表
CREATE TABLE IF NOT EXISTS `file_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `file_id` varchar(64) NOT NULL COMMENT '文件ID',
  `file_name` varchar(255) NOT NULL COMMENT '文件名',
  `file_path` varchar(500) NOT NULL COMMENT '文件路径',
  `file_url` varchar(500) DEFAULT NULL COMMENT '文件URL',
  `file_size` bigint NOT NULL COMMENT '文件大小（字节）',
  `content_type` varchar(100) DEFAULT NULL COMMENT '文件类型',
  `md5` varchar(32) DEFAULT NULL COMMENT '文件MD5',
  `bucket_name` varchar(100) NOT NULL COMMENT '存储桶名称',
  `tags` varchar(200) DEFAULT NULL COMMENT '文件标签',
  `description` varchar(500) DEFAULT NULL COMMENT '文件描述',
  `access_permission` tinyint DEFAULT '1' COMMENT '访问权限：1-私有，2-公开读，3-公开读写',
  `upload_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_file_id` (`file_id`),
  KEY `idx_bucket_name` (`bucket_name`),
  KEY `idx_content_type` (`content_type`),
  KEY `idx_upload_time` (`upload_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件信息表';