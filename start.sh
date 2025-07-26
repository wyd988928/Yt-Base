#!/bin/bash

# Yt-Base 项目启动脚本

echo "=== Yt-Base 易通基础服务平台启动脚本 ==="

# 检查Java环境
if ! command -v java &> /dev/null; then
    echo "错误: 未找到Java环境，请安装JDK 17或更高版本"
    exit 1
fi

# 检查Maven环境
if ! command -v mvn &> /dev/null; then
    echo "错误: 未找到Maven环境，请安装Maven 3.8或更高版本"
    exit 1
fi

# 编译项目
echo "正在编译项目..."
mvn clean compile -DskipTests

if [ $? -ne 0 ]; then
    echo "错误: 项目编译失败"
    exit 1
fi

echo "项目编译成功！"

# 启动服务函数
start_service() {
    local service_name=$1
    local service_dir=$2
    local port=$3
    
    echo "正在启动 $service_name (端口: $port)..."
    
    cd $service_dir
    nohup mvn spring-boot:run > ../logs/${service_name}.log 2>&1 &
    local pid=$!
    echo $pid > ../logs/${service_name}.pid
    
    echo "$service_name 启动中，PID: $pid"
    cd ..
}

# 创建日志目录
mkdir -p logs

# 启动服务
echo "开始启动各个服务..."

# 启动网关
start_service "yt-base-gateway" "yt-base-gateway" "8080"
sleep 5

# 启动OCR服务
start_service "yt-base-ocr" "yt-base-ocr" "8081"
sleep 5

# 启动推送服务
start_service "yt-base-push" "yt-base-push" "8082"
sleep 5

# 启动存储服务
start_service "yt-base-storage" "yt-base-storage" "8083"
sleep 5

echo ""
echo "=== 所有服务启动完成 ==="
echo "网关地址: http://localhost:8080"
echo "OCR服务: http://localhost:8081"
echo "推送服务: http://localhost:8082"
echo "存储服务: http://localhost:8083"
echo ""
echo "查看日志: tail -f logs/服务名.log"
echo "停止服务: ./stop.sh"
echo ""
echo "等待服务完全启动..."
sleep 10

# 检查服务状态
check_service() {
    local service_name=$1
    local port=$2
    
    if curl -s http://localhost:$port/actuator/health > /dev/null 2>&1; then
        echo "✓ $service_name 运行正常"
    else
        echo "✗ $service_name 可能未正常启动，请检查日志"
    fi
}

echo "检查服务状态..."
check_service "网关服务" "8080"
check_service "OCR服务" "8081"
check_service "推送服务" "8082"
check_service "存储服务" "8083"

echo ""
echo "启动完成！"