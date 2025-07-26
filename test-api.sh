#!/bin/bash

# API测试脚本

echo "=== Yt-Base API 测试脚本 ==="

BASE_URL="http://localhost:8080"

# 测试OCR服务
echo "测试OCR服务..."
echo "1. 通用文字识别"
curl -X POST "$BASE_URL/api/ocr/recognize/general" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "imageBase64=iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==" \
  | jq '.'

echo -e "\n"

# 测试推送服务
echo "测试推送服务..."
echo "1. 发送邮件"
curl -X POST "$BASE_URL/api/push/email/send" \
  -H "Content-Type: application/json" \
  -d '{
    "toEmails": ["test@example.com"],
    "subject": "测试邮件",
    "content": "这是一封测试邮件",
    "isHtml": false
  }' \
  | jq '.'

echo -e "\n"

echo "2. 发送短信"
curl -X POST "$BASE_URL/api/push/sms/send" \
  -H "Content-Type: application/json" \
  -d '{
    "phoneNumbers": ["13800138000"],
    "content": "这是一条测试短信"
  }' \
  | jq '.'

echo -e "\n"

# 测试存储服务
echo "测试存储服务..."
echo "1. 上传文件"
curl -X POST "$BASE_URL/api/storage/upload" \
  -H "Content-Type: application/json" \
  -d '{
    "fileName": "test.txt",
    "fileContent": "VGhpcyBpcyBhIHRlc3QgZmlsZQ==",
    "contentType": "text/plain"
  }' \
  | jq '.'

echo -e "\n"
echo "=== API测试完成 ==="