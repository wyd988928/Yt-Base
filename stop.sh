#!/bin/bash

# Yt-Base 项目停止脚本

echo "=== Yt-Base 易通基础服务平台停止脚本 ==="

# 停止服务函数
stop_service() {
    local service_name=$1
    local pid_file="logs/${service_name}.pid"
    
    if [ -f "$pid_file" ]; then
        local pid=$(cat $pid_file)
        if ps -p $pid > /dev/null 2>&1; then
            echo "正在停止 $service_name (PID: $pid)..."
            kill $pid
            
            # 等待进程结束
            local count=0
            while ps -p $pid > /dev/null 2>&1 && [ $count -lt 30 ]; do
                sleep 1
                count=$((count + 1))
            done
            
            if ps -p $pid > /dev/null 2>&1; then
                echo "强制停止 $service_name..."
                kill -9 $pid
            fi
            
            echo "✓ $service_name 已停止"
        else
            echo "✗ $service_name 进程不存在"
        fi
        rm -f $pid_file
    else
        echo "✗ 未找到 $service_name 的PID文件"
    fi
}

# 停止所有服务
echo "开始停止所有服务..."

stop_service "yt-base-storage"
stop_service "yt-base-push"
stop_service "yt-base-ocr"
stop_service "yt-base-gateway"

echo ""
echo "=== 所有服务已停止 ==="

# 清理临时文件
echo "清理临时文件..."
find . -name "*.pid" -delete
echo "清理完成！"