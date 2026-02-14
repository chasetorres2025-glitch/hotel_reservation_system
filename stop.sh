#!/bin/bash

echo "==================================="
echo "酒店管理系统 - 停止脚本"
echo "==================================="
echo ""

# 停止后端服务
echo "1. 停止后端服务..."
BACKEND_PID=$(lsof -ti:8080)
if [ ! -z "$BACKEND_PID" ]; then
    kill -9 $BACKEND_PID
    echo "   ✓ 后端服务已停止"
else
    echo "   - 后端服务未运行"
fi

# 停止前端服务
echo ""
echo "2. 停止前端服务..."
FRONTEND_PID=$(lsof -ti:3000)
if [ ! -z "$FRONTEND_PID" ]; then
    kill -9 $FRONTEND_PID
    echo "   ✓ 前端服务已停止"
else
    echo "   - 前端服务未运行"
fi

echo ""
echo "==================================="
echo "所有服务已停止"
echo "==================================="
